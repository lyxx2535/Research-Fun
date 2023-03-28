package nju.researchfun.entity.user;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.user.UserSimpleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 团队成员实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends Model<Student> {


    private Long uid;
    private String defaultResearchDirection;//默认研究对象
    //private Long teacherId;//导师id 可能会有修改

    public UserSimpleInfo toUserSimpleInfo(UserService userService) {
        UserSimpleInfo res = new UserSimpleInfo();
        res.setUserId(this.uid);
        User user = userService.getUserById(this.uid);
        res.setUsername(user.getUsername());
        res.setTrueName(user.getTrueName());
        return res;
    }
}
