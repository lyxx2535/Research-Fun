package nju.researchfun.vo.schedule;

import nju.researchfun.constant.ScheduleState;
import nju.researchfun.vo.user.UserSimpleInfo;
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
