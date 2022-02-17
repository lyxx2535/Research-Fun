package com.example.rgms.repository.Search;

import com.example.rgms.entity.document.author_docEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface author_docRepository extends JpaRepository<author_docEntity,Long> {
    author_docEntity findByAuidAndDid(int auid,int did);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from author_doc where did = ?1")
    void deleteAllByDid(int did);
}
