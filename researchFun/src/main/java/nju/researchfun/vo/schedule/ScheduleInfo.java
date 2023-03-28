package nju.researchfun.vo.schedule;

import lombok.Builder;
import lombok.Data;
import nju.researchfun.constant.ScheduleState;

@Data
@Builder
public class ScheduleInfo {
    private Long scheduleId;
    private String date;
    private String content;
    private ScheduleState state;
    private String ddl;
}
