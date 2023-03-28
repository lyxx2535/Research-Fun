package nju.researchfun.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.user.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TeacherMapper extends BaseMapper<Teacher> {

}
