package nju.researchfun.vo.message;

import nju.researchfun.constant.MessageState;
import nju.researchfun.constant.MessageType;
import nju.researchfun.vo.user.UserSimpleInfo;
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
    private String extraInfo;
}
