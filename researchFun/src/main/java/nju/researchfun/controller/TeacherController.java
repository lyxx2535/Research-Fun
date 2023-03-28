package nju.researchfun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import nju.researchfun.service.TeacherService;
import nju.researchfun.vo.response.ResponseVO;
import nju.researchfun.vo.user.TeacherDetailedInfo;
import nju.researchfun.vo.user.UserSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 老师相关控制器
 */
@RestController
@RequestMapping("/teacher")
@Api(tags = "老师相关接口")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/simpleInfo/all")
    @ApiOperation(value = "获取所有老师简单信息接口")
    public ResponseVO<List<UserSimpleInfo>> getAllTeacherSimpleInfos(){
        return new ResponseVO<>(teacherService.getAllTeacherSimpleInfos());
    }

    @GetMapping("/detailedInfo")
    @ApiOperation(value = "获取老师详细信息", notes = "根据老师的用户id得到老师详细信息")
    @ApiImplicitParam(name = "teacherId", value = "老师的用户id", defaultValue = "2", required = true)
    public ResponseVO<TeacherDetailedInfo> getDetailedInfoById(@RequestParam Long teacherId){
        return new ResponseVO<>(teacherService.getDetailedInfoById(teacherId));
    }

}
