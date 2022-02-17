package com.example.rgms.service.impl;

import com.example.rgms.constant.MessageState;
import com.example.rgms.constant.MessageType;
import com.example.rgms.entity.MessageEntity;
import com.example.rgms.exception.BadRequestException;
import com.example.rgms.exception.NotFoundException;
import com.example.rgms.repository.MessageRepository;
import com.example.rgms.service.MessageService;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    @Lazy
    private UserService userService;


    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository){
        this.messageRepository=messageRepository;
    }

    @Override
    public Page<MessageVO> getAllMessagesByUserId(Long userId, int currentPage, int pageSize) {
        Page<MessageEntity> entityPage=messageRepository.findByRecipientOrderByDateDesc(userId, PageRequest.of(currentPage, pageSize));
        List<MessageVO> list=new ArrayList<>(entityPage.getNumberOfElements());
        for(MessageEntity messageEntity : entityPage){
            list.add(messageEntity.toMessageVO(userService));
        }
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), entityPage.getTotalElements());
    }

    @Override
    public String markMessageRead(Long messageId) {
        MessageEntity messageEntity=getMessageEntityById(messageId);
        messageEntity.setState(MessageState.READ);
        messageRepository.save(messageEntity);
        return "success";
    }

    @Override
    public void sendMessage(Long sender, Long recipient, String body) {
        this.sendMessage(sender, recipient, body, MessageType.DEFAULT, null);
    }

    @Override
    public void sendMessage(Long sender, Long recipient, String body, MessageType type, Long extraInfoId) {
        messageRepository.save(MessageEntity.builder()
                .sender(sender)
                .recipient(recipient)
                .body(body)
                .state(MessageState.UNREAD)
                .date(new Date())
                .type(type)
                .extraInfoId(extraInfoId)
                .build());
    }

    @Override
    public String deleteMessage(Long messageId) {
        MessageEntity messageEntity=getMessageEntityById(messageId);
        messageRepository.delete(messageEntity);
        return "success";
    }

    @Override
    public MessageEntity getMessageEntityById(Long messageId){
        if(messageId==null)
            throw new BadRequestException("信息id不能为null");
        Optional<MessageEntity> maybeEntity=messageRepository.findById(messageId);
        if(!maybeEntity.isPresent())
            throw new NotFoundException("不存在id为"+messageId+"的消息");
        return maybeEntity.get();
    }

    @Override
    public MessageEntity saveMessageEntity(MessageEntity entity) {
        return messageRepository.save(entity);
    }

    @Override
    public boolean hasAppliedToJoinGroup(Long userId) {
        return messageRepository.existsBySenderAndType(userId, MessageType.APPLY_TO_JOIN_GROUP);
    }

    @Override
    public boolean hasAppliedToExitGroup(Long userId) {
        return messageRepository.existsBySenderAndType(userId, MessageType.APPLY_TO_EXIT_GROUP);
    }

    @Override
    public void markMessageBatchRead(List<Long> messageIds) {
        messageRepository.markMessagesRead(messageIds);
    }

    @Override
    public void deleteMessageBatch(List<Long> messageIds) {
        messageRepository.deleteAllById(messageIds);
    }

    @Override
    public int getUnreadMessageNum(Long userId) {
        return messageRepository.countUnreadByRecipient(userId);
    }
}
