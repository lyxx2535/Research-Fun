package nju.researchfun.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录表单VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class LoginForm {
    @ApiModelProperty(value = "用户名", example = "123456")
    private String username;
    @ApiModelProperty(value = "密码", example = "123456")
    private String password;
    @ApiModelProperty(value = "验证码", example = "A1S2")
    private String check;
    /**
     * 用于微信小程序端绑定微信的code
     */
    private String code;
    @ApiModelProperty(value = "sessionId", example = "ABA42DBD75C15CD12EB0C75FA32A44F4")
    private String sessionId;
}
