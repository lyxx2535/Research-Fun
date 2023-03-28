package nju.researchfun.entity.user;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.constant.UserStatus;
import nju.researchfun.vo.user.UserSimpleInfo;

/**
 * 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Model<User> {

    @TableId
    private Long id;            // id
    private String username;    // 用户名
    private String password;    // 密码
    private String email;       // 用户邮箱
    private Long groupId;       // 用户当前的项目组id
    private String trueName;    // 用户真实姓名
    private String portrait;    // 用户头像图片地址
    private String userType;    // 用户类别

    private UserStatus status;
    //@TableField(select = false)
    private String code;        // 激活码 不加入查询字段

    private String direction;   // 研究方向

    public UserSimpleInfo toUserSimpleInfo() {
        return UserSimpleInfo.builder()
                .userId(id)
                .username(username)
                .trueName(trueName)
                .portrait(portrait)
                .build();
    }
}
