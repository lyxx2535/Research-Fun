package nju.researchfun.vo.user;

import nju.researchfun.constant.UserType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class UserIdAndUserType {
    @ApiModelProperty(value = "用户id", example = "123456")
    private Long userId;
    @ApiModelProperty(value = "用户类型", example = "STUDENT")
    private UserType userType;
}
