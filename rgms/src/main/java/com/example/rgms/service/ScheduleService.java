package com.example.rgms.service;

import com.example.rgms.vo.schedule.*;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface ScheduleService {
    void createSchedule(Long userId, ScheduleForm scheduleForm);

    void editSchedule(Long scheduleId, String content);

    void deleteSchedule(Long scheduleId);

    Page<ScheduleInfo> getAllSchedulesOfTheUser(Long userId, int currentPage, int pageSize);

    void finishSchedule(Long scheduleId);

    List<ScheduleWithUserInfo> getAllSchedulesOfTheGroup(Long groupId, int year, int month);

    List<ScheduleInfoInTheMonth> getAllSchedulesOfTheUserInTheMonth(Long userId, int year, int month);

    boolean existsByUserIdAndDate(Long userId, Date date);

    void createSchedule(ScheduleFormForTeacher form);

    void editSchedule(Long scheduleId, String content, List<Long> stakeHolderIds);

    void deleteSchedule(Long scheduleId, List<Long> stakeHolderIds);

    void finishSchedule(Long scheduleId, List<Long> stakeHolderIds);

    ScheduleInfo getScheduleInfoById(Long scheduleId);

    Page<ScheduleInfo> getDeletedSchedulesOfTheUser(Long userId, int currentPage, int pageSize);
}
