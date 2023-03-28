package nju.researchfun.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import nju.researchfun.vo.schedule.*;

import java.util.Date;
import java.util.List;

public interface ScheduleService {


    boolean existsByUserIdAndDate(Long userId, Date date);

    void createSchedule(Long userId, ScheduleForm scheduleForm);

    void editSchedule(Long scheduleId, String content);

    void deleteSchedule(Long scheduleId);

    void finishSchedule(Long scheduleId);

    ScheduleInfo getScheduleInfoById(Long scheduleId);

    IPage<ScheduleInfo> getAllSchedulesOfTheUser(Long userId, int currentPage, int pageSize);

    void createSchedule(ScheduleFormForTeacher form);

    List<ScheduleWithUserInfo> getAllSchedulesOfTheGroup(Long groupId, int year, int month);

    List<ScheduleInfoInTheMonth> getAllSchedulesOfTheUserInTheMonth(Long userId, int year, int month);

    /**
     * 日程提醒接口
     */
    void remindDDL();

}
