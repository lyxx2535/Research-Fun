package com.example.rgms.repository.comment;

import com.example.rgms.entity.comment.ReplyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReplyRepository extends JpaRepository<ReplyEntity,Long> {
    Page<ReplyEntity> findByCommentid(int commentid, Pageable pageable);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from reply where commentid = ?1")
    void deleteAllByCommentid(int commentid);
}
