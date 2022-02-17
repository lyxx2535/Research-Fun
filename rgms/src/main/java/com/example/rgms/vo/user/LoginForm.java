package com.example.rgms.vo.user;

import com.example.rgms.constant.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class LoginForm {
    @ApiModelProperty(value = "用户名", example = "123456")
    private String username;
    @ApiModelProperty(value = "密码", example = "123456")
    private String password;
}
