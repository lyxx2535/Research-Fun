package com.example.rgms.service.impl;

import com.example.rgms.constant.MessageState;
import com.example.rgms.constant.MessageType;
import com.example.rgms.constant.UserType;
import com.example.rgms.entity.MessageEntity;
import com.example.rgms.entity.ResearchGroupEntity;
import com.example.rgms.entity.user.StudentEntity;
import com.example.rgms.entity.user.TeacherEntity;
import com.example.rgms.entity.user.UserEntity;
import com.example.rgms.exception.BadRequestException;
import com.example.rgms.exception.NotFoundException;
import com.example.rgms.exception.RgmsException;
import com.example.rgms.repository.user.UserRepository;
import com.example.rgms.service.*;
import com.example.rgms.utils.TokenUtil;
import com.example.rgms.vo.researchgroup.GroupInfoWithoutId;
import com.example.rgms.vo.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Value(value = "${user.default-portrait-url}")
    private String defaultPortraitUrl;
    @Value(value = "${research-group.direction-separator}")
    private String directionSep;

    private final UserRepository userRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ResearchGroupService researchGroupService;
    private final MessageService messageService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, StudentService studentService,
                           TeacherService teacherService, ResearchGroupService researchGroupService,
                           MessageService messageService, BCryptPasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.studentService=studentService;
        this.teacherService=teacherService;
        this.researchGroupService=researchGroupService;
        this.messageService=messageService;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public LoginRet login(LoginForm loginForm) {
        String username=loginForm.getUsername(), password=loginForm.getPassword();

        if(!userRepository.existsByUsername(username))
            throw new BadRequestException("该用户名尚未注册！");

        UserEntity userEntity=userRepository.findByUsername(username);
        if(!passwordEncoder.matches(password, userEntity.getPassword()))
            throw new BadRequestException("密码错误！");

        Long userId=userEntity.getId();

        // 获取userType
        UserType userType;
        if(studentService.existsById(userId)){
            userType=UserType.STUDENT;
        } else if(teacherService.existsById(userId)){
            userType=UserType.TEACHER;
        } else {
            throw new RgmsException("id为"+userId+"的用户无法在用户类型中找到！");
        }

        return LoginRet.builder()
                .userId(userEntity.getId())
                .userType(userType)
                .token(TokenUtil.sign(username))
                .build();
    }

    @Override
    public Boolean existsTheUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void register(RegisterForm registerForm) {
        if(existsTheUsername(registerForm.getUsername()))
            throw new BadRequestException("要注册的用户名已经存在！");
        UserEntity userEntity=registerForm.toUserEntity();
        userEntity.setPortrait(defaultPortraitUrl);
        userEntity.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        userRepository.save(userEntity);

        Long userId=userEntity.getId();
        switch (registerForm.getUserType()){
            case STUDENT:
                studentService.addOneStudent(StudentEntity.builder().userId(userId).build());
                break;
            case TEACHER:
                teacherService.addOneTeacher(TeacherEntity.builder().userId(userId).build());
                break;
            default:
                throw new BadRequestException("非法的用户类型！");
        }
    }

    @Override
    public UserIdAndUserType getUserIdAndUserTypeByUserName(String username) {
        if(!existsTheUsername(username))
            throw new BadRequestException("不存在用户名为“"+username+"”的用户！");

        UserIdAndUserType res=new UserIdAndUserType();

        Long userId=userRepository.findByUsername(username).getId();
        res.setUserId(userId);

        if(studentService.existsById(userId)){
            res.setUserType(UserType.STUDENT);
        } else if(teacherService.existsById(userId)){
            res.setUserType(UserType.TEACHER);
        } else {
            throw new RgmsException("id为"+userId+"的用户无法在用户类型中找到！");
        }
        return res;
    }

    @Override
    public void editTrueName(Long userId, String trueName) {
        UserEntity userEntity=getUserEntityById(userId);
        userEntity.setTrueName(trueName);
        userRepository.save(userEntity);
    }

    @Override
    public void editEmail(Long userId, String email) {
        UserEntity userEntity=getUserEntityById(userId);
        userEntity.setEmail(email);
        userRepository.save(userEntity);
    }

    @Override
    public void exitResearchGroup(Long userId) {
        UserEntity userEntity=getUserEntityById(userId);
        if(userEntity.getGroupId()==null)
            throw new BadRequestException("您尚未加入研究组，无法退出");
        if(researchGroupService.getGroupCreator(userEntity.getGroupId()).equals(userId))
            throw new BadRequestException("您是研究组的创建者，无法退出研究组");
        Long lastGroupId=userEntity.getGroupId();
        userEntity.setGroupId(null);
        userRepository.save(userEntity);

        // 向研究组中所有的成员发送消息提醒
        List<UserEntity> userEntities=userRepository.findByGroupId(lastGroupId);
        for(UserEntity entity : userEntities){
            Long id=entity.getId();
            messageService.sendMessage(userId, id, "用户{sender}退出了研究组");
        }
    }

    @Override
    public void approveToJoinGroup(Long messageId) {
        MessageEntity messageEntity=messageService.getMessageEntityById(messageId);
        if(messageEntity.getType()!=MessageType.APPLY_TO_JOIN_GROUP)
            throw new BadRequestException("该消息的类型不是申请加入研究组类型！");
        Long groupCreator=messageEntity.getRecipient(), applier=messageEntity.getSender();
        UserEntity userEntity=getUserEntityById(applier);
        if(userEntity.getGroupId()!=null)
            throw new BadRequestException("加入失败，对方已经加入了其他的研究组");
        Long groupId=getUserEntityById(groupCreator).getGroupId();
        ResearchGroupEntity groupEntity=researchGroupService.getResearchGroupEntityById(groupId); // 检查是否存在这个id的研究组
        userEntity.setGroupId(groupId);
        userRepository.save(userEntity);

        // 将消息类型改为普通，消息状态改为已读
        messageEntity.setType(MessageType.DEFAULT);
        messageEntity.setState(MessageState.READ);
        messageService.saveMessageEntity(messageEntity);

        // 向研究组中所有的成员发送消息提醒
        List<UserEntity> userEntities=userRepository.findByGroupId(groupId);
        for(UserEntity entity : userEntities){
            Long id=entity.getId();
            if(!id.equals(applier)) {
                messageService.sendMessage(applier, id, "用户{sender}加入了研究组");
            }
        }
        // 向加入者发送消息提醒
        messageService.sendMessage(groupCreator, applier, groupEntity.getGroupName()+"研究组负责人{sender}老师已同意您加入研究组！");
    }

    public UserEntity getUserEntityById(Long userId){
        if(userId==null)
            throw new BadRequestException("给定的用户id不能为null");
        Optional<UserEntity> maybeEntity=userRepository.findById(userId);
        if(!maybeEntity.isPresent())
            throw new NotFoundException("不存在id为"+userId+"的用户！");
        return maybeEntity.get();
    }

    @Override
    public ResearchGroupEntity createAndJoinResearchGroup(GroupInfoWithoutId form) {
        if(!teacherService.existsById(form.getCreatorId()))
            throw new NotFoundException("不存在用户id为"+form.getCreatorId()+"的老师");
        UserEntity userEntity=getUserEntityById(form.getCreatorId());
        if(userEntity.getGroupId()!=null)
            throw new BadRequestException("您已经在一个研究组了！");
        if(form.getDirections().size()==0)
            throw new BadRequestException("研究方向的数量不能为0");
        ResearchGroupEntity researchGroupEntity=researchGroupService.addOneResearchGroup(form.toResearchGroupEntity(directionSep));
        // 将自己加入研究组
        userEntity.setGroupId(researchGroupEntity.getId());
        userRepository.save(userEntity);
        return researchGroupEntity;
    }

    @Override
    public UserSimpleInfo getUserSimpleInfoById(Long userId) {
        UserEntity userEntity=getUserEntityById(userId);
        return UserSimpleInfo.builder()
                .userId(userId)
                .username(userEntity.getUsername())
                .trueName(userEntity.getTrueName())
                .portrait(userEntity.getPortrait())
                .build();
    }

    @Override
    public List<UserSimpleInfo> getAllMemberSimpleInfosInTheGroup(Long groupId) {
        List<UserEntity> userEntities=userRepository.findByGroupId(groupId);
        List<UserSimpleInfo> res=new ArrayList<>(userEntities.size());
        for(UserEntity userEntity : userEntities)
            res.add(userEntity.toUserSimpleInfo());
        return res;
    }

    @Override
    public boolean existsById(Long userId){
        return userRepository.existsById(userId);
    }

    @Override
    public void updateUserTrueNameAndEmailAndPortrait(UserTrueNameAndEmailAndPortrait info) {
        UserEntity userEntity=getUserEntityById(info.getUserId());
        userEntity.setTrueName(info.getTrueName());
        userEntity.setEmail(info.getEmail());
        userEntity.setPortrait(info.getPortrait());
        userRepository.save(userEntity);
    }

    @Override
    public void deleteResearchGroup(Long userId, Long groupId) {
        if(!researchGroupService.getGroupCreator(groupId).equals(userId))
            throw new BadRequestException("只有研究组的创建者可以删除研究组");
        userRepository.allMemberExitGroup(groupId);
        researchGroupService.deleteGroup(groupId);
    }

    @Override
    public void applyToJoinGroup(Long userId, Long groupId) {
        UserEntity userEntity=getUserEntityById(userId);
        if(userEntity.getGroupId()!=null)
            throw new BadRequestException("您已经在一个研究组里了，请先退出");
        if(!messageService.hasAppliedToJoinGroup(userId)) { // 不允许重复申请
            messageService.sendMessage(userId, researchGroupService.getGroupCreator(groupId),
                    "用户{sender}申请加入您的研究组", MessageType.APPLY_TO_JOIN_GROUP, null);
        }
    }

    @Override
    public void refuseToJoinGroup(Long groupCreator, Long applier) {
        UserEntity userEntity=getUserEntityById(groupCreator);
        ResearchGroupEntity groupEntity=researchGroupService.getResearchGroupEntityById(userEntity.getGroupId());
        messageService.sendMessage(groupCreator, applier, groupEntity.getGroupName()+"研究组负责人{sender}老师拒绝您加入研究组");
    }

    @Override
    public void applyToExitGroup(Long userId){
        UserEntity userEntity=getUserEntityById(userId);
        if(userEntity.getGroupId()==null)
            throw new BadRequestException("该用户尚未加入研究组");
        if(!messageService.hasAppliedToExitGroup(userId)) { // 不允许重复申请
            Long groupCreatorId = researchGroupService.getGroupCreator(userEntity.getGroupId());
            messageService.sendMessage(userId, groupCreatorId, "用户{sender}申请退出您的研究组",
                    MessageType.APPLY_TO_EXIT_GROUP, null);
        }
    }

    @Override
    public void approveToExitGroup(Long messageId){
        MessageEntity messageEntity=messageService.getMessageEntityById(messageId);
        if(messageEntity.getType()!=MessageType.APPLY_TO_EXIT_GROUP)
            throw new BadRequestException("该消息的类型不是申请退出研究组类型！");
        Long applier=messageEntity.getSender();
        UserEntity userEntity=getUserEntityById(applier);
        if(userEntity.getGroupId()==null)
            throw new BadRequestException("申请者已经退出研究组了");
        Long groupId=userEntity.getGroupId();
        userEntity.setGroupId(null);
        userRepository.save(userEntity);

        // 将消息类型改为普通，消息状态改为已读
        messageEntity.setType(MessageType.DEFAULT);
        messageEntity.setState(MessageState.READ);
        messageService.saveMessageEntity(messageEntity);

        // 向研究组中所有的成员发送消息提醒
        List<UserEntity> userEntities=userRepository.findByGroupId(groupId);
        for(UserEntity entity : userEntities){
            Long id=entity.getId();
            if(!id.equals(applier)) {
                messageService.sendMessage(applier, id, "用户{sender}退出了研究组");
            }
        }
        // 向申请者发送消息提醒
        messageService.sendMessage(researchGroupService.getGroupCreator(groupId), applier,
                researchGroupService.getResearchGroupEntityById(groupId).getGroupName()+"研究组负责人{sender}老师已同意您退出研究组");
    }

    @Override
    public void refuseToExitGroup(Long messageId){
        MessageEntity messageEntity=messageService.getMessageEntityById(messageId);
        if(messageEntity.getType()!=MessageType.APPLY_TO_EXIT_GROUP)
            throw new BadRequestException("该消息不是 申请退出研究组 的消息类型");
        UserEntity userEntity=getUserEntityById(messageEntity.getRecipient());
        ResearchGroupEntity groupEntity=researchGroupService.getResearchGroupEntityById(userEntity.getGroupId());
        messageService.sendMessage(messageEntity.getRecipient(), messageEntity.getSender(),
                groupEntity.getGroupName()+"研究组负责人{sender}老师拒绝您退出研究组");
    }

    @Override
    public Long getGroupId(Long userId) {
        return getUserEntityById(userId).getGroupId();
    }
}
