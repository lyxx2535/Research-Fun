package nju.researchfun.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum CommentType implements IEnum<Integer> {
    TEXT(0, "TEXT"), // 文字
    IMAGE(1, "IMAGE"), // 图片

    ;
    private final int value;
    private final String desc;

    CommentType(int value, String desc) {
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
