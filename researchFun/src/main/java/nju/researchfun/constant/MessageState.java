package nju.researchfun.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum MessageState implements IEnum<Integer> {
    UNREAD(0, "UNREAD"),     // 未读
    READ(1, "READ"),       // 已读
    ;
    private final int value;
    private final String desc;

    MessageState(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
