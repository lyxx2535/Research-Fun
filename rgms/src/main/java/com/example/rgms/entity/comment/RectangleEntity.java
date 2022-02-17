package com.example.rgms.entity.comment;


import com.example.rgms.vo.comment.RectangleVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rectangle")


public class RectangleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int commentid;
    private int type;
    private double x1;
    private  double x2;
    private double y1;
    private double y2;
    private double width;
    private double height;



    public RectangleVO toRectVO(){
        RectangleVO rectangleVO=new RectangleVO();
        rectangleVO.setX1(this.x1);
        rectangleVO.setX2(x2);
        rectangleVO.setY1(y1);
        rectangleVO.setY2(y2);
        rectangleVO.setHeight(this.getHeight());
        rectangleVO.setWidth(this.getWidth());
        return  rectangleVO;

    }

}
