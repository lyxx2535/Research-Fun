package nju.researchfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
    /**
     * 查找该研究组所有组员
     */
    List<User>  findByGroupId(long groupId);

    /**
     * 查找该研究组所有导师
     */
    List<User> getAllTeasInGroup(Long groupid);

    /**
     * 查找该研究组所有学生
     */
    List<User> getAllStusInGroup(Long groupid);
}
