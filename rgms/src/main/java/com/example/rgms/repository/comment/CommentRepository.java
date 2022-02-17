package com.example.rgms.repository.comment;

import com.example.rgms.entity.comment.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity,Long > {
    List<CommentEntity>  findByDid(int did);

    List<CommentEntity> findByDidOrderByDate(int did);

    CommentEntity findById(int id);




    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from comment where id = ?1")
    void deleteById(int id);

}
