package com.example.rgms.entity;

import com.example.rgms.constant.MessageState;
import com.example.rgms.constant.MessageType;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.MessageVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long sender;        // 发送者
    private Long recipient;     // 接收者


    /**
     * 消息主体。比如：
     * 您的学生{sender}完成了日程
     * {sender}同学上传了一篇新的文献
     * 您的学生{sender}未能完成任务！
     * ……
     * 其中{sender}是特意空出来方便前端对发送者进行高亮而设置的
     */
    private String body;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date date;    // 时间戳，年月日时分秒
    private MessageState state;
    private MessageType type;
    private Long extraInfoId;

    public MessageVO toMessageVO(UserService userService){
        DateFormat format=DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG, Locale.CHINA);

        return MessageVO.builder()
                .id(id)
                .senderInfo(sender==null ? null : userService.getUserSimpleInfoById(sender))
                .body(body)
                .date(format.format(date))
                .state(state)
                .type(type)
                .extraInfoId(extraInfoId)
                .build();
    }
}
