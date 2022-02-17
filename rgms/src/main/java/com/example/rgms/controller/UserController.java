package com.example.rgms.controller;

import com.example.rgms.entity.ResearchGroupEntity;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.researchgroup.GroupInfoWithoutId;
import com.example.rgms.vo.user.*;
import com.example.rgms.vo.response.ResponseVO;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/login")
    @ApiOperation(
            value = "登录接口",
            notes = "该方法会先检查数据库中是否有对应账号，然后检查密码是否一致，都满足，则返回用户id、类别、token，否则会返回错误提示"
    )
    @ApiParam(name = "loginForm", value = "登录表单", required = true)
    public ResponseVO<LoginRet> login(@RequestBody  LoginForm loginForm){
        return new ResponseVO<>(userService.login(loginForm));
    }

//    @PostMapping("/existsTheUsername")
//    @ApiOperation(value = "是否存在该用户名", notes = "输入一个用户名，返回是否已经存在该用户名")
//    @ApiImplicitParam(name = "username", value = "用户名", defaultValue = "111111", required = true)
//    public ResponseVO<Boolean> existsTheUsername(@RequestParam String username){
//        return new ResponseVO<>(userService.existsTheUsername(username));
//    }

    @PostMapping("/register")
    @ApiOperation(value = "注册接口", notes = "该方法会检查用户名是否存在，不存在则会注册账户，存在会返回错误信息")
    @ApiParam(name = "registerForm", value = "注册表单", required = true)
    public void register(@RequestBody RegisterForm registerForm){
        userService.register(registerForm);
    }

    @GetMapping("/getUserIdAndUserTypeByUserName")
    @ApiOperation(value = "根据用户名得到用户id和用户类型", notes = "可以在登录成功或者注册成功后调用")
    @ApiImplicitParam(name = "username", value = "用户名", defaultValue = "123456", required = true)
    public ResponseVO<UserIdAndUserType> getUserIdAndUserTypeByUserName(@RequestParam String username){
        return new ResponseVO<>(userService.getUserIdAndUserTypeByUserName(username));
    }

//    @PostMapping("/editTrueName")
//    @ApiOperation(value = "编辑用户真名", notes = "失败会返回失败原因")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true),
//            @ApiImplicitParam(name = "trueName", value = "用户真名", defaultValue = "张三", required = true)
//    })
//    public void editTrueName(@RequestParam Long userId, @RequestParam String trueName){
//        userService.editTrueName(userId, trueName);
//    }
//
//    @PostMapping("/editEmail")
//    @ApiOperation(value = "编辑用户邮箱", notes = "失败会返回失败原因")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true),
//            @ApiImplicitParam(name = "email", value = "用户邮箱", defaultValue = "123@123.com", required = true)
//    })
//    public void editEmail(@RequestParam Long userId, @RequestParam String email){
//        userService.editEmail(userId, email);
//    }
//
//    @PostMapping("/exitResearchGroup")
//    @ApiOperation(value = "退出研究组接口", notes = "失败返回报错。该接口会同时向研究组的老师发送消息提示")
//    @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true)
//    public void exitResearchGroup(@RequestParam Long userId){
//        userService.exitResearchGroup(userId);
//    }

    @PostMapping("/applyToJoinGroup")
    @ApiOperation(value = "申请加入研究组接口", notes = "如果该用户已经处于一个研究组了，则会报错")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "申请者id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "groupId", value = "要加入的研究组id", defaultValue = "1", required = true)
    })
    public void applyToJoinGroup(@RequestParam Long userId, @RequestParam Long groupId){
        userService.applyToJoinGroup(userId, groupId);
    }

    @PostMapping("/refuseToJoinGroup")
    @ApiOperation(value = "拒绝加入研究组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupCreator", value = "研究组创建者id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "applier", value = "申请加入者id", defaultValue = "1", required = true)
    })
    public void refuseToJoinGroup(Long groupCreator, Long applier) {
        userService.refuseToJoinGroup(groupCreator, applier);
    }

    @PostMapping("/approveToJoinGroup")
    @ApiOperation(value = "同意加入研究组接口", notes = "如果用户用户已经处于一个研究组，会返回错误提示。" +
            "该接口会同时向研究组的成员和新加入者发送消息提醒")
    @ApiImplicitParam(name = "messageId", value = "申请加入研究组的消息的id", defaultValue = "1", required = true)
    public void approveToJoinGroup(@RequestParam Long messageId){
        userService.approveToJoinGroup(messageId);
    }

    @PutMapping("/createAndJoinResearchGroup")
    @ApiOperation(value = "创建并加入研究组接口", notes = "该接口会先检测用户是否为老师，再检测用户是否已经加入研究组，然后才会创建" +
            "研究组并将用户加入。返回创建的研究组实体")
    @ApiParam(name = "form", required = true)
    public ResponseVO<ResearchGroupEntity> createAndJoinResearchGroup(@RequestBody GroupInfoWithoutId form){
        return new ResponseVO<>(userService.createAndJoinResearchGroup(form));
    }

    @GetMapping("/allMemberSimpleInfosInTheGroup")
    @ApiOperation(value = "根据研究组id获取研究组所有成员的接口")
    @ApiImplicitParam(name = "groupId", value = "研究组id", defaultValue = "1", required = true)
    public ResponseVO<List<UserSimpleInfo>> getAllMemberSimpleInfosInTheGroup(@RequestParam Long groupId){
        return new ResponseVO<>(userService.getAllMemberSimpleInfosInTheGroup(groupId));
    }

    @PutMapping("/userTrueNameAndEmailAndPortrait")
    @ApiOperation(value = "更新用户真名、邮箱和头像")
    @ApiParam(name = "info", required = true)
    public void updateUserTrueNameAndEmailAndPortrait(@RequestBody UserTrueNameAndEmailAndPortrait info){
        userService.updateUserTrueNameAndEmailAndPortrait(info);
    }

    @DeleteMapping("/researchGroup")
    @ApiOperation(value = "删除研究组", notes = "使研究组中所有成员退出该研究组，并删除研究组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "删除者id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "groupId", value = "要删除的研究组id", defaultValue = "1", required = true)
    })
    public void deleteResearchGroup(@RequestParam Long userId, @RequestParam Long groupId){
        userService.deleteResearchGroup(userId, groupId);
    }

    @PostMapping("/applyToExitGroup")
    @ApiOperation(value = "申请退出研究组接口", notes = "想 研究组创建者 发送 申请退出 的消息")
    @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true)
    public ResponseVO<String> applyToExitGroup(@RequestParam Long userId){
        userService.applyToExitGroup(userId);
        return new ResponseVO<>("success");
    }

    @PostMapping("/approveToExitGroup")
    @ApiOperation(value = "同意退出研究组接口", notes = "使申请者退出当前所处的研究组，并且会将该消息的类型变为DEFAULT，状态变为已读，" +
            "该接口会同时向研究组的成员和退出者发送消息提醒")
    @ApiImplicitParam(name = "messageId", value = "申请退出研究组的消息的id", defaultValue = "1", required = true)
    public ResponseVO<String> approveToExitGroup(@RequestParam Long messageId){
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

    @GetMapping("/groupId")
    @ApiOperation(value = "根据用户id获取用户所属的研究组id", notes = "如果用户尚未加入研究组，则返回null")
    @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true)
    public ResponseVO<Long> getGroupId(@RequestParam Long userId){
        return new ResponseVO<>(userService.getGroupId(userId));
    }
}
