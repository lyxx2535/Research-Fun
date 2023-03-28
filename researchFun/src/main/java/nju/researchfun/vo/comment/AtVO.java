package nju.researchfun.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class AtVO {
    @ApiModelProperty(value = "sendId", example = "1")
    private Long sendId;
    @ApiModelProperty(value = "sendName", example = "sendName")
    private String sendName;
    @ApiModelProperty(value = "receiverId", example = "10")
    private Long receiverId;
    @ApiModelProperty(value = "comment", example = "comment")
    private String comment;
    @ApiModelProperty(value = "notice", example = "notice")
    private String notice;
    @ApiModelProperty(value = "did", example = "27")
    private Long did;

    @ApiModelProperty(value = "content", example = "content")
    private String content;
    @ApiModelProperty(value = "position", example = "position")
    private String position;
    @ApiModelProperty(value = "id", example = "1")// 评论id
    private Long id;
}
