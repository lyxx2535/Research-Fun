package nju.researchfun.vo.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nju.researchfun.constant.UserType;
import lombok.Builder;
import lombok.Data;

/**
 * 登录信息返回
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRet {
    private Long userId;
    private UserType userType;
    private String token;
    /**
     * 用于微信小程序端判断是否绑定微信的标志
     */
    private boolean isBound;
}
