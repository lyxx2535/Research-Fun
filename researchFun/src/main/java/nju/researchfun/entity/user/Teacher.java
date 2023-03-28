package nju.researchfun.entity.user;

import nju.researchfun.entity.user.User;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.user.UserSimpleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导师实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {

    private Long uid;

    public UserSimpleInfo toTeacherSimpleInfo(UserService userService) {
        UserSimpleInfo res = new UserSimpleInfo();
        res.setUserId(this.uid);
        User user = userService.getUserById(this.uid);
        res.setUsername(user.getUsername());
        res.setTrueName(user.getTrueName());
        return res;
    }
}
