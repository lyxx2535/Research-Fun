package nju.researchfun.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum LanguageType implements IEnum<Integer> {
    ZH(1, "zh"),         //中文
    EN(2, "en"),         //英语
    JP(3, "jp"),         //日语
    KOR(4, "kor"),       //韩语
    YUE(5, "yue"),       //粤语
    RU(6, "ru"),         //俄语
    DE(7, "de"),         //德语
    FRA(8, "fra"),       //法语
    TH(9, "th"),         //泰语
    PT(10, "pt"),        //葡萄牙语
    SPA(11, "spa"),      //西班牙语
    ARA(12, "ara"),      //阿拉伯语

    ;
    private final int value;
    private final String desc;

    LanguageType(int value, String desc) {
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
