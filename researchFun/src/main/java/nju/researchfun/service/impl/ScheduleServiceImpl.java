package nju.researchfun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import nju.researchfun.constant.MessageType;
import nju.researchfun.constant.ScheduleState;
import nju.researchfun.entity.Schedule;
import nju.researchfun.entity.user.User;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.NotFoundException;
import nju.researchfun.mapper.ScheduleMapper;
import nju.researchfun.service.*;
import nju.researchfun.vo.schedule.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.*;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private ResearchGroupService researchGroupService;


    @Override
    public void createSchedule(Long userId, ScheduleForm scheduleForm) {//学生创建日程
        Long groupId = userService.getUserById(userId).getGroupId();//自己创，自己收
        /*   Long scheduleId = */
        createSchedule(userId, userId, groupId, scheduleForm.getDate(), scheduleForm.getContent(), scheduleForm.getDdl());

        //todo 这里真的有必要吗
/*        sendMessageToItsCreator(userId, scheduleId,
                "您的学生{sender}新创建了" + toFormatDate(scheduleForm.getDate()) + "的日程");*/
    }

    @Override
    public void editSchedule(Long scheduleId, String content) {
        Schedule schedule = getScheduleById(scheduleId);
        if (isPastDate(schedule.getDate()))
            throw new BadRequestException("过去日期的日程无法修改！");
        if (schedule.getState() == ScheduleState.DELETED)
            throw new BadRequestException("已删除的日程无法修改！");

        schedule.setContent(content);
        if (schedule.getState() == ScheduleState.FINISHED)
            schedule.setState(ScheduleState.UNFINISHED);
        scheduleMapper.updateById(schedule);

        //todo 同上 真的必要吗
        /*if (!schedule.getAccepterId().equals(schedule.getUserId()))//如果不是本人创立的日程
            sendMessageToItsCreator(schedule.getUserId(), schedule.getId(), schedule.getAccepterId(),
                    "您的学生{sender}修改了" + toFormatDate(schedule.getDate()) + "的日程");*/
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        schedule.setState(ScheduleState.DELETED);
        scheduleMapper.updateById(schedule);

        //todo 同上 真的必要吗
        /*if (!schedule.getAccepterId().equals(schedule.getUserId()))//如果不是本人创立的日程
            sendMessageToItsCreator(schedule.getUserId(), schedule.getId(), schedule.getAccepterId(),
                    "您的学生{sender}删除了" + toFormatDate(schedule.getDate()) + "的日程");*/
    }

    @Override
    public void finishSchedule(Long scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        if (schedule.getState() == ScheduleState.FINISHED || schedule.getState() == ScheduleState.DELETED)
            throw new BadRequestException("该日程已经完成或者已经删除！");
        if (isPastDate(schedule.getDate()))
            throw new BadRequestException("过去日期的日程无法修改！");

        schedule.setState(ScheduleState.FINISHED);
        scheduleMapper.updateById(schedule);

        //todo 同上 真的必要吗
        /*if (!schedule.getAccepterId().equals(schedule.getUserId()))//如果不是本人创立的日程
            sendMessageToItsCreator(schedule.getUserId(), schedule.getId(), schedule.getAccepterId(),
                    "您的学生{sender}完成了" + toFormatDate(schedule.getDate()) + "的日程");*/
    }


    @Override
    public IPage<ScheduleInfo> getAllSchedulesOfTheUser(Long userId, int currentPage, int pageSize) {
        Page<Schedule> page = new Page<>(currentPage, pageSize);

        User user = userService.getUserById(userId);
        Schedule schedule = new Schedule();
        schedule.setAccepterId(userId);//todo 这里直接把搜索条件的改为接收者id
        schedule.setGroupId(user.getGroupId());//TODO 同时还要加入研究组限制
        QueryWrapper<Schedule> wrapper = new QueryWrapper<>(schedule);
        wrapper.ne("state", 3);// 屏蔽已删除的日程
        IPage<Schedule> scheduleIPage = scheduleMapper.selectPage(page, wrapper);


        List<ScheduleInfo> scheduleInfoList = new ArrayList<>();
        for (Schedule s : scheduleIPage.getRecords()) {
            scheduleInfoList.add(s.toScheduleInfo());
        }
        Page<ScheduleInfo> scheduleInfoPage = new Page<>(currentPage, pageSize, scheduleInfoList.size());
        scheduleInfoPage.setRecords(scheduleInfoList);
        return scheduleInfoPage;
    }


    @Override
    public ScheduleInfo getScheduleInfoById(Long scheduleId) {
        try {
            return getScheduleById(scheduleId).toScheduleInfo();
        } catch (NotFoundException e) {
            throw new NotFoundException("该日程不存在或已被删除");
        }
    }

    @Override
    public void createSchedule(ScheduleFormForTeacher form) {
        if (!teacherService.existsById(form.getCreatorId()))
            throw new BadRequestException("只有老师能调用这个接口");

        //获取研究组id
        Long groupId = userService.getUserById(form.getCreatorId()).getGroupId();

        // 向相关者发送设立日程并发送消息
        createScheduleAndSendMessageToStakeHolders(form, groupId, "老师{sender}提醒您TA新创建了一个日程", form.getStakeHolderIds());
    }

    private void createScheduleAndSendMessageToStakeHolders(ScheduleFormForTeacher form, Long groupId, String body, List<Long> stakeHolderIds) {
        for (Long id : stakeHolderIds) {
            Long scheduleId = createSchedule(form.getCreatorId(), id, groupId, form.getDate(), form.getContent(), form.getDdl());
            if (!id.equals(form.getCreatorId())) {//不是本人就发消息通知
                messageService.sendMessage(form.getCreatorId(), id, body, MessageType.SCHEDULE_RELATIVE, scheduleId);
            }
        }
    }


    @Override
    public boolean existsByUserIdAndDate(Long userId, Date date) {
        Schedule schedule = new Schedule();
        schedule.setDate(date);
        schedule.setUserId(userId);
        QueryWrapper<Schedule> wrapper = new QueryWrapper<>(schedule);

        return scheduleMapper.selectCount(wrapper) > 0;
    }


    @Override
    public List<ScheduleWithUserInfo> getAllSchedulesOfTheGroup(Long groupId, int year, int month) {
        if (!researchGroupService.selectById(groupId))
            throw new NotFoundException("不存在id为" + groupId + "的研究组");
        if (month <= 0 || month > 12)
            throw new BadRequestException("输入的月份必须为1到12的整数");

        List<Schedule> list = scheduleMapper.findByGroupIdAndDate(groupId, year, month);
        List<ScheduleWithUserInfo> res = new ArrayList<>(list.size());
        for (Schedule schedule : list)
            res.add(schedule.toScheduleWithUserInfo(userService));
        return res;
    }

    @Override
    public List<ScheduleInfoInTheMonth> getAllSchedulesOfTheUserInTheMonth(Long userId, int year, int month) {
        if (!userService.existsById(userId))
            throw new NotFoundException("不存在id为" + userId + "的用户");
        if (month <= 0 || month > 12)
            throw new BadRequestException("输入的月份必须为1到12的整数");
        //接收者为该用户的日程
        List<Schedule> list = scheduleMapper.findByUserIdAndYearAndMonth(userId, year, month);
        List<ScheduleInfoInTheMonth> res = new ArrayList<>(list.size());
        for (Schedule schedule : list)
            res.add(schedule.toScheduleInfoInTheMonth());
        return res;
    }


    private Schedule getScheduleById(Long scheduleId) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null)
            throw new NotFoundException("不存在id为" + scheduleId + "的日程");
        return schedule;
    }

    private void sendMessageToItsCreator(Long userId, Long scheduleId, Long accepterId, String body) {
        User user = userService.getUserById(userId);
        // 如果user是学生，就向他老师发送消息
        if ("STUDENT".equals(user.getUserType())) {
            if (accepterId != null) {//接收者向日程创立者发消息
                messageService.sendMessage(accepterId, userId, body, MessageType.SCHEDULE_RELATIVE, scheduleId);
            }
        }
    }

    private boolean isPastDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return date.before(calendar.getTime());
    }

    private String toFormatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA).format(date);
    }

    private Long createSchedule(Long creatorId, Long accepterId, Long groupId, Date date, String content, Date ddl) {
        if (!userService.existsById(creatorId))
            throw new NotFoundException("不存在id为" + creatorId + "的用户");
        if (!userService.existsById(accepterId))
            throw new NotFoundException("不存在id为" + accepterId + "的用户");
        if (isPastDate(date))
            throw new BadRequestException("请勿在过去的日期创建日程");
        /*if (existsByUserIdAndDate(creatorId, date))
            throw new BadRequestException("你已经创建了这一天的日程了，可以选择修改它");*/

        date.setTime(date.getTime() + 1000 * (60 * 60 * 24 - 1));

        Schedule newSchedule = Schedule.builder()
                .userId(creatorId)
                .date(date)
                .content(content)
                .state(ScheduleState.UNFINISHED)
                .groupId(groupId)
                .accepterId(accepterId)//加上日程的接收者
                .deadline(ddl) //加上提醒时间
                .build();

        scheduleMapper.insert(newSchedule);
        return scheduleMapper.selectOne(new QueryWrapper<>(newSchedule)).getId();
    }

    @Override
    public void remindDDL() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        List<Schedule> list = scheduleMapper.findByDDL(year, month, date);
        for (Schedule ddl : list) {
            messageService.sendMessage(ddl.getUserId(), ddl.getAccepterId(),
                    "您有未完成的日程！请注意时间！", MessageType.SCHEDULE_RELATIVE, ddl.getId());
        }
    }


}
