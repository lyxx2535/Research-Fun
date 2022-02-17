package com.example.rgms.vo.schedule;

import com.example.rgms.constant.ScheduleState;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleInfoInTheMonth {
    private Long scheduleId;
    private int dayOfMonth;
    private String content;
    private ScheduleState state;
}
