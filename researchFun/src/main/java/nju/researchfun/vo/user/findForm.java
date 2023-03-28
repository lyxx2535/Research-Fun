package nju.researchfun.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class findForm {
    @ApiModelProperty(value = "用户新密码", example = "123456")
    private String password;
    @ApiModelProperty(value = "用户邮箱", example = "201830168@smail.edu.nju.cn")
    private String email;
}
