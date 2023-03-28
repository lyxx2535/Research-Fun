package nju.researchfun.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum MessageType implements IEnum<Integer> {
    DEFAULT(1, "DEFAULT"),               // 默认消息格式。这种消息没有附带信息，格式简单，只有消息自己的信息内容
    SCHEDULE_RELATIVE(2, "SCHEDULE_RELATIVE"),       // 日程相关消息。这个消息会附带日程id，方便查看相关的日程内容
    APPLY_TO_JOIN_GROUP(3, "APPLY_TO_JOIN_GROUP"),   // 申请加入研究组消息。这个消息不会附加内容，需要的发送者信息在senderInfo中已经有了
    APPLY_TO_EXIT_GROUP(4, "APPLY_TO_EXIT_GROUP"),   // 申请退出研究组消息
    DOC_AT(5, "DOC_AT"),                             // 文献消息提醒
    ;

    private final int value;
    private final String desc;

    MessageType(int value, String desc) {
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
