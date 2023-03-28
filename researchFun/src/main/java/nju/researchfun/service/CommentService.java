package nju.researchfun.service;


import nju.researchfun.vo.comment.*;
import nju.researchfun.vo.response.PageResponseVO;
import nju.researchfun.vo.response.ResponseVO;

import java.util.List;

public interface CommentService {

    ResponseVO<List<Commentinfo>> getCommentBydid(Long did);

    PageResponseVO<ReplyInfo> getReplyByComment(Long commentid, int currentPage, int pageSize);

    ResponseVO<List<Rectangleinfo>> getRectangleByCommentId(Long commentid);

    ResponseVO<Long> createComment(Commentinfo info);

    ResponseVO<Long> createRectangle(Rectangleinfo rectangleinfo);

    ResponseVO<Long> createReply(ReplyInfo info);

    ResponseVO<String> createCompleteComment(CommentVO commentVO);

    /**
     * comment对象需要一次性返回位置信息
     */
    ResponseVO<List<CommentVO>> getCommentinfoBydid(Long did);

    ResponseVO<String> deleteComment(Long cid);

    ResponseVO<List<CompleteCommentVO>> searchCommentBydid(Long did);

    /**
     * 根据评论在原页面的位置进行排序后返回
     */
    ResponseVO<List<CompleteCommentVO>> searchCommentOrderedBydid(Long did);

    ResponseVO<String> initComment(CommentInfoVO commentinfoVO);

    void at(AtVO vo);
}
