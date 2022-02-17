package com.example.rgms.repository.Search;

import com.example.rgms.entity.document.docEntity;
import com.example.rgms.entity.document.keywordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//Java实现不同的查询结果并集
public interface SearchdocRepository  extends JpaRepository<docEntity,Long> {
    //Page<ScheduleEntity> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);

    @Query(nativeQuery = true,
            countQuery = "select  count(1) from doc d1 where d1.title like concat ('%',?1,'%') ",
            value = " SELECT d.* from doc d where d.title like concat ('%',?1,'%')  ")
    Page<docEntity> findByTitle(String keywords, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT d.* from doc d join keyword_doc kd " +
                    "on d.did= kd.did join keyword k " +
                    "on kd.kid = k.kid " +
                    "where k.kname like concat('%', ?1, '%')",
            countQuery = "SELECT count(*) from doc d join keyword_doc kd on d.did= kd.did" +
                    " join keyword k  on kd.kid = k.kid " +
                    "where k.kname like concat('%', ?1, '%')"
    )

    Page<docEntity> findBykeyword(String keyword,Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "select d.* from doc d join author_doc  ad " +
                    "on d.did= ad.did join author a " +
                    "on ad.auid = a.au_id" +
                    " where a.aname  like concat('%', ?1, '%')",
            countQuery = "select  count(1) from doc d join author_doc  ad " +
                    "                    on d.did= ad.did join author a " +
                    "                    on ad.auid = a.au_id" +
                    "                     where a.aname  like concat('%', ?1, '%') "
    )
    Page<docEntity> findByauthor(String keyword, Pageable pageable);

    Page<docEntity> findByUserid(int userid,Pageable pageable);


    docEntity findByTitleAndUserid(String title,int userid);

    @Query(
            nativeQuery = true,
            value = "select d.* from doc d ",
            countQuery = "select  count(*) from doc d"
    )
    Page<docEntity> findAll(Pageable pageable);
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM doc d join author_doc ad on d.did = ad.did " +
                    "join author a on ad.auid= a.au_id " +
                    "join keyword_doc kd on d.did = kd.did " +
                    " join keyword k on kd.kid=k.kid " +
                    "where d.title like concat('%',?1,'%') " +
                    "and if(?2 is not null,a.aname in authors,1=1)"
    )

    List<docEntity> findalldocs(@Param("title") String title, @Param("authors") List<String> authors);

    docEntity findByDid(int did);








    @Query(
            nativeQuery = true,
            value = "select d.* from doc d  " +
                    " where d.title  like concat('%', ?1, '%')  order by d.date desc"
    )
    List<docEntity> findByTitle(String title);



    @Query(
            nativeQuery = true,
            value = "select d.* from doc d join author_doc  ad " +
                    "on d.did= ad.did join  author a " +
                    "on ad.auid = a.au_id" +
                    " where a.aname  like concat('%', ?1, '%') order by d.date desc"
    )

    List<docEntity> findByauthor(String keyword);

    @Query(nativeQuery = true,
            value = "SELECT d.* from doc d join keyword_doc kd " +
                    "on d.did= kd.did join  keyword k " +
                    "on kd.kid = k.kid " +
                    "where k.kname like concat('%', ?1, '%') order by d.date desc"
    )
    List<docEntity> findByKeywords(String keyword);

    List<docEntity> findByUserid(int userid);

    @Query(
            nativeQuery = true,
            value = "select d.* from  doc d  order by d.date desc"
    )
    List<docEntity> findAll();

   // void






}
