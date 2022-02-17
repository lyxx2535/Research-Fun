package com.example.rgms.vo.comment;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder


public class CommentVO {
    private commentinfo info;
    private Rectangleinfo boundingRect;
    private List<Rectangleinfo>  rects;

    public CommentVO(commentinfo info,Rectangleinfo rectangleinfo,List<Rectangleinfo> rects)
    {
        this.info=info;
        this.boundingRect=rectangleinfo;
        this.rects=rects;
    }

}
