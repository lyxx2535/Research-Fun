package com.example.rgms.vo;

import com.example.rgms.constant.MessageState;
import com.example.rgms.constant.MessageType;
import com.example.rgms.vo.user.UserSimpleInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageVO {
    private Long id;
    private UserSimpleInfo senderInfo;
    private String body;
    private String date;
    private MessageState state;
    private MessageType type;
    private Long extraInfoId;
}
