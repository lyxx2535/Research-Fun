package nju.researchfun.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum UserStatus implements IEnum<Integer> {
    ACTIVATED(1,"ACTIVATED"),     // 已激活
    UNACTIVATED(0,"UNACTIVATED"), // 未激活
    ;

    private final int value;
    private final String desc;

    UserStatus(int value, String desc) {
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
