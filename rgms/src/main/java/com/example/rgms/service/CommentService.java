package com.example.rgms.service;

import com.example.rgms.entity.comment.CommentEntity;
import com.example.rgms.vo.comment.*;
import com.example.rgms.vo.response.PageResponseVO;
import com.example.rgms.vo.response.ResponseVO;
import io.swagger.models.auth.In;

import java.awt.*;
import java.util.List;

public interface CommentService {

    public ResponseVO<List> getCommentBydid(int did);

    public ResponseVO<Integer>  createComment(commentinfo info);

    public ResponseVO<Integer> createRectangle(Rectangleinfo rectangleinfo);

    public PageResponseVO<replyinfo> getReplyByComment(int commentid,int currentPage,int pageSize);


    public  ResponseVO<List<Rectangleinfo>> getRectangleByCommentid(int commentid);



    /*
    * comment对象需要一次性返回位置信息
    *
    **/

    public ResponseVO<List<CommentVO>> getCommentinfoBydid(int did);


    public ResponseVO<Integer> createReply(replyinfo info);



    public ResponseVO<String> createCompleteComment(CommentVO commentVO);



    /*
    * 4.2更新
    *
    *
    * */

    public ResponseVO<String> initComment(CommentinfoVO commentinfoVO);



    public ResponseVO<List<CompletecommentVO>> searchCommentBydid(int did);


    public ResponseVO<String> deleteComment(int commentid );




}
