package nju.researchfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.doc.Doc_Author;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface Doc_AuthorMapper extends BaseMapper<Doc_Author> {
}
