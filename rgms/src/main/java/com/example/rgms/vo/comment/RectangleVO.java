package com.example.rgms.vo.comment;

import com.example.rgms.entity.comment.RectangleEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@ApiModel
@Getter
@Setter
public class RectangleVO {
    private double x1;
    private double x2;
    private double y1;
    private double y2;
    private double width;
    private double height;

    public RectangleEntity toEntity(){
        RectangleEntity rectangleEntity= new RectangleEntity();
        rectangleEntity.setX1(this.x1);
        rectangleEntity.setX2(this.x2);
        rectangleEntity.setY1(this.y1);
        rectangleEntity.setY2(this.y2);
        rectangleEntity.setHeight(this.height);
        rectangleEntity.setWidth(this.width);
        return rectangleEntity;
    }


}
