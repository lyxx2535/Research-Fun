package nju.researchfun.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.constant.ScheduleState;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.schedule.ScheduleInfo;
import nju.researchfun.vo.schedule.ScheduleInfoInTheMonth;
import nju.researchfun.vo.schedule.ScheduleWithUserInfo;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @TableId
    private Long id;
    private Long userId;
    private Date date;
    private String content;
    private ScheduleState state;
    private Long groupId; //对应研究组
    private Long accepterId;//日程的接收者
    private Date deadline;//提醒日期

    public ScheduleInfo toScheduleInfo() {
        DateFormat format = DateFormat.getDateInstance(DateFormat.FULL, Locale.CHINA);
        return ScheduleInfo.builder()
                .scheduleId(id)
                .date(format.format(date))
                .content(content)
                .state(state)
                .ddl(deadline == null ? null : format.format(deadline))
                .build();
    }

    public ScheduleWithUserInfo toScheduleWithUserInfo(UserService userService) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return ScheduleWithUserInfo.builder()
                .scheduleId(id)
                .userInfo(userService.getUserSimpleInfoById(userId))
                .dayOfMonth(calendar.get(Calendar.DAY_OF_MONTH))
                .content(content)
                .state(state)
                .build();
    }

    public ScheduleInfoInTheMonth toScheduleInfoInTheMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return ScheduleInfoInTheMonth.builder()
                .scheduleId(id)
                .dayOfMonth(calendar.get(Calendar.DAY_OF_MONTH))
                .content(content)
                .state(state)
                .build();
    }
}
