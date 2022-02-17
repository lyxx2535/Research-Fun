package com.example.rgms.repository;

import com.example.rgms.entity.ScheduleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    Page<ScheduleEntity> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "select s.* from schedule s " +
                    "where s.user_id=?1 and s.state<>2 " +
                    "order by s.date desc",
            countQuery = "select count(s.id) from schedule s " +
                    "where s.user_id=?1 and s.state<>2 "
    )
    Page<ScheduleEntity> findByUserId(Long userId, Pageable pageable);

    @Query(
            nativeQuery = true,
            value = "select s.* from schedule s left join user u on s.user_id=u.id " +
                    "where u.group_id=?1 and year(s.date)=?2 and month(s.date)=?3 and s.state<>2"
    )
    List<ScheduleEntity> findByGroupId(Long groupId, int year, int month);

    @Query(
            nativeQuery = true,
            value = "select s.* from schedule s where s.user_id=?1 and year(s.date)=?2 and month(s.date)=?3 " +
                    "and s.state<>2"
    )
    List<ScheduleEntity> findByUserIdAndYearAndMonth(Long userId, int year, int month);

    @Query(
            nativeQuery = true,
            value = "select * from schedule s where state=0 and " +
                    "year(date)=?1 and month(date)=?2 and day(date)=?3"
    )
    List<ScheduleEntity> findUnfinishedScheduleByDate(int year, int month, int day);

    @Query(
            nativeQuery = true,
            value = "select count(id) from schedule where user_id=?1 and date=?2 and state<>2"
    )
    int countByUserIdAndDateAndState(Long userId, Date date);
}
