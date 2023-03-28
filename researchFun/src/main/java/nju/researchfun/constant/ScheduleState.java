package nju.researchfun.constant;


import com.baomidou.mybatisplus.core.enums.IEnum;

public enum ScheduleState implements IEnum<Integer> {
    UNFINISHED(1, "UNFINISHED"),     // 未完成
    FINISHED(2, "FINISHED"),       // 已完成
    DELETED(3, "DELETED"),        // 已删除
    ;
    private final int value;
    private final String desc;

    ScheduleState(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    public static ScheduleState parse(Integer v) {
        for (ScheduleState scheduleState : values()) {
            if (scheduleState.value == v) {
                return scheduleState;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
