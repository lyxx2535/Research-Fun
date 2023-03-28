package nju.researchfun.vo.user;

import nju.researchfun.constant.UserType;
import nju.researchfun.entity.user.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RegisterForm {
    @ApiModelProperty(value = "用户名", example = "hhhhhh")
    private String username;
    @ApiModelProperty(value = "密码", example = "123456")
    private String password;
    @ApiModelProperty(value = "用户类型", example = "TEACHER")
    private UserType userType;
    @ApiModelProperty(value = "电子邮箱", example = "201830168@smail.nju.edu.cn")
    private String email;
    @ApiModelProperty(value = "验证码", example = "ACSN")
    private String check;
    /**
     * 用于微信小程序端绑定微信的code
     */
    private String code;
    @ApiModelProperty(value = "sessionId",example = "ABA42DBD75C15CD12EB0C75FA32A44F4")
    private String sessionId;

    /**
     * 得到 user，不包括groupId，groupId的绑定通过另外的接口
     * @return User
     */
    public User toUser(){
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .userType(userType.toString())
                .build();
    }
}
