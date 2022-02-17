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
public class Content {
    @ApiModelProperty(value = "text", example = "内容信息")
    private  String text;
    @ApiModelProperty(value = "image", example = "图片信息")
    private String image;



}
