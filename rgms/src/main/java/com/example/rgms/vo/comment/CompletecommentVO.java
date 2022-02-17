package com.example.rgms.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@ApiModel
@Getter
@Setter
public class CompletecommentVO {
    @ApiModelProperty(value = "content", example = "1")
    private Content content;
    @ApiModelProperty(value = "poisition", example = "1")
    private Poisition position;
    @ApiModelProperty(value = "comment", example = "1")
    private Comment comment;
    @ApiModelProperty(value = "id", example = "1")
    private int id;
    @ApiModelProperty(value = "infos", example = "1")
    private UserInfos userInfos;
}
