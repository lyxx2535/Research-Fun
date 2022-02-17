package com.example.rgms.vo.schedule;

import com.example.rgms.constant.ScheduleState;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleInfo {
    private Long scheduleId;
    private String date;
    private String content;
    private ScheduleState state;
}
