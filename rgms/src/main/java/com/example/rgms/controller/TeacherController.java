package com.example.rgms.controller;

import com.example.rgms.service.TeacherService;
import com.example.rgms.vo.response.ResponseVO;
import com.example.rgms.vo.user.TeacherDetailedInfo;
import com.example.rgms.vo.user.UserSimpleInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@Api(tags = "老师相关接口")
public class TeacherController {
    private final TeacherService teacherService;
    public TeacherController(TeacherService teacherService){
        this.teacherService=teacherService;
    }

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
