package nju.researchfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.user.UserOpenId;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserOpenIdMapper extends BaseMapper<UserOpenId> {
    int insert(UserOpenId userOpenId);
    UserOpenId check(String openId);
    UserOpenId checkByUserId(Long userId);
    int updateByOpenId(UserOpenId userOpenId);
}
