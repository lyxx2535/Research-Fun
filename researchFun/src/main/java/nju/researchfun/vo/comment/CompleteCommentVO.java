package nju.researchfun.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@ApiModel
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteCommentVO {
    @ApiModelProperty(value = "content", example = "1")
    private Content content;
    @ApiModelProperty(value = "position", example = "1")
    private Poisition position;
    @ApiModelProperty(value = "comment", example = "1")
    private Comment comment;
    @ApiModelProperty(value = "id", example = "1")
    private Long id;
    @ApiModelProperty(value = "infos", example = "1")
    private UserInfos userInfos;
}
