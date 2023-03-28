package nju.researchfun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import nju.researchfun.config.WXConfig;
import nju.researchfun.constant.MessageState;
import nju.researchfun.constant.MessageType;
import nju.researchfun.constant.UserStatus;
import nju.researchfun.constant.UserType;
import nju.researchfun.entity.*;
import nju.researchfun.entity.ResearchGroup;
import nju.researchfun.entity.user.UserOpenId;
import nju.researchfun.entity.user.User_Group;
import nju.researchfun.entity.user.Student;
import nju.researchfun.entity.user.Teacher;
import nju.researchfun.entity.user.User;
import nju.researchfun.entity.user.User_Password;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.NotFoundException;
import nju.researchfun.exception.ResearchFunException;
import nju.researchfun.mapper.UserMapper;
import nju.researchfun.mapper.User_GroupMapper;
import nju.researchfun.mapper.User_PasswordMapper;
import nju.researchfun.service.*;
import nju.researchfun.utils.TokenUtil;
import nju.researchfun.vo.researchgroup.GroupInfoWithoutId;
import nju.researchfun.vo.response.ResponseVO;
import nju.researchfun.vo.researchgroup.ResearchGroupExtremelySimpleInfo;
import nju.researchfun.vo.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private static final String APPID = "wxa74a307165e06d4b";
    private static final String APPSECRET = "ff4c7110b48715c37ef76a2ee814a2ae";
    @Autowired
    private UserOpenIdService userOpenIdService;

    @Value(value = "${user.default-portrait-url}")
    private String defaultPortraitUrl;
    @Value(value = "${research-group.direction-separator}")
    private String directionSep;
    @Value(value = "${localhost}")
    private String localhost;


    @Autowired
    private UserMapper userMapper;
    @Autowired
    private User_GroupMapper user_groupMapper;
    @Autowired
    private User_PasswordMapper user_passwordMapper;

    @Autowired
    private StudentService studentService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ResearchGroupService researchGroupService;
    @Autowired
    private MailService mailService;
    @Autowired
    private CheckCodeService checkCodeService;
    @Autowired
    private WXConfig wxConfig;


    @Override
    public LoginRet login(LoginForm loginForm) {
        //checkCode(session, loginForm.getCheck());
        //todo 改回来
        checkCodeService.checkCode(loginForm.getSessionId(), loginForm.getCheck());

        String username = loginForm.getUsername(), password = loginForm.getPassword();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);

        User user;
        try {//找到的用户名多于一条
            user = userMapper.selectOne(wrapper);
        } catch (Exception e) {
            throw new BadRequestException("该用户名已被使用！");
        }
        if (user == null)
            throw new BadRequestException("该用户名尚未注册！");
        if (user.getStatus() == UserStatus.UNACTIVATED)
            throw new BadRequestException("该用户尚未激活！请激活后登录！");


        //todo 要换成 security 框架的方法

        //     if (!passwordEncoder.matches(password, user.getPassword()))
        //        throw new BadRequestException("密码错误！");
        if (password == null || !password.equals(user.getPassword()))
            throw new BadRequestException("密码错误！");


        // 获取userType
        UserType userType;
        if ("STUDENT".equals(user.getUserType())) {
            userType = UserType.STUDENT;
        } else if ("TEACHER".equals(user.getUserType())) {
            userType = UserType.TEACHER;
        } else {
            throw new ResearchFunException("id为" + user.getId() + "的用户无法在用户类型中找到！");
        }
        if (loginForm.getCode() != null) {
            String openid = null;
            WeChatSessionModel weChatSessionModel;
            //微信服务器的接口路径
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID + "&secret=" + APPSECRET + "&js_code=" + loginForm.getCode() + "&grant_type=authorization_code";
            RestTemplate restTemplate = new RestTemplate();
            //进行网络请求,访问微信服务器接口
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            //根据返回值进行后续操作

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String sessionData = responseEntity.getBody();
                Gson gson = new Gson();
                //将json字符串转化为实体类;
                weChatSessionModel = gson.fromJson(sessionData, WeChatSessionModel.class);
                //获取用户的唯一标识
                openid = weChatSessionModel.getOpenid();
            }
            //TODO 这里我塞进去了
            userOpenIdService.insert(new UserOpenId(user.getId(), openid));

            UserOpenId userOpenId = userOpenIdService.checkByUserId(user.getId());
            boolean is_bound = userOpenId != null;
            return LoginRet.builder()
                    .userId(user.getId())
                    .userType(userType)
                    .token(TokenUtil.sign(username))
                    .isBound(is_bound)
                    .build();
        }

        return LoginRet.builder()
                .userId(user.getId())
                .userType(userType)
                .token(TokenUtil.sign(username))
                .build();

    }


    /**
     * @return 不为空 则存在
     */
    @Override
    public Boolean existsTheUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);

        User user = userMapper.selectOne(wrapper);//如果能找到，说明用户名已经存在
        return user != null;
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null)
            throw new BadRequestException("给定的用户id不能为null");
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new NotFoundException("不存在id为" + userId + "的用户！");
        return user;
    }

    @Override
    //@Transactional //todo 不开启事务 太慢了
    public void register(RegisterForm registerForm) {
        //checkCode(session, registerForm.getCheck());//验证码
        checkCodeService.checkCode(registerForm.getSessionId(), registerForm.getCheck());

        if (existsTheUsername(registerForm.getUsername()))
            throw new BadRequestException("该用户名已经存在！");
        if (exitsTheEmail(registerForm.getEmail())) //邮箱唯一
            throw new BadRequestException("该邮箱已被注册！");

        User user = registerForm.toUser();
        user.setPortrait(defaultPortraitUrl);
        //TODO 最后要用回 springsecurity
        //User.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setPassword(registerForm.getPassword());
        user.setCode(UUID.randomUUID().toString().replace("-", ""));//设置唯一激活码
        user.setStatus(UserStatus.UNACTIVATED);

        switch (registerForm.getUserType()) {//要操作两张表
            case STUDENT:
                userMapper.insert(user);
                studentService.addOneStudent(Student.builder().uid(user.getId()).build());
                break;
            case TEACHER:
                userMapper.insert(user);
                teacherService.addOneTeacher(Teacher.builder().uid(user.getId()).build());
                break;
            default:
                throw new BadRequestException("非法的用户类型！");
        }
        if (registerForm.getCode() != null) {
            String openid = null;
            WeChatSessionModel weChatSessionModel;
            //微信服务器的接口路径
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID + "&secret=" + APPSECRET + "&js_code=" + registerForm.getCode() + "&grant_type=authorization_code";
            RestTemplate restTemplate = new RestTemplate();
            //进行网络请求,访问微信服务器接口
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            //根据返回值进行后续操作

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String sessionData = responseEntity.getBody();
                Gson gson = new Gson();
                //将json字符串转化为实体类;
                weChatSessionModel = gson.fromJson(sessionData, WeChatSessionModel.class);
                //获取用户的唯一标识
                openid = weChatSessionModel.getOpenid();
            }
            userOpenIdService.insert(new UserOpenId(user.getId(), openid));
        }
        //发邮件
        String text;
        try {//todo 前端页面的实现？
            text = "<a href='" + localhost + "/user/active?code="
                    + user.getCode() + "'>点击激活【研坊学术研究管理系统】</a>";
            mailService.sendHtmlMail(user.getEmail(), "激活邮件", text);
        } catch (MessagingException e) {
            throw new BadRequestException("注册失败！请稍后再试");
        }

    }

    @Override
    public UserIdAndUserType getUserIdAndUserTypeByUserName(String username) {
        if (!existsTheUsername(username))
            throw new BadRequestException("不存在用户名为“" + username + "”的用户！");

        UserIdAndUserType res = new UserIdAndUserType();

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);

        User user = userMapper.selectOne(wrapper);
        Long userId = user.getId();
        res.setUserId(userId);

        if ("STUDENT".equals(user.getUserType())) {
            res.setUserType(UserType.STUDENT);
        } else if ("TEACHER".equals(user.getUserType())) {
            res.setUserType(UserType.TEACHER);
        } else {
            throw new ResearchFunException("id为" + userId + "的用户无法在用户类型中找到！");
        }
        return res;
    }


    @Override
    public UserSimpleInfo getUserSimpleInfoById(Long userId) {
        User user = getUserById(userId);
        return UserSimpleInfo.builder()
                .userId(userId)
                .username(user.getUsername())
                .trueName(user.getTrueName())
                .portrait(user.getPortrait())
                .build();
    }

    /**
     * 根据研究组 id查研究组——用户关系表，查出研究组里的所有成员
     * 多对多 多表查询
     */
    @Override
    public List<UserSimpleInfo> getAllMemberSimpleInfosInTheGroup(Long groupId) {
        List<User> users = getAllMembersInTheGroup(groupId);
        List<UserSimpleInfo> res = new ArrayList<>(users.size());
        for (User user : users)
            res.add(user.toUserSimpleInfo());
        return res;
    }

    /**
     * 根据研究组id查研究组——用户关系表，查出研究组里的所有成员
     * 多对多 多表查询
     */
    @Override
    public List<User> getAllMembersInTheGroup(Long groupId) {
        return userMapper.findByGroupId(groupId);
    }

    /**
     * 删之前将所有当前组id是这个组的全置为 null
     * 要删干净 研究组——用户关系表里和 gid相关的数据全部删掉
     * 研究组表也要删掉
     * 其实论文库、评论、日程等里面的东西也要删掉 但是懒得搞了 hhhhhhhhhh
     */
    @Override
    @Transactional
    public void deleteResearchGroup(Long userId, Long groupId) {
        if (!researchGroupService.getGroupCreator(groupId).equals(userId))
            throw new BadRequestException("只有研究组的创建者可以删除研究组");

        String groupName = researchGroupService.getName(groupId);

        // 向研究组中所有的成员发送消息提醒
        List<User> users = userMapper.findByGroupId(groupId);
        for (User u : users) {
            Long id = u.getId();
            if (!id.equals(userId)) {
                messageService.sendMessage(userId, id, "研究组" + groupName + "已解散");
            }
        }

        //目前在这个组里的成员全部将组id设为null
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("group_id", groupId).set("group_id", null);
        userMapper.update(null, wrapper);

        //根据键值对条件做删除
        Map<String, Object> map = new HashMap<>();
        map.put("gid", groupId);
        user_groupMapper.deleteByMap(map);
        researchGroupService.deleteGroup(groupId);
    }

    @Override
    @Transactional
    public ResearchGroup createAndJoinResearchGroup(GroupInfoWithoutId form) {
        if (!teacherService.existsById(form.getCreatorId()))
            throw new NotFoundException("不存在用户id为" + form.getCreatorId() + "的老师");
        User user = getUserById(form.getCreatorId());
        if (form.getDirections().size() == 0)
            throw new BadRequestException("研究方向的数量不能为0");
        ResearchGroup researchGroup = researchGroupService.addOneResearchGroup(form.toResearchGroup(directionSep));

        //将自己加入研究组
        //添加研究组——用户对应表
        //并且将"目前的研究组"切为这个
        User_Group user_group = new User_Group();
        user_group.setUid(user.getId());
        user_group.setGid(researchGroup.getId());
        user_groupMapper.insert(user_group);

        user.setGroupId(researchGroup.getId());
        userMapper.updateById(user);
        return researchGroup;
    }

    @Override
    public boolean existsById(Long uid) {
        return userMapper.selectById(uid) != null;//非空即为存在
    }

    /**
     * 获取用户当前研究组id
     */
    @Override
    public Long getGroupId(Long uid) {
        try {
            return userMapper.selectById(uid).getGroupId();
        } catch (NullPointerException e) {//空指针异常就返回空
            return null;
        }
    }


    @Override
    public void updateUserTrueNameAndEmailAndPortrait(UserTrueNameAndEmailAndPortrait info) {
        User user = getUserById(info.getUserId());

        if (!user.getEmail().equals(info.getEmail()))
            throw new BadRequestException("邮箱不可以修改！");

        user.setTrueName(info.getTrueName());
        user.setEmail(info.getEmail());
        user.setPortrait(info.getPortrait());
        userMapper.updateById(user);
    }


    /**
     * 如何获取该研究组的组名？ 这里的设计是根据隐藏的附加信息获取
     */
    @Override
    @Transactional
    public void approveToJoinGroup(Long messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message.getType() != MessageType.APPLY_TO_JOIN_GROUP)
            throw new BadRequestException("该消息的类型不是申请加入研究组类型！");
        Long groupCreatorId = message.getRecipient(), applierId = message.getSender();
        User applier = getUserById(applierId);

        String groupName = message.getExtraInfo();

        ResearchGroup group = researchGroupService.getResearchGroupByGroupName(groupName);

        // 将消息类型改为普通，消息状态改为已读
        message.setType(MessageType.DEFAULT);
        message.setState(MessageState.READ);
        messageService.updateMessageById(message);

        //加入研究组
        //添加研究组——用户对应表
        //并且将"目前的研究组"切为这个
        User_Group user_group = new User_Group();
        user_group.setUid(applier.getId());
        user_group.setGid(group.getId());

        if (user_groupMapper.selectCount(new QueryWrapper<>(user_group)) > 0) {//已经加进来了
            throw new BadRequestException("该成员已经加入" + groupName + "研究组！");
        }
        user_groupMapper.insert(user_group);

        applier.setGroupId(group.getId());
        userMapper.updateById(applier);


        // 向研究组中所有的成员发送消息提醒
        List<User> users = userMapper.findByGroupId(group.getId());
        for (User u : users) {
            Long id = u.getId();
            if (!id.equals(applierId)) {
                messageService.sendMessage(applierId, id, "用户{sender}加入了研究组" + groupName);
            }
        }
        // 向加入者发送消息提醒
        messageService.sendMessage(groupCreatorId, applierId, groupName +
                "研究组负责人{sender}老师已同意您加入研究组！");
    }

    @Override
    @Transactional
    public void approveToExitGroup(Long messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message.getType() != MessageType.APPLY_TO_EXIT_GROUP)
            throw new BadRequestException("该消息的类型不是申请退出研究组类型！");

        String groupName = message.getExtraInfo();
        ResearchGroup group = researchGroupService.getResearchGroupByGroupName(groupName);

        Long applierId = message.getSender();
        User applier = getUserById(applierId);

        Long groupId = group.getId();
        User_Group user_group = new User_Group();
        user_group.setGid(groupId);
        user_group.setUid(applierId);
        QueryWrapper<User_Group> wrapper = new QueryWrapper<>(user_group);

        // 将消息类型改为普通，消息状态改为已读
        message.setType(MessageType.DEFAULT);
        message.setState(MessageState.READ);
        messageService.updateMessageById(message);

        if (user_groupMapper.selectOne(wrapper) == null)//用户——研究组关系表找不到
            throw new BadRequestException("申请者已经退出研究组了");

        //删掉用户——研究组关系表
        //如果该用户当前组id是这个要退出的研究组,则置为null
        user_groupMapper.delete(wrapper);
        if (applier.getGroupId().equals(groupId))//如果该用户当前组id是这个要退出的研究组
            applier.setGroupId(null);
        userMapper.updateById(applier);


        // 向研究组中所有的成员发送消息提醒
        List<User> users = userMapper.findByGroupId(group.getId());
        for (User u : users) {
            Long id = u.getId();
            if (!id.equals(applierId)) {
                messageService.sendMessage(applierId, id, "用户{sender}退出了研究组" + groupName);
            }
        }
        // 向申请者发送消息提醒
        messageService.sendMessage(researchGroupService.getGroupCreator(groupId), applierId,
                groupName + "研究组负责人{sender}老师已同意您退出研究组");
    }


    /**
     * 一个成员可以在多个研究组
     * 申请即是发消息
     */
    @Override
    @Transactional
    public void applyToJoinGroup(Long userId, Long groupId) {
        ResearchGroup group = researchGroupService.getResearchGroupById(groupId);
        String body = "用户{sender}申请加入您的研究组" + group.getGroupName();

        //组的创建者给这个人发一条信息
        messageService.sendMessage(group.getCreatorId(), userId,
                "您已申请加入" + group.getGroupName() + "研究组，请等待审核。", MessageType.DEFAULT,
                null, group.getGroupName());


        if (!messageService.hasAppliedToJoinGroup(userId, body)) {

            List<User> teachers = getAllTeasInGroup(groupId);
            for (User teacher : teachers) {//组里的所有导师都能收到该消息
                messageService.sendMessage(userId, teacher.getId(),
                        body, MessageType.APPLY_TO_JOIN_GROUP, null, group.getGroupName());
            }


        }
    }

    /**
     * 此时若想申请退出 该用户当前的研究组必然是目前的研究组
     */
    @Override
    @Transactional
    public void applyToExitGroup(Long userId) {
        User user = getUserById(userId);
        if (user.getGroupId() == null)
            throw new BadRequestException("该用户尚未加入研究组或未选择研究组");

        Long groupId = user.getGroupId();// 此时若想申请退出 该用户当前的研究组必然是目前的研究组

        ResearchGroup group = researchGroupService.getResearchGroupById(groupId);
        if (group == null)
            throw new NotFoundException("不存在id为" + groupId + "的研究组");

        String body = "用户{sender}申请退出您的研究组" + group.getGroupName();

        //组的创建者给这个人发一条信息
        messageService.sendMessage(group.getCreatorId(), userId,
                "您已申请退出" + group.getGroupName() + "研究组，请等待审核。", MessageType.DEFAULT,
                null, group.getGroupName());

        if (!messageService.hasAppliedToExitGroup(userId, body)) { // 不允许重复申请
            List<User> teachers = getAllTeasInGroup(groupId);
            for (User teacher : teachers) {//所有的导师都能收到退出申请
                messageService.sendMessage(userId, teacher.getId(),
                        body, MessageType.APPLY_TO_EXIT_GROUP, null, group.getGroupName());
            }

        }
    }


    @Override
    public void refuseToExitGroup(Long messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message.getType() != MessageType.APPLY_TO_EXIT_GROUP)
            throw new BadRequestException("该消息不是 申请退出研究组 的消息类型");

        String groupName = message.getExtraInfo();

        messageService.sendMessage(message.getRecipient(), message.getSender(),
                groupName + "研究组负责人{sender}老师拒绝您退出研究组");
    }

    @Override
    public boolean exitsTheEmail(String email) {//大于0个即为存在
        return userMapper.selectCount(new QueryWrapper<>(User.builder().email(email).build())) > 0;
    }

    @Override
    public List<User> getAllTeasInGroup(Long groupid) {
        return userMapper.getAllTeasInGroup(groupid);
    }

    @Override
    public List<User> getAllStusInGroup(Long groupid) {
        return userMapper.getAllStusInGroup(groupid);
    }


    @Override
    public void refuseToJoinGroup(Long messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message.getType() != MessageType.APPLY_TO_JOIN_GROUP)
            throw new BadRequestException("该消息不是 申请加入研究组 的消息类型");


        String groupName = message.getExtraInfo();

        messageService.sendMessage(message.getRecipient(), message.getSender(),
                groupName + "研究组负责人{sender}老师拒绝您加入研究组");
    }


    /**
     * 激活用户
     * todo 前端页面的实现？
     */
    @Override
    public boolean active(String code) {
        User user = userMapper.selectOne(new QueryWrapper<>(User.builder().code(code).build()));
        if (user != null) {//用户存在，可以激活
            user.setStatus(UserStatus.ACTIVATED);
            userMapper.updateById(user);
            return true;//todo 是跳转还是怎么搞
        } else {
            return false;
            //throw new BadRequestException("激活失败！请联系管理员");
        }
    }

    @Override
    public void updatePwd(UserIdAndPwd info) {
        if (info.getNewPwd().equals(info.getOldPwd()))
            throw new BadRequestException("修改前后的密码一致！");

        User user = getUserById(info.getUserId());

        if (!user.getPassword().equals(info.getOldPwd()))
            throw new BadRequestException("当前用户密码不正确！请重新输入");

        user.setPassword(info.getNewPwd());
        userMapper.updateById(user);
    }

    /**
     * 这里不会立即更改密码 而是先存一条待修改目录在数据库 等到确认后才修改密码
     */
    @Override
    @Transactional
    public void findPwd(findForm findForm) {
        String email = findForm.getEmail();
        User user = userMapper.selectOne(new QueryWrapper<>(User.builder().email(email).build()));

        if (user == null)
            throw new NotFoundException("该邮箱对应的账号不存在！");

        //一分钟内只能发一封邮件 因此要查询相关邮件的第一条修改密码的时间数据
        //现在是点击后就自动跳转了 不用管了
/*
        QueryWrapper<User_Password> wrapper = new QueryWrapper<>(User_Password.builder().email(email).build());
        wrapper.orderByDesc("time");
        IPage<User_Password> page = user_passwordMapper.selectPage(new Page<>(0, 1), wrapper);
        if (page.getRecords().size() == 1 &&
                System.currentTimeMillis() - page.getRecords().get(0).getTime().getTime() < 60 * 1000)
            throw new BadRequestException("请一分钟后再试！");
*/


        String code = UUID.randomUUID().toString().replace("-", "");
        user.setCode(code);//设置新的激活码
        userMapper.updateById(user);
        user_passwordMapper.insert(User_Password.builder().
                password(findForm.getPassword()).
                uuid(code).
                time(new Date()).
                build());


        //发邮件
        String text;
        try {//todo 前端页面的实现？
            text = "<a href='" + localhost + "/user/activePwd?code="
                    + code + "'>【研坊学术研究管理系统】点击确认修改密码，十分钟内有效！（若非您本人操作，请删除此邮件）</a>";
            mailService.sendHtmlMail(user.getEmail(), "修改密码邮件", text);
        } catch (MessagingException e) {
            throw new BadRequestException("修改密码失败！请稍后再试");
        }
    }


    /**
     * 激活用户
     * 返回状态
     * 0: 成功
     * 1: 失败 激活码超时失效
     * 2: 失败 激活码无效
     */
    @Override
    public int activePwd(String code) {
        User_Password user_password = user_passwordMapper.selectOne(new QueryWrapper<>(User_Password.builder().uuid(code).build()));
        if (user_password == null) {//该激活码不存在
            return 2;
        } else if (System.currentTimeMillis() - user_password.getTime().getTime() > 10 * 60 * 1000) {
            //十分钟有效期
            return 1;
        } else {//没问题 更新密码
            User user = userMapper.selectOne(new QueryWrapper<>(User.builder().code(user_password.getUuid()).build()));
            user.setPassword(user_password.getPassword());
            userMapper.updateById(user);
            return 0;
        }
    }

    @Override
    public int insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public ResponseVO<LoginRet> loginWX(String code) {
        String openid = null;
        String session_key = null;
        String errcode = null;
        String errmsg = null;
        WeChatSessionModel weChatSessionModel;

        //微信服务器的接口路径
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + APPID + "&secret=" + APPSECRET + "&js_code=" + code + "&grant_type=authorization_code";
        RestTemplate restTemplate = new RestTemplate();
        //进行网络请求,访问微信服务器接口
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        //根据返回值进行后续操作
        System.out.println(responseEntity);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("成功获取openId");
            String sessionData = responseEntity.getBody();
            Gson gson = new Gson();
            //将json字符串转化为实体类;
            weChatSessionModel = gson.fromJson(sessionData, WeChatSessionModel.class);
            //获取用户的唯一标识
            openid = weChatSessionModel.getOpenid();
            //获取会话秘钥（具有时效性，用户登录的临时通行证）
            //登录后前端的每次接口请求都需带上session_key
            session_key = weChatSessionModel.getSession_key();
            //获取错误码
            errcode = weChatSessionModel.getErrcode();
            //获取错误信息
            errmsg = weChatSessionModel.getErrmsg();
        }
        System.out.println("openId:" + openid);
        System.out.println("session_key:" + session_key);
        try {
            if (openid == null || session_key == null) {
                throw new BadRequestException(errmsg);
            }
            System.out.println("openId!=null");
            UserOpenId userOpenId = userOpenIdService.check(openid);
            if (userOpenId == null) {
                System.out.println("userOpenId==null");
                return new ResponseVO<>(LoginRet.builder().userId(-1L).userType(null).token(null).isBound(false).build(), "该微信未被绑定！");
            } else {
                System.out.println("userOpenId!=null");
                User user = userMapper.selectById(userOpenId.getUserId());
                UserType userType;
                if (user == null) {
                    throw new BadRequestException("登陆失败,原绑定账号已注销！");
                }
                if ("STUDENT".equals(user.getUserType())) {
                    userType = UserType.STUDENT;
                } else if ("TEACHER".equals(user.getUserType())) {
                    userType = UserType.TEACHER;
                } else {
                    throw new ResearchFunException("id为" + user.getId() + "的用户无法在用户类型中找到！");
                }
                return new ResponseVO<>(LoginRet.builder()
                        .userId(user.getId())
                        .userType(userType)
                        .token(TokenUtil.sign(user.getUsername()))
                        .isBound(true)
                        .build(), "该微信已被绑定，登录成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResearchFunException("未知错误！");
        }
    }

    public void changeGroup(UserAndGroup info) {
        User user = userMapper.selectById(info.getId());
        if (user == null)
            throw new NotFoundException("该用户不存在！");

        User_Group user_group = user_groupMapper.selectOne(new QueryWrapper<>(User_Group.builder().uid(info.getId()).gid(info.getGroupId()).build()));
        if (user_group == null)
            throw new NotFoundException("该用户目前并不在该研究组里！");

        user.setGroupId(info.getGroupId());
        userMapper.updateById(user);
    }

    @Override
    public List<ResearchGroupExtremelySimpleInfo> getUserAllGroups(Long uid) {
        User user = userMapper.selectById(uid);
        if (user == null)
            throw new NotFoundException("该用户不存在！");

        List<ResearchGroup> groups = researchGroupService.findGroupsByUid(uid);

        List<ResearchGroupExtremelySimpleInfo> res = new ArrayList<>();

        for (ResearchGroup group : groups)
            res.add(ResearchGroupExtremelySimpleInfo.toExtremelySimpleInfo(group));

        return res;
    }


}