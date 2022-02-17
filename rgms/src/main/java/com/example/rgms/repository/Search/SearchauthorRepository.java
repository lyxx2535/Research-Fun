package com.example.rgms.repository.Search;

import com.example.rgms.entity.document.authorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchauthorRepository  extends JpaRepository<authorEntity,Long> {

   @Query(
           nativeQuery = true,
           value = "select a.au_id,a.aname from author a  " +
                   "where  a.aname  like concat('%', ?1, '%') "
   )
    List<authorEntity> findauthorEntitiesByAname(String Aname);
   boolean existsByAname(String Aname);
   authorEntity findByAname(String Aname);

   // boolean existsByanme(String name);
   @Query(
           nativeQuery = true,
           value = "SELECT distinct a.au_id,a.aname FROM " +
                   "author a join author_doc ad " +
                   "on a.au_id =ad.auid where ad.did=?1"
   )
    List<authorEntity> findauthorEntitiesBydId(int did);


}
