package nju.researchfun.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import nju.researchfun.constant.MessageState;
import nju.researchfun.constant.MessageType;
import nju.researchfun.entity.Message;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.NotFoundException;
import nju.researchfun.mapper.MessageMapper;
import nju.researchfun.service.MessageService;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.message.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void sendMessage(Long sender, Long recipient, String body, MessageType type, Long extraInfoId, String extraInfo) {
        messageMapper.insert(Message.builder()
                .sender(sender)
                .recipient(recipient)
                .body(body)
                .state(MessageState.UNREAD)
                .date(new Date())
                .type(type)
                .extraInfoId(extraInfoId)
                .extraInfo(extraInfo)
                .build());
    }


    @Override
    public void sendMessage(Long sender, Long recipient, String body, MessageType type, Long extraInfoId) {
        this.sendMessage(sender, recipient, body, type, extraInfoId, null);
    }

    @Override
    public void sendMessage(Long sender, Long recipient, String body) {
        this.sendMessage(sender, recipient, body, MessageType.DEFAULT, null);
    }

    @Override
    public String markMessageRead(Long messageId) {
        Message message = getMessageById(messageId);
        message.setState(MessageState.READ);
        messageMapper.updateById(message);
        return "success";
    }

    @Override
    public Message getMessageById(Long messageId) {
        if (messageId == null)
            throw new BadRequestException("信息id不能为null");
        Message message = messageMapper.selectById(messageId);
        if (message == null)
            throw new NotFoundException("不存在id为" + messageId + "的消息");
        return message;
    }

    @Override
    public int getUnreadMessageNum(Long userId) {
        Message message = new Message();
        message.setRecipient(userId);
        QueryWrapper<Message> wrapper = new QueryWrapper<>(message);
        return messageMapper.selectCount(wrapper);
    }

    @Override
    public Message insertMessage(Message message) {
        messageMapper.insert(message);
        return message;
    }

    @Override
    public Message updateMessageById(Message message) {
        messageMapper.updateById(message);
        return message;
    }

    @Override
    public String deleteMessage(Long messageId) {
        try {
            messageMapper.deleteById(messageId);
        } catch (Exception e) {
            throw new BadRequestException("该消息不存在！");
        }
        return "success";
    }

    @Override
    public void messageBatch(List<Long> messageIds, int key) {
        switch (key) {
            case 1: //标记已读
                markMessageBatchRead(messageIds);
                break;
            case 2: //全部删除
                deleteMessageBatch(messageIds);
                break;
            default:
                throw new BadRequestException("不正确的参数!");
        }
    }

    private void markMessageBatchRead(List<Long> messageIds) {
        Message message = new Message();
        message.setState(MessageState.READ);//更新的字段
        UpdateWrapper<Message> wrapper = new UpdateWrapper<>();
        wrapper.in("id", messageIds);
        messageMapper.update(message, wrapper);
    }

    private void deleteMessageBatch(List<Long> messageIds) {
        messageMapper.deleteBatchIds(messageIds);
    }


    @Override
    public boolean hasAppliedToJoinGroup(Long userId, String body) {
        return hasApplied(userId, body, MessageType.APPLY_TO_JOIN_GROUP);
    }

    @Override
    public boolean hasAppliedToExitGroup(Long userId, String body) {
        return hasApplied(userId, body, MessageType.APPLY_TO_EXIT_GROUP);
    }

    private boolean hasApplied(Long userId, String body, MessageType messageType) {
        Message message = new Message();
        message.setType(messageType);
        message.setSender(userId);
        message.setBody(body);
        List<Message> list = messageMapper.selectList(new QueryWrapper<>(message));
        return list != null && list.size() > 0;
    }

    @Override
    public IPage<MessageVO> getAllMessagesByUserId(Long userId, int currentPage, int pageSize) {
        Page<Message> page = new Page<>(currentPage, pageSize);

        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("recipient", userId);
        wrapper.orderByDesc("date");

        IPage<Message> iPage = messageMapper.selectPage(page, wrapper);

        List<MessageVO> list = new ArrayList<>();
        for (Message message : iPage.getRecords()) {//把信息拿出来并做清洗
            list.add(message.toMessageVO(userService));
        }
        Page<MessageVO> res = new Page<>(currentPage, pageSize, iPage.getTotal());
        res.setRecords(list);
        return res;
    }

    @Override
    public IPage<MessageVO> getAllMessagesByUserIdAndKey(Long userId, int currentPage, int pageSize, int key) {
        Page<Message> page = new Page<>(currentPage, pageSize);

        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("recipient", userId);
        wrapper.orderByDesc("date");
        switch (key) {
            case 0:
                break;
            case 1://查询研究组消息
                Integer[] in = new Integer[]{1,3,4};
                wrapper.in("type", Arrays.asList(in));
                break;
            case 2://查询日程相关消息
                wrapper.eq("type", 2);
                break;
            case 3://查询文献相关信息
                wrapper.eq("type", 5);
                break;
            default:
                throw new BadRequestException("不正确的参数！");
        }

        IPage<Message> iPage = messageMapper.selectPage(page, wrapper);

        List<MessageVO> list = new ArrayList<>();
        for (Message message : iPage.getRecords()) {//把信息拿出来并做清洗
            list.add(message.toMessageVO(userService));
        }
        Page<MessageVO> res = new Page<>(currentPage, pageSize, iPage.getTotal());
        res.setRecords(list);
        return res;
    }
}
