package nju.researchfun.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserIdAndPwd {
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;
    @ApiModelProperty(value = "用户旧密码", example = "123456")
    private String oldPwd;
    @ApiModelProperty(value = "用户新密码", example = "123456")
    private String newPwd;
}
