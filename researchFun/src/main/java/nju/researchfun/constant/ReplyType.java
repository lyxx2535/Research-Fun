package nju.researchfun.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum ReplyType implements IEnum<Integer> {
    TEXT(1, "TEXT"), // 文字
    IMAGE(0, "IMAGE"), // 图片

    ;
    private final int value;
    private final String desc;

    ReplyType(int value, String desc) {
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
