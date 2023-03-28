package nju.researchfun.controller;

import io.swagger.annotations.*;
import nju.researchfun.entity.ResearchGroup;
import nju.researchfun.service.CheckCodeService;
import nju.researchfun.service.UserService;
import nju.researchfun.utils.CheckCodeUtil;
import nju.researchfun.vo.researchgroup.GroupInfoWithoutId;
import nju.researchfun.vo.researchgroup.ResearchGroupExtremelySimpleInfo;
import nju.researchfun.vo.researchgroup.ResearchGroupSimpleInfo;
import nju.researchfun.vo.response.ResponseVO;
import nju.researchfun.vo.session.SessionVO;
import nju.researchfun.vo.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CheckCodeService checkCodeService;

    @PostMapping("/login")
    @ApiOperation(
            value = "登录接口",
            notes = "该方法会先检查数据库中是否有对应账号，然后检查密码是否一致，都满足，则返回用户id、类别、token，否则会返回错误提示"
    )
    @ApiParam(name = "loginForm", value = "登录表单", required = true)
    public ResponseVO<LoginRet> login(@RequestBody LoginForm loginForm) {
        return new ResponseVO<>(userService.login(loginForm));
    }

    @PostMapping("/register")
    @ApiOperation(value = "注册接口", notes = "该方法会检查用户名是否存在，不存在则会注册账户，存在会返回错误信息")
    @ApiParam(name = "registerForm", value = "注册表单", required = true)
    public void register(@RequestBody RegisterForm registerForm) {
        userService.register(registerForm);
    }

    @GetMapping("/getUserIdAndUserTypeByUserName")
    @ApiOperation(value = "根据用户名得到用户id和用户类型", notes = "可以在登录成功或者注册成功后调用")
    @ApiImplicitParam(name = "username", value = "用户名", defaultValue = "123456", required = true)
    public ResponseVO<UserIdAndUserType> getUserIdAndUserTypeByUserName(@RequestParam String username) {
        return new ResponseVO<>(userService.getUserIdAndUserTypeByUserName(username));
    }

    @PutMapping("/userTrueNameAndEmailAndPortrait")
    @ApiOperation(value = "更新用户真名、邮箱和头像")
    @ApiParam(name = "info", required = true)
    public void updateUserTrueNameAndEmailAndPortrait(@RequestBody UserTrueNameAndEmailAndPortrait info) {
        userService.updateUserTrueNameAndEmailAndPortrait(info);
    }

    @PostMapping("/updatePwd")
    @ApiOperation(value = "更改密码接口", notes = "此方法会先比较新旧密码是否相同，然后再比较旧密码是否是正确的，再进行更新")
    @ApiParam(name = "info", required = true)
    public void updatePwd(@RequestBody UserIdAndPwd info) {
        userService.updatePwd(info);
    }

    @GetMapping("/allMemberSimpleInfosInTheGroup")
    @ApiOperation(value = "根据研究组id获取研究组所有成员的接口")
    @ApiImplicitParam(name = "groupId", value = "研究组id", defaultValue = "1", required = true)
    public ResponseVO<List<UserSimpleInfo>> getAllMemberSimpleInfosInTheGroup(@RequestParam Long groupId) {
        return new ResponseVO<>(userService.getAllMemberSimpleInfosInTheGroup(groupId));
    }

    @DeleteMapping("/researchGroup")
    @ApiOperation(value = "删除研究组", notes = "使研究组中所有成员退出该研究组，并删除研究组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "删除者id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "groupId", value = "要删除的研究组id", defaultValue = "1", required = true)
    })
    public void deleteResearchGroup(@RequestParam Long userId, @RequestParam Long groupId) {
        userService.deleteResearchGroup(userId, groupId);
    }

    @PutMapping("/createAndJoinResearchGroup")
    @ApiOperation(value = "创建并加入研究组接口",
            notes = "该接口会先检测用户是否为老师，再检测用户是否已经加入研究组，" +
                    "然后才会创建研究组并将用户加入。返回创建的研究组实体")
    @ApiParam(name = "form", required = true)
    public ResponseVO<ResearchGroup> createAndJoinResearchGroup(@RequestBody GroupInfoWithoutId form) {
        return new ResponseVO<>(userService.createAndJoinResearchGroup(form));
    }


    @PostMapping("/applyToJoinGroup")
    @ApiOperation(value = "申请加入研究组接口", notes = "用户可以加入多个研究组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "申请者id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "groupId", value = "要加入的研究组id", defaultValue = "1", required = true)
    })
    public void applyToJoinGroup(@RequestParam Long userId, @RequestParam Long groupId) {
        userService.applyToJoinGroup(userId, groupId);
    }

    @PostMapping("/applyToExitGroup")
    @ApiOperation(value = "申请退出研究组接口", notes = "向 研究组创建者 发送 申请退出 的消息")
    @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true)
    public ResponseVO<String> applyToExitGroup(@RequestParam Long userId) {
        userService.applyToExitGroup(userId);
        return new ResponseVO<>("success");
    }

/*    @PostMapping("/refuseToJoinGroup")
    @ApiOperation(value = "拒绝加入研究组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupCreator", value = "研究组创建者id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "applier", value = "申请加入者id", defaultValue = "1", required = true)
    })
    public void refuseToJoinGroup(Long groupCreator, Long applier) {
        userService.refuseToJoinGroup(groupCreator, applier);
    }*/

    @PostMapping("/refuseToJoinGroup")
    @ApiOperation(value = "拒绝加入研究组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "messageId", value = "申请加入研究组的消息的id", defaultValue = "2", required = true),
    })
    public void refuseToJoinGroup(Long messageId) {
        userService.refuseToJoinGroup(messageId);
    }


    @GetMapping("/groupId")
    @ApiOperation(value = "根据用户id获取用户当前的研究组id", notes = "如果用户尚未加入研究组，则返回null")
    @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true)
    public ResponseVO<Long> getGroupId(@RequestParam Long userId) {
        return new ResponseVO<>(userService.getGroupId(userId));
    }

    @PostMapping("/approveToJoinGroup")
    @ApiOperation(value = "同意加入研究组接口", notes = "如果用户用户已经处于一个研究组，会返回错误提示。" +
            "该接口会同时向研究组的成员和新加入者发送消息提醒")
    @ApiImplicitParam(name = "messageId", value = "申请加入研究组的消息的id", defaultValue = "1", required = true)
    public void approveToJoinGroup(@RequestParam Long messageId) {
        userService.approveToJoinGroup(messageId);
    }

    @PostMapping("/approveToExitGroup")
    @ApiOperation(value = "同意退出研究组接口", notes = "使申请者退出当前所处的研究组，并且会将该消息的类型变为DEFAULT，状态变为已读，" +
            "该接口会同时向研究组的成员和退出者发送消息提醒")
    @ApiImplicitParam(name = "messageId", value = "申请退出研究组的消息的id", defaultValue = "1", required = true)
    public ResponseVO<String> approveToExitGroup(@RequestParam Long messageId) {
        userService.approveToExitGroup(messageId);
        return new ResponseVO<>("success");
    }

    @PostMapping("/refuseToExitGroup")
    @ApiOperation(value = "拒绝退出研究组")
    @ApiImplicitParam(name = "messageId", value = "申请退出研究组的消息的id", defaultValue = "1", required = true)
    public ResponseVO<String> refuseToExitGroup(Long messageId) {
        userService.refuseToExitGroup(messageId);
        return new ResponseVO<>("success");
    }

    @PostMapping("/checkCode")
    @ApiOperation(
            value = "获取验证码",
            notes = "会返回验证码图片的base64数据"
    )
    @ApiImplicitParam(name = "sessionId", value = "本次回话的sessionId", defaultValue = "6F8183A2CA4EE74F6415A360848A4590", required = true)
    public void checkCode(@RequestParam String sessionId, HttpServletResponse response) {
//        try {
//            CheckCodeUtil.proCheckCode(request, response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            checkCodeService.proCheckCode(sessionId,response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/active")
    @ApiOperation(
            value = "用户激活",
            notes = "不报错即为激活成功"
    )
    @ApiImplicitParam(name = "code", value = "激活码", defaultValue = "111111111111", required = true)
    public void active(String code, HttpServletResponse response) throws IOException {
        //TODO 如何设计 这里先照抄
        String msg;
        if (userService.active(code)) {
            //todo 看看这里怎么改
            msg = "激活成功,请<a href='http://rgm.zhiyou.tech/login'>登录</a>";
        } else {
            msg = "激活失败,请联系管理员！";
        }
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(msg);
    }

    @PostMapping("/find")
    @ApiOperation(
            value = "找回密码接口",
            notes = "该方法会先根据用户输入的邮箱号检查是否有该账号，若有，则发送一封验证邮件，点击确认后才能更新密码"
    )
    @ApiParam(name = "findForm", value = "找回密码表单", required = true)
    public void find(@RequestBody findForm findForm) {
        userService.findPwd(findForm);
    }

    @GetMapping("/activePwd")
    @ApiOperation(
            value = "用户修改密码确认",
            notes = "直接返回文字"
    )
    @ApiImplicitParam(name = "code", value = "激活码", defaultValue = "111111111111", required = true)
    public void activePwd(String code, HttpServletResponse response) throws IOException {
        //TODO 如何设计 这里先照抄
        String msg;
        int status = userService.activePwd(code);
        if (status == 0) {
            //todo 看看这里怎么改
            msg = "确认成功,请<a href='http://rgm.zhiyou.tech/login'>登录</a>";
        } else if (status == 1) {
            msg = "该激活码超时无效！";
        } else {
            msg = "该激活码无效！请联系管理员";
        }
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(msg);
    }

    @PostMapping("/changeGroup")
    @ApiOperation(
            value = "切换用户当前研究组id入口",
            notes = "根据当前用户的id和准备切换的研究组id来进行相应的切换"
    )
    @ApiParam(name = "userAndGroup", value = "用户的id和要切换的研究组id", required = true)
    public void changeGroup(@RequestBody UserAndGroup userAndGroup) {
        userService.changeGroup(userAndGroup);
    }

    @GetMapping("/getUserAllGroups")
    @ApiOperation(
            value = "获取当前用户所在的所有研究组",
            notes = "返回一个列表 含有研究组id和组名"
    )
    @ApiParam(name = "ResearchGroupExtremelySimpleInfo", value = "用户的id和要切换的研究组id", required = true)
    public ResponseVO<List<ResearchGroupExtremelySimpleInfo>> changeGroup(@RequestParam Long userId) {
        return new ResponseVO<>(userService.getUserAllGroups(userId));
    }


    @PostMapping("/wxLogin")
    @ApiOperation(
            value = "微信登录接口",
            notes = "该方法会先根据前端传来的code检查该微信是否绑定账号（通过给属性is_bound赋值）"
    )
    @ApiParam(name = "code", value = "微信登录code", required = true)
    public ResponseVO<LoginRet> loginWX(@RequestParam String code) {
        return userService.loginWX(code);
    }

}
