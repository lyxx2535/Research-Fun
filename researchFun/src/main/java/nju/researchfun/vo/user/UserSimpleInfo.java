package nju.researchfun.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel
public class UserSimpleInfo {
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;
    @ApiModelProperty(value = "用户名", example = "123456")
    private String username;
    @ApiModelProperty(value = "用户真名", example = "张三")
    private String trueName;
    @ApiModelProperty(value = "用户头像地址", example = "/usr/local/rgms-resources/portrait/76afa191-3d2c-4c10-b969-b4b048630523_胡桃.jpg")
    private String portrait;
}
