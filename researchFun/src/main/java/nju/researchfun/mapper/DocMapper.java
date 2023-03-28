package nju.researchfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.doc.Doc;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DocMapper extends BaseMapper<Doc> {
    List<Doc> selectByKeyword(String keyword);

    List<Doc> selectByAuthorName(String keyword);

}
