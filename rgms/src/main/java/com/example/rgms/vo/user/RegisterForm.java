package com.example.rgms.vo.user;

import com.example.rgms.constant.UserType;
import com.example.rgms.entity.user.UserEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import springfox.documentation.annotations.ApiIgnore;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RegisterForm {
    @ApiModelProperty(value = "用户名", example = "123456")
    private String username;
    @ApiModelProperty(value = "密码", example = "123456")
    private String password;
    @ApiModelProperty(value = "用户类型", example = "TEACHER")
    private UserType userType;
    @ApiModelProperty(value = "电子邮箱", example = "123456@123.com")
    private String email;

    /**
     * 得到userEntity，不包括groupId，groupId的绑定通过另外的接口
     * @return UserEntity
     */
    public UserEntity toUserEntity(){
        return UserEntity.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
    }
}
