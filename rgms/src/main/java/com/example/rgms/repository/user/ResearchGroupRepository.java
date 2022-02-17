package com.example.rgms.repository.user;

import com.example.rgms.entity.ResearchGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResearchGroupRepository extends JpaRepository<ResearchGroupEntity, Long> {
    boolean existsByGroupName(String groupName);
}
