package nju.researchfun.entity.doc;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.vo.comment.RectangleVO;
import nju.researchfun.vo.comment.Rectangleinfo;

/**
 * 批注所在位置信息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rectangle {
    @TableId
    private Long id;
    private Long cid;
    private Integer type;
    private Double x1;
    private Double x2;
    private Double y1;
    private Double y2;
    private Double width;
    private Double height;


    public static RectangleVO toRectVO(Rectangle r) {
        RectangleVO rectangleVO = new RectangleVO();
        rectangleVO.setX1(r.getX1());
        rectangleVO.setX2(r.getX2());
        rectangleVO.setY1(r.getY1());
        rectangleVO.setY2(r.getY2());
        rectangleVO.setHeight(r.getHeight());
        rectangleVO.setWidth(r.getWidth());
        return rectangleVO;
    }

    public static Rectangleinfo toRectangleInfo(Rectangle rectangle){
        Rectangleinfo rectangleinfo = new Rectangleinfo();
        rectangleinfo.setId(rectangle.getId());
        rectangleinfo.setCommentid(rectangle.getCid());
        rectangleinfo.setType(rectangle.getType());
        rectangleinfo.setX1(rectangle.getX1());
        rectangleinfo.setX2(rectangle.getX2());

        rectangleinfo.setY1(rectangle.getY1());
        rectangleinfo.setY2(rectangle.getY2());

        rectangleinfo.setWidth(rectangle.getWidth());
        rectangleinfo.setHeight(rectangle.getHeight());
        return rectangleinfo;
    }

}
