package nju.researchfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ScheduleMapper extends BaseMapper<Schedule> {

    List<Schedule> findByGroupIdAndDate(Long groupId, int year, int month);

    List<Schedule> findByDDL(int year, int month, int date);

    List<Schedule> findByUserIdAndYearAndMonth(Long accepterId, int year, int month);

}
