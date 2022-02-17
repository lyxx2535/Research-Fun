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
public class Rectangleinfo {
    @ApiModelProperty(value = "id", example = "1")
    private int id;
    @ApiModelProperty(value = "批注id", example = "1")
    private int commentid;
    @ApiModelProperty(value = "id", example = "1")
    private int type;
    @ApiModelProperty(value = "左起点", example = "1")
    private double x1;
    @ApiModelProperty(value = "右终点", example = "3")
    private double x2;
    @ApiModelProperty(value = "上坐标", example = "1")
    private double y1;
    @ApiModelProperty(value = "下坐标", example = "3")
    private double y2;
    @ApiModelProperty(value = "宽度", example = "2")
    private double width;
    @ApiModelProperty(value = "高度", example = "2")
    private double height;


}

