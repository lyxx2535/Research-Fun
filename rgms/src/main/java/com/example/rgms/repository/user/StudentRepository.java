package com.example.rgms.repository.user;

import com.example.rgms.entity.user.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    List<StudentEntity> findByTeacherId(Long teacherId);

    @Query(nativeQuery = true,
            value = "select s.* " +
                    "from (student s left join user u on s.user_id=u.id) " +
                    "left join research_group r on u.group_id=r.id " +
                    "where u.username like concat('%', ?1, '%') or u.true_name like concat('%', ?1, '%')")
    List<StudentEntity> findStudentByUsernameAndTrueName(String key);
}
