package com.example.rgms.repository.user;

import com.example.rgms.entity.user.UserEntity;
import com.example.rgms.service.UserService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsername(String username);

    UserEntity findByUsername(String username);

    List<UserEntity> findByGroupId(Long groupId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update user u set u.group_id=null where u.group_id=?1")
    void allMemberExitGroup(Long groupId);

    @Query(nativeQuery = true,
            value = "SELECT u1.id FROM  user u1 " +
                    "where u1. group_id = (" +
                    "       select u2.group_id from rgms.user u2 where u2.id= ?1" +
                    ");"
    )
    List<Integer> findUseridBySameGroup(int userid);
}
