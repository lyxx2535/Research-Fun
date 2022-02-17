package com.example.rgms.repository.Search;


import com.example.rgms.entity.document.Keyword_DocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface Keyword_DocRepository extends JpaRepository<Keyword_DocEntity,Long> {
    Keyword_DocEntity findByKidAndDid(int kid,int Did );
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from keyword_doc where did = ?1")
    void  deleteAllByDid(int did);
}
