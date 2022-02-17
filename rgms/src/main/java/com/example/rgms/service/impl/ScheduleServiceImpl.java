package com.example.rgms.service.impl;

import com.example.rgms.constant.MessageType;
import com.example.rgms.constant.ScheduleState;
import com.example.rgms.entity.ScheduleEntity;
import com.example.rgms.exception.BadRequestException;
import com.example.rgms.exception.NotFoundException;
import com.example.rgms.repository.ScheduleRepository;
import com.example.rgms.service.*;
import com.example.rgms.vo.schedule.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.*;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final UserService userService;
    private final StudentService studentService;
    private final MessageService messageService;
    private final ResearchGroupService researchGroupService;
    private final TeacherService teacherService;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, UserService userService,
                               StudentService studentService, MessageService messageService,
                               ResearchGroupService researchGroupService, TeacherService teacherService){
        this.scheduleRepository=scheduleRepository;
        this.userService=userService;
        this.studentService=studentService;
        this.messageService=messageService;
        this.researchGroupService=researchGroupService;
        this.teacherService=teacherService;
    }

    @Override
    public void createSchedule(Long userId, ScheduleForm scheduleForm) {
        Long scheduleId=createSchedule(userId, scheduleForm.getDate(), scheduleForm.getContent());

        sendMessageToItsTeacher(userId, scheduleId,
                "您的学生{sender}新创建了"+ toFormatDate(scheduleForm.getDate()) + "的日程");
    }

    @Override
    public void editSchedule(Long scheduleId, String content) {
        ScheduleEntity scheduleEntity=getScheduleEntityById(scheduleId);
        if(isPastDate(scheduleEntity.getDate()))
            throw new BadRequestException("过去日期的日程无法修改！");
        if(scheduleEntity.getState()==ScheduleState.DELETED)
            throw new BadRequestException("已删除的日程无法修改！");
        scheduleEntity.setContent(content);
        if(scheduleEntity.getState()==ScheduleState.FINISHED)
            scheduleEntity.setState(ScheduleState.UNFINISHED);
        scheduleRepository.save(scheduleEntity);

        sendMessageToItsTeacher(scheduleEntity.getUserId(), scheduleEntity.getId(),
                "您的学生{sender}修改了"+ toFormatDate(scheduleEntity.getDate()) + "的日程");
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        ScheduleEntity scheduleEntity=getScheduleEntityById(scheduleId);
        scheduleRepository.delete(scheduleEntity);

        sendMessageToItsTeacher(scheduleEntity.getUserId(), scheduleEntity.getId(),
                "您的学生{sender}删除了"+ toFormatDate(scheduleEntity.getDate()) + "的日程");
    }

    @Override
    public Page<ScheduleInfo> getAllSchedulesOfTheUser(Long userId, int currentPage, int pageSize) {
        Pageable pageable=PageRequest.of(currentPage, pageSize);
        Page<ScheduleEntity> entityPage=scheduleRepository.findByUserId(userId, pageable);
        List<ScheduleInfo> scheduleInfoList=new ArrayList<>(entityPage.getNumberOfElements());
        for(ScheduleEntity entity :entityPage) {
            scheduleInfoList.add(entity.toScheduleInfo());
        }
        return new PageImpl<>(scheduleInfoList, pageable, entityPage.getTotalElements());
    }

    @Override
    public void finishSchedule(Long scheduleId) {
        ScheduleEntity scheduleEntity=getScheduleEntityById(scheduleId);
        if(scheduleEntity.getState()==ScheduleState.FINISHED || scheduleEntity.getState()==ScheduleState.DELETED)
            throw new BadRequestException("该日程已经完成或者已经删除！");
        if(isPastDate(scheduleEntity.getDate()))
            throw new BadRequestException("过去日期的日程无法修改！");
        scheduleEntity.setState(ScheduleState.FINISHED);
        scheduleRepository.save(scheduleEntity);

        sendMessageToItsTeacher(scheduleEntity.getUserId(), scheduleEntity.getId(),
                "您的学生{sender}完成了"+ toFormatDate(scheduleEntity.getDate()) + "的日程");
    }

    @Override
    public List<ScheduleWithUserInfo> getAllSchedulesOfTheGroup(Long groupId, int year, int month) {
        if(!researchGroupService.existsById(groupId))
            throw new NotFoundException("不存在id为"+groupId+"的研究组");
        if(month <= 0 || month > 12)
            throw new BadRequestException("输入的月份必须为1到12的整数");
        List<ScheduleEntity> entityList=scheduleRepository.findByGroupId(groupId, year, month);
        List<ScheduleWithUserInfo> res=new ArrayList<>(entityList.size());
        for(ScheduleEntity entity : entityList)
            res.add(entity.toScheduleWithUserInfo(userService));
        return res;
    }

    @Override
    public List<ScheduleInfoInTheMonth> getAllSchedulesOfTheUserInTheMonth(Long userId, int year, int month) {
        if(!userService.existsById(userId))
            throw new NotFoundException("不存在id为"+userId+"的用户");
        if(month <= 0 || month > 12)
            throw new BadRequestException("输入的月份必须为1到12的整数");
        List<ScheduleEntity> entityList=scheduleRepository.findByUserIdAndYearAndMonth(userId, year, month);
        List<ScheduleInfoInTheMonth> res=new ArrayList<>(entityList.size());
        for(ScheduleEntity entity : entityList)
            res.add(entity.toScheduleInfoInTheMonth());
        return res;
    }

    @Override
    public boolean existsByUserIdAndDate(Long userId, Date date) {
        return scheduleRepository.countByUserIdAndDateAndState(userId, date)>0;
    }

    @Override
    public void createSchedule(ScheduleFormForTeacher form) {
        if(!teacherService.existsById(form.getCreatorId()))
            throw new BadRequestException("只有老师能调用这个接口");
        Long scheduleId=createSchedule(form.getCreatorId(), form.getDate(), form.getContent());

        // 向相关者发送消息
        sendMessageToStakeHolders(form.getCreatorId(), scheduleId, "老师{sender}提醒您TA新创建了一个日程", form.getStakeHolderIds());
    }

    @Override
    public void editSchedule(Long scheduleId, String content, List<Long> stakeHolderIds) {
        ScheduleEntity scheduleEntity=getScheduleEntityById(scheduleId);
        if(isPastDate(scheduleEntity.getDate()))
            throw new BadRequestException("过去日期的日程无法修改！");
        if(scheduleEntity.getState()==ScheduleState.DELETED)
            throw new BadRequestException("已删除的日程无法修改！");
        if(!teacherService.existsById(scheduleEntity.getUserId()))
            throw new BadRequestException("该日程的创建者不是老师");
        scheduleEntity.setContent(content);
        if(scheduleEntity.getState()==ScheduleState.FINISHED)
            scheduleEntity.setState(ScheduleState.UNFINISHED);
        scheduleRepository.save(scheduleEntity);

        // 向相关者发送消息
        sendMessageToStakeHolders(scheduleEntity.getUserId(), scheduleId, "老师{sender}提醒您TA新修改了一个日程", stakeHolderIds);
    }

    @Override
    public void deleteSchedule(Long scheduleId, List<Long> stakeHolderIds) {
        ScheduleEntity scheduleEntity=getScheduleEntityById(scheduleId);
        if(!teacherService.existsById(scheduleEntity.getUserId()))
            throw new BadRequestException("该日程的创建者不是老师");
        scheduleEntity.setState(ScheduleState.DELETED);
        scheduleRepository.save(scheduleEntity);

        // 向相关者发送消息
        sendMessageToStakeHolders(scheduleEntity.getUserId(), scheduleId, "老师{sender}提醒您TA新删除了一个日程", stakeHolderIds);
    }

    @Override
    public void finishSchedule(Long scheduleId, List<Long> stakeHolderIds) {
        ScheduleEntity scheduleEntity=getScheduleEntityById(scheduleId);
        if(scheduleEntity.getState()==ScheduleState.FINISHED || scheduleEntity.getState()==ScheduleState.DELETED)
            throw new BadRequestException("该日程已经完成或者已经删除！");
        if(isPastDate(scheduleEntity.getDate()))
            throw new BadRequestException("过去日期的日程无法修改！");
        if(!teacherService.existsById(scheduleEntity.getUserId()))
            throw new BadRequestException("该日程的创建者不是老师");
        scheduleEntity.setState(ScheduleState.FINISHED);
        scheduleRepository.save(scheduleEntity);

        // 向相关者发送消息
        sendMessageToStakeHolders(scheduleEntity.getUserId(), scheduleId, "老师{sender}提醒您TA完成了一个日程", stakeHolderIds);
    }

    @Override
    public ScheduleInfo getScheduleInfoById(Long scheduleId) {
        try {
            return getScheduleEntityById(scheduleId).toScheduleInfo();
        } catch (NotFoundException e){
            throw new NotFoundException("该日程不存在或已被删除");
        }
    }

    @Override
    public Page<ScheduleInfo> getDeletedSchedulesOfTheUser(Long userId, int currentPage, int pageSize) {
        Pageable pageable=PageRequest.of(currentPage, pageSize);
        Page<ScheduleEntity> entityPage=scheduleRepository.findByUserIdOrderByDateDesc(userId, pageable);
        List<ScheduleInfo> scheduleInfoList=new ArrayList<>(entityPage.getNumberOfElements());
        for(ScheduleEntity entity :entityPage) {
            if(entity.getState()==ScheduleState.DELETED)
                scheduleInfoList.add(entity.toScheduleInfo());
        }
        return new PageImpl<>(scheduleInfoList, pageable, entityPage.getTotalElements());
    }

    private ScheduleEntity getScheduleEntityById(Long scheduleId){
        Optional<ScheduleEntity> maybeEntity=scheduleRepository.findById(scheduleId);
        if(!maybeEntity.isPresent())
            throw new NotFoundException("不存在id为"+scheduleId+"的日程");
        return maybeEntity.get();
    }

    private void sendMessageToItsTeacher(Long userId, Long scheduleId, String body){
        // 如果user是学生，就向他老师发送消息
        if(studentService.existsById(userId)){
            Long teacherId=studentService.getInstructorId(userId);
            if(teacherId!=null){
                messageService.sendMessage(userId, teacherId, body, MessageType.SCHEDULE_RELATIVE, scheduleId);
            }
        }
    }

    private boolean isPastDate(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return date.before(calendar.getTime());
    }

    private String toFormatDate(Date date){
        return DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA).format(date);
    }

    private Long createSchedule(Long creatorId, Date date, String content){
        if(!userService.existsById(creatorId))
            throw new NotFoundException("不存在id为"+creatorId+"的用户");
        if(isPastDate(date))
            throw new BadRequestException("请勿在过去的日期创建日程");
        if(existsByUserIdAndDate(creatorId, date))
            throw new BadRequestException("你已经创建了这一天的日程了，可以选择修改它");
        return scheduleRepository.save(ScheduleEntity.builder()
                .userId(creatorId)
                .date(date)
                .content(content)
                .state(ScheduleState.UNFINISHED)
                .build())
                .getId();
    }

    private void sendMessageToStakeHolders(Long creatorId, Long scheduleId, String body, List<Long> stakeHolderIds){
        for(Long id : stakeHolderIds){
            if(!id.equals(creatorId)){
                messageService.sendMessage(creatorId, id, body, MessageType.SCHEDULE_RELATIVE, scheduleId);
            }
        }
    }
}
