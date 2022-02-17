package com.example.rgms.repository.comment;

import com.example.rgms.entity.comment.RectangleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RectangleRepository extends JpaRepository<RectangleEntity,Long> {
    List<RectangleEntity>  findByCommentid(int commentid);

    @Override
    void delete(RectangleEntity rectangleEntity);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from rectangle where commentid = ?1")
    void deleteAllByCommentid(int commentid);
}
