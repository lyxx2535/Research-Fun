package com.example.rgms.repository.Search;

import com.example.rgms.entity.document.keywordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchkeywordRepository extends JpaRepository<keywordEntity,Long> {
    Page<keywordEntity> findByKname(String kname, Pageable pageable);
    boolean existsByKname(String kname);

    keywordEntity  findByKname(String kname);

    @Query(
            nativeQuery = true,
            value = "SELECT  k.* FROM " +
                    "keyword k left join keyword_doc  kd on" +
                    " k.kid =kd.kid where kd.did=?1"
    )
    List<keywordEntity> findBydId(int did);
}
