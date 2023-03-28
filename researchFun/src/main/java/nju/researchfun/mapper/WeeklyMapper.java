package nju.researchfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.weekly.Weekly;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


@Mapper
@Repository
public interface WeeklyMapper extends BaseMapper<Weekly> {


}
