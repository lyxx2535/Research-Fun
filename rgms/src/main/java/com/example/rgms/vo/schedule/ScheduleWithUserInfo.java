package com.example.rgms.vo.schedule;

import com.example.rgms.constant.ScheduleState;
import com.example.rgms.vo.user.UserSimpleInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleWithUserInfo {
    private Long scheduleId;
    private UserSimpleInfo userInfo;
    private int dayOfMonth;
    private String content;
    private ScheduleState state;
}
