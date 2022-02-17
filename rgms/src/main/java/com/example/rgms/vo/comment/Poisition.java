package com.example.rgms.vo.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@ApiModel
@Getter
@Setter
public class Poisition {
    @ApiModelProperty(value = "boundinfRect", example = "最大坐标")
    private  RectangleVO boundingRect;
    @ApiModelProperty(value = "rects", example = "[]")
    private List<RectangleVO> rects;
    @ApiModelProperty(value = "pageNumber", example = "1")
    private int pageNumber;
}

