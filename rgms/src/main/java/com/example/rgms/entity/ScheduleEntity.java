package com.example.rgms.entity;

import com.example.rgms.constant.ScheduleState;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.schedule.ScheduleInfo;
import com.example.rgms.vo.schedule.ScheduleInfoInTheMonth;
import com.example.rgms.vo.schedule.ScheduleWithUserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Date date;
    private String content;
    private ScheduleState state;

    public ScheduleInfo toScheduleInfo(){
        DateFormat format=DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
        return ScheduleInfo.builder()
                .scheduleId(id)
                .date(format.format(date))
                .content(content)
                .state(state)
                .build();
    }

    public ScheduleWithUserInfo toScheduleWithUserInfo(UserService userService){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return ScheduleWithUserInfo.builder()
                .scheduleId(id)
                .userInfo(userService.getUserSimpleInfoById(userId))
                .dayOfMonth(calendar.get(Calendar.DAY_OF_MONTH))
                .content(content)
                .state(state)
                .build();
    }

    public ScheduleInfoInTheMonth toScheduleInfoInTheMonth(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return ScheduleInfoInTheMonth.builder()
                .scheduleId(id)
                .dayOfMonth(calendar.get(Calendar.DAY_OF_MONTH))
                .content(content)
                .state(state)
                .build();
    }
}
