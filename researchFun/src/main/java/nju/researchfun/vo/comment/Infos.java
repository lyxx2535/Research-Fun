package nju.researchfun.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@ApiModel
@Getter
@Setter
public class Infos {
    @ApiModelProperty(value = "userid", example = "用户id")
    private Long userid;
    @ApiModelProperty(value = "did", example = "文章id")
    private Long did;
}
