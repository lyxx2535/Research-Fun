package com.example.rgms.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserTrueNameAndEmailAndPortrait {
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;
    @ApiModelProperty(value = "用户真名", example = "王志强")
    private String trueName;
    @ApiModelProperty(value = "用户邮箱", example = "wzqiang@163.com")
    private String email;
    @ApiModelProperty(value = "用户头像地址", example = "/usr/local/rgms-resources/portrait/76afa191-3d2c-4c10-b969-b4b048630523_胡桃.jpg")
    private String portrait;
}
