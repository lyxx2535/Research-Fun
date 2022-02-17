package com.example.rgms.controller;

import com.example.rgms.service.ScheduleService;
import com.example.rgms.vo.response.PageResponseVO;
import com.example.rgms.vo.response.ResponseVO;
import com.example.rgms.vo.schedule.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/schedule")
@Api(tags = "日程相关接口")
public class ScheduleController {
    private ScheduleService scheduleService;
    public ScheduleController(ScheduleService scheduleService){
        this.scheduleService=scheduleService;
    }

    @PostMapping("/create")
    @ApiOperation(value = "创建日程接口", notes = "不报错就是创建成功")
    @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true)
    public void createSchedule(
            @RequestParam Long userId,
            @RequestBody @ApiParam(required = true) ScheduleForm scheduleForm){
        scheduleService.createSchedule(userId, scheduleForm);
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑日程接口", notes = "无法修改过去日期的日程，可以修改已完成的日程，修改后日程会变为未完成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scheduleId", value = "日程id", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "content", value = "新的日程内容", defaultValue = "这是新的日程安排", required = true)
    })
    public void editSchedule(@RequestParam Long scheduleId, @RequestParam String content){
        scheduleService.editSchedule(scheduleId, content);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除日程接口")
    @ApiImplicitParam(name = "scheduleId", value = "日程id", defaultValue = "2", required = true)
    public void deleteSchedule(@RequestParam Long scheduleId){
        scheduleService.deleteSchedule(scheduleId);
    }

    @GetMapping("/all/user")
    @ApiOperation(value = "获取用户所有日程的接口", notes = "不包括已删除的日程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", defaultValue = "5", required = true)
    })
    public PageResponseVO<ScheduleInfo> getAllSchedulesOfTheUser(
            @RequestParam Long userId, @RequestParam int currentPage, @RequestParam int pageSize){
        return new PageResponseVO<>(scheduleService.getAllSchedulesOfTheUser(userId, currentPage, pageSize));
    }

    @PostMapping("/finish")
    @ApiOperation(value = "完成日程接口")
    @ApiImplicitParam(name = "scheduleId", value = "日程id", defaultValue = "1", required = true)
    public void finishSchedule(@RequestParam Long scheduleId){
        scheduleService.finishSchedule(scheduleId);
    }

    @GetMapping("/all/group")
    @ApiOperation(value = "获取研究组某年某月的所有日程", notes = "不包括已删除的日程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "研究组id", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "year", value = "年份", defaultValue = "2021", required = true),
            @ApiImplicitParam(name = "month", value = "月份", defaultValue = "2", required = true)
    })
    public ResponseVO<List<ScheduleWithUserInfo>> getAllSchedulesOfTheGroup(
            @RequestParam Long groupId, @RequestParam int year, @RequestParam int month){
        return new ResponseVO<>(scheduleService.getAllSchedulesOfTheGroup(groupId, year, month));
    }

    @GetMapping("/all/user/month")
    @ApiOperation(value = "获取某用户某年某月的所有日程", notes = "不包括已删除的日程")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "year", value = "年份", defaultValue = "2021", required = true),
            @ApiImplicitParam(name = "month", value = "月份", defaultValue = "2", required = true)
    })
    public ResponseVO<List<ScheduleInfoInTheMonth>> getAllSchedulesOfTheUserInTheMonth(
            @RequestParam Long userId, @RequestParam int year, @RequestParam int month
    ){
        return new ResponseVO<>(scheduleService.getAllSchedulesOfTheUserInTheMonth(userId, year, month));
    }

    @PostMapping("/existsByUserIdAndDate")
    @ApiOperation(value = "判断该用户是否已经在该日期创建了日程", notes = "在用户创建日程时提供方便")
    public ResponseVO<Boolean> existsByUserIdAndDate(@RequestBody @ApiParam(required = true) UserIdAndDate userIdAndDate){
        return new ResponseVO<>(scheduleService.existsByUserIdAndDate(userIdAndDate.getUserId(), userIdAndDate.getDate()));
    }

    @PostMapping("/createForTeacher")
    @ApiOperation(value = "给老师提供的创建日程接口，需要提供相关者id列表")
    @ApiParam(name = "form", required = true)
    public void createScheduleForTeacher(@RequestBody ScheduleFormForTeacher form){
        scheduleService.createSchedule(form);
    }

//    @PutMapping("/editForTeacher")
//    @ApiOperation(value = "为老师提供的修改日程接口，需要提供相关者id列表")
//    @ApiParam(name = "form", required = true)
//    public void editScheduleForTeacher(@RequestBody ScheduleIdAndContentAndStakeHolders form){
//        scheduleService.editSchedule(form.getScheduleId(), form.getContent(), form.getStakeHolderIds());
//    }

//    @DeleteMapping("/teacher")
//    @ApiOperation(value = "为老师提供的接口，需要提供相关者id列表")
//    @ApiParam(name = "form", required = true)
//    public void deleteScheduleForTeacher(@RequestBody ScheduleIdAndStakeHolders form){
//        scheduleService.deleteSchedule(form.getScheduleId(), form.getStakeHolderIds());
//    }

//    @PostMapping("/finishForTeacher")
//    @ApiOperation(value = "为老师提供的接口，需要提供相关者id列表")
//    @ApiParam(name = "form", required = true)
//    public void finishSchedule(@RequestBody ScheduleIdAndStakeHolders form) {
//        scheduleService.finishSchedule(form.getScheduleId(), form.getStakeHolderIds());
//    }

    @GetMapping
    @ApiOperation(value = "通过日程id获取日程信息")
    @ApiImplicitParam(name = "scheduleId", value = "日程id", defaultValue = "33", required = true)
    public ResponseVO<ScheduleInfo> getScheduleInfoById(@RequestParam Long scheduleId){
        return new ResponseVO<>(scheduleService.getScheduleInfoById(scheduleId));
    }

//    @GetMapping("/deleted")
//    @ApiOperation(value = "获取用户所有已删除的日程")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "2", required = true),
//            @ApiImplicitParam(name = "currentPage", value = "当前页，从0计数", defaultValue = "0", required = true),
//            @ApiImplicitParam(name = "pageSize", value = "每页元素个数", defaultValue = "10", required = true)
//    })
//    public PageResponseVO<ScheduleInfo> getDeletedSchedulesOfTheUser(Long userId, int currentPage, int pageSize){
//        return new PageResponseVO<>(scheduleService.getDeletedSchedulesOfTheUser(userId, currentPage, pageSize));
//    }
}
