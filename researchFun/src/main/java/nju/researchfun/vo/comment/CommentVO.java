package nju.researchfun.vo.comment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 批注VO
 * 批注信息
 * 批注的边界信息
 * 其他位置信息
 */
@Data
@Builder
public class CommentVO {
    private Commentinfo info;
    private Rectangleinfo boundingRect;
    private List<Rectangleinfo> rects;

    public CommentVO(Commentinfo info, Rectangleinfo rectangleinfo, List<Rectangleinfo> rects) {
        this.info = info;
        this.boundingRect = rectangleinfo;
        this.rects = rects;
    }

}
