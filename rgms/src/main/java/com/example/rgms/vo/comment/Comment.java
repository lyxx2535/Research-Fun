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
public class Comment {
    @ApiModelProperty(value = "text", example = "内容")
    private String text;
    @ApiModelProperty(value = "emoji", example = "表情")
    private String emoji;

}
