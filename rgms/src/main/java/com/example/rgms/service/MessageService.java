package com.example.rgms.service;

import com.example.rgms.constant.MessageType;
import com.example.rgms.entity.MessageEntity;
import com.example.rgms.vo.MessageVO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MessageService {
    Page<MessageVO> getAllMessagesByUserId(Long userId, int currentPage, int pageSize);

    String markMessageRead(Long messageId);

    void sendMessage(Long sender, Long recipient, String body);

    void sendMessage(Long sender, Long recipient, String body, MessageType type, Long extraInfoId);

    String deleteMessage(Long messageId);

    void markMessageBatchRead(List<Long> messageIds);

    void deleteMessageBatch(List<Long> messageIds);

    int getUnreadMessageNum(Long userId);

    MessageEntity getMessageEntityById(Long messageId);

    MessageEntity saveMessageEntity(MessageEntity entity);

    boolean hasAppliedToJoinGroup(Long userId);

    boolean hasAppliedToExitGroup(Long userId);
}
