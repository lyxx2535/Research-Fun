package nju.researchfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.ResearchGroup;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ResearchGroupMapper extends BaseMapper<ResearchGroup> {


    /**
     * 根据用户 id 查找该用户的所处研究组
     */
    List<ResearchGroup> findByUid(long uid);

}
