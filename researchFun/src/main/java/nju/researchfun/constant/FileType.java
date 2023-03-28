package nju.researchfun.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum FileType implements IEnum<Integer> {
    PORTRAIT(1, "PORTRAIT"), // 头像图片
    PDF_DOCUMENT(2, "PDF_DOCUMENT"), // pdf文献
    COMMENT_IMG(3, "COMMENT_IMG"), // 批注图片

    VOICE(4, "VOICE"), // 语音

    ;
    private final int value;
    private final String desc;

    FileType(int value, String desc) {
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
