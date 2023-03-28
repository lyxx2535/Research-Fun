package nju.researchfun.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfoVO {
    @ApiModelProperty(value = "content", example = "1")
    private Content content;
    @ApiModelProperty(value = "position", example = "1")
    private Poisition position;
    @ApiModelProperty(value = "comment", example = "1")
    private Comment comment;
    @ApiModelProperty(value = "id", example = "1")
    private int id;
    @ApiModelProperty(value = "id", example = "1")
    private Infos infos;

}
