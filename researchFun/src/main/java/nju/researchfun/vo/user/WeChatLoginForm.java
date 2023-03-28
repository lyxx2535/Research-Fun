package nju.researchfun.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信登录表单
 */
@Data
@ApiModel
public class WeChatLoginForm {
    @ApiModelProperty(value = "用户的code", example = "001uns000xW1EN1zwU2004y8uh1uns0j")
    private String code;
    @ApiModelProperty(value = "防止恶意攻击的验证码", example = "4BED32CC33314317022062DB8D93B1C8")
    private String state;
}
