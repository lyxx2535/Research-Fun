package nju.researchfun.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nju.researchfun.entity.user.User_Password;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface User_PasswordMapper extends BaseMapper<User_Password> {
}
