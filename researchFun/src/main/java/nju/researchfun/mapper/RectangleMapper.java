package nju.researchfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.doc.Rectangle;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RectangleMapper extends BaseMapper<Rectangle> {
}
