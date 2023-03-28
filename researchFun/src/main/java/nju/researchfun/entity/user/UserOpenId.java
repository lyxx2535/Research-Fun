package nju.researchfun.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("t_user_openid")
public class UserOpenId {
    private Long id;
    private Long userId;            // user_id
    private String openId;          // open_id

    public UserOpenId(Long userId, String openId) {
        this.userId = userId;
        this.openId = openId;
    }
}
