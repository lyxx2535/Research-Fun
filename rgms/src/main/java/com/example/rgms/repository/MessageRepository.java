package com.example.rgms.repository;

import com.example.rgms.constant.MessageType;
import com.example.rgms.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    Page<MessageEntity> findByRecipientOrderByDateDesc(Long recipient, Pageable pageable);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "update message m set m.state=1 where m.id in ?1")
    void markMessagesRead(List<Long> messageIds);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from message where id in ?1")
    void deleteAllById(List<Long> messagesIds);

    @Query(nativeQuery = true, value = "select count(*) from message where recipient=?1 and state=0")
    int countUnreadByRecipient(Long recipientId);

    boolean existsBySenderAndType(Long sender, MessageType type);
}
