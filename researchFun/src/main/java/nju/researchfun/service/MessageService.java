package nju.researchfun.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import nju.researchfun.constant.MessageType;
import nju.researchfun.entity.Message;
import nju.researchfun.vo.message.MessageVO;

import java.util.List;

public interface MessageService {
    boolean hasAppliedToJoinGroup(Long userId, String body);

    boolean hasAppliedToExitGroup(Long userId, String body);

    void sendMessage(Long sender, Long recipient, String body);

    void sendMessage(Long sender, Long recipient, String body, MessageType type, Long extraInfoId);

    //新增
    void sendMessage(Long sender, Long recipient, String body, MessageType type, Long extraInfoId, String extraInfo);

    String markMessageRead(Long messageId);

    Message getMessageById(Long messageId);

    int getUnreadMessageNum(Long userId);

    Message insertMessage(Message entity);

    Message updateMessageById(Message entity);

    String deleteMessage(Long messageId);

    void messageBatch(List<Long> messageIds, int key);

//    void markMessageBatchRead(List<Long> messageIds);

//    void deleteMessageBatch(List<Long> messageIds);

    IPage<MessageVO> getAllMessagesByUserId(Long userId, int currentPage, int pageSize);

    IPage<MessageVO> getAllMessagesByUserIdAndKey(Long userId, int currentPage, int pageSize, int key);
}
