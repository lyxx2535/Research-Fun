package nju.researchfun.vo.document;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class ReadVO {
    @ApiModelProperty(value = "文件id", example = "1")
    private Long did;
    @ApiModelProperty(value = "用户id", example = "1")
    private Long uid;
    @ApiModelProperty(value = "用户所在研究组id", example = "1")
    private Long gid;
}
