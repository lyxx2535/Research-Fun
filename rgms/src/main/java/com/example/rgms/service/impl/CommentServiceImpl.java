package com.example.rgms.service.impl;

import com.example.rgms.entity.comment.CommentEntity;
import com.example.rgms.entity.comment.RectangleEntity;
import com.example.rgms.entity.comment.ReplyEntity;
import com.example.rgms.entity.user.UserEntity;
import com.example.rgms.exception.BadRequestException;
import com.example.rgms.exception.NotFoundException;
import com.example.rgms.repository.comment.CommentRepository;
import com.example.rgms.repository.comment.RectangleRepository;
import com.example.rgms.repository.comment.ReplyRepository;
import com.example.rgms.service.CommentService;
import com.example.rgms.service.FileService;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.comment.*;
import com.example.rgms.vo.response.PageResponseVO;
import com.example.rgms.vo.response.ResponseVO;
import io.swagger.models.auth.In;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;



/*
* 评论信息
*
*
* */
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final RectangleRepository rectangleRepository;
    private final UserService userService;
    private final FileService fileService;

    @Autowired
    public  CommentServiceImpl(CommentRepository commentRepository,
                               ReplyRepository replyRepository,
                               RectangleRepository rectangleRepository,
                               UserService userService, FileService fileService)

    {
        this.commentRepository=commentRepository;
        this.replyRepository=replyRepository;
        this.rectangleRepository=rectangleRepository;
        this.userService = userService;
        this.fileService=fileService;
    }





    @Override
    public ResponseVO<List> getCommentBydid(int did) {
        try{
            List<CommentEntity> entities = commentRepository.findByDid(did);
            List<commentinfo> result= new ArrayList<>(entities.size());


            for(CommentEntity entity:entities){
                commentinfo info = new commentinfo();
                info.setId(entity.getId());
                info.setDid(entity.getDid());

                info.setComment_type(entity.getComment_type());
                info.setContent(entity.getContent());
                info.setComment_text(entity.getComment_text());
                info.setComment_emoji(entity.getComment_emoji());

                info.setPagenumber(entity.getPagenumber());
                info.setDate(entity.getDate());
                info.setUserid(entity.getUserid());

                Long userid=new Long((long) entity.getUserid());
                UserEntity userEntity=userService.getUserEntityById(userid);
                String username=userEntity.getUsername();
                String portait=userEntity.getPortrait();
                info.setUsername(username);
                info.setPortrait(portait);

                result.add(info);

            }

            return new ResponseVO<>(result);


        }catch (Exception e){
            e.printStackTrace();
            throw  new NotFoundException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<Integer> createComment(commentinfo info) {
        try {
            CommentEntity commentEntity = new CommentEntity();
            commentEntity.setDid(info.getDid());
            commentEntity.setComment_type(info.getComment_type());
            commentEntity.setContent(info.getContent());
            commentEntity.setComment_text(info.getComment_text());
            commentEntity.setComment_emoji(info.getComment_emoji());
            commentEntity.setPagenumber(info.getPagenumber());
            commentEntity.setUserid(info.getUserid());
            Date date= new Date(System.currentTimeMillis());
            commentEntity.setDate(date);

            commentEntity = commentRepository.save(commentEntity);


            return new ResponseVO<>(commentEntity.getId(),"Save Success!");
        }catch (Exception e){
            e.printStackTrace();
            throw  new BadRequestException(e.getMessage());
        }

    }

    @Override
    public ResponseVO<Integer> createRectangle(Rectangleinfo rectangleinfo) {

        try {

            RectangleEntity rectangleEntity = new RectangleEntity();

            rectangleEntity.setCommentid(rectangleinfo.getCommentid());
            rectangleEntity.setType(rectangleinfo.getType());
            rectangleEntity.setX1(rectangleinfo.getX1());
            rectangleEntity.setX2(rectangleinfo.getX2());
            rectangleEntity.setY1(rectangleinfo.getY1());
            rectangleEntity.setY1(rectangleinfo.getY2());
            rectangleEntity.setWidth(rectangleinfo.getWidth());
            rectangleEntity.setHeight(rectangleinfo.getHeight());

            RectangleEntity entity=rectangleRepository.save(rectangleEntity);
            return new ResponseVO<>(entity.getId(),"Save Success!");
        }catch (Exception e){
            e.printStackTrace();
            throw  new BadRequestException(e.getMessage());
        }

    }

    @Override
    public PageResponseVO<replyinfo> getReplyByComment(int commentid, int currentPage, int pageSize) {
        try{
            Page<ReplyEntity>  replyEntities = replyRepository.findByCommentid(commentid, PageRequest.of(currentPage,pageSize));
            List<replyinfo> list= new ArrayList<>(replyEntities.getNumberOfElements());
            for(ReplyEntity entity:replyEntities){
                replyinfo reply=new replyinfo();

                reply.setId(entity.getId());
                reply.setCommentid(entity.getCommentid());

                reply.setContent(entity.getContent());
                reply.setDate(entity.getDate());

                reply.setType(entity.getType());
                reply.setReplyid(entity.getReplyid());

                reply.setUserid(entity.getUserid());
                Long userid=new Long((long) entity.getUserid());
                UserEntity userEntity=userService.getUserEntityById(userid);
                String username=userEntity.getUsername();
                String portait=userEntity.getPortrait();
                reply.setUsername(username);
                reply.setPortrait(portait);
                list.add(reply);
            }
            Page<replyinfo> pageresult=new PageImpl<>(list,PageRequest.of(currentPage,pageSize),replyEntities.getTotalElements());

            return new PageResponseVO<>(pageresult);

        }
        catch (Exception e){
            e.printStackTrace();
            throw  new NotFoundException(e.getMessage());
        }

    }

    @Override
    public ResponseVO<List<Rectangleinfo>> getRectangleByCommentid(int commentid) {
        try{
             List<RectangleEntity> rectangleEntities=rectangleRepository.findByCommentid(commentid);
             List<Rectangleinfo> rectangleinfos= new ArrayList<>();
             for(RectangleEntity rectangleEntity:rectangleEntities){
                 Rectangleinfo info= new Rectangleinfo();
                 info.setId(rectangleEntity.getId());
                 info.setCommentid(rectangleEntity.getCommentid());
                 info.setType(rectangleEntity.getType());
                 info.setX1(rectangleEntity.getX1());
                 info.setX2(rectangleEntity.getX2());

                 info.setY1(rectangleEntity.getY1());
                 info.setY2(rectangleEntity.getY2());

                 info.setWidth(rectangleEntity.getWidth());
                 info.setHeight(rectangleEntity.getHeight());

                 rectangleinfos.add(info);
             }
             return new ResponseVO<>(rectangleinfos);

        }catch (Exception e){
            e.printStackTrace();
            throw  new NotFoundException(e.getMessage());
        }

    }

    @Override
    public ResponseVO<List<CommentVO>> getCommentinfoBydid(int did) {
        try{
            List<CommentEntity> entities = commentRepository.findByDid(did);
            List<CommentVO> commentVOS=new ArrayList<>(entities.size());
            //List<commentinfo> result= new ArrayList<>(entities.size());


            for(CommentEntity entity:entities){
                //CommentVO comment= new CommentVO();
                //初始化info对象
                commentinfo info = new commentinfo();
                info.setId(entity.getId());
                info.setDid(entity.getDid());

                info.setComment_type(entity.getComment_type());
                info.setContent(entity.getContent());
                info.setComment_text(entity.getComment_text());
                info.setComment_emoji(entity.getComment_emoji());

                info.setPagenumber(entity.getPagenumber());
                info.setDate(entity.getDate());
                info.setUserid(entity.getUserid());

                Long userid=new Long((long) entity.getUserid());
                UserEntity userEntity=userService.getUserEntityById(userid);
                String username=userEntity.getUsername();
                String portait=userEntity.getPortrait();
                info.setUsername(username);
                info.setPortrait(portait);

                //获取批注位置信息
                List<RectangleEntity> rectangleEntities=rectangleRepository.findByCommentid(info.getId());
                List<Rectangleinfo> rectangleinfos= new ArrayList<>();
                Rectangleinfo boundingRect=null;
                for(RectangleEntity rectangleEntity:rectangleEntities){
                    Rectangleinfo rectangleinfo= new Rectangleinfo();
                    rectangleinfo .setId(rectangleEntity.getId());
                    rectangleinfo.setCommentid(rectangleEntity.getCommentid());
                    rectangleinfo.setType(rectangleEntity.getType());
                    rectangleinfo.setX1(rectangleEntity.getX1());
                    rectangleinfo.setX2(rectangleEntity.getX2());

                    rectangleinfo.setY1(rectangleEntity.getY1());
                    rectangleinfo.setY2(rectangleEntity.getY2());

                    rectangleinfo.setWidth(rectangleEntity.getWidth());
                    rectangleinfo.setHeight(rectangleEntity.getHeight());

                    if(rectangleEntity.getType()==0){
                        boundingRect=rectangleinfo;
                    }
                    else{
                        rectangleinfos.add(rectangleinfo);
                    }
                }

                CommentVO commentVO=new CommentVO(info,boundingRect,rectangleinfos);
                commentVOS.add(commentVO);

            }

            return new ResponseVO<>(commentVOS);




        }catch (Exception e){
            e.printStackTrace();
            throw  new NotFoundException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<Integer> createReply(replyinfo info) {
        try{
            ReplyEntity replyEntity = new ReplyEntity();

            replyEntity.setCommentid(info.getCommentid());
            replyEntity.setType(info.getType());
            replyEntity.setReplyid(info.getReplyid());
            replyEntity.setContent(info.getContent());
            replyEntity.setUserid(info.getUserid());

            Date date= new Date(System.currentTimeMillis());
            replyEntity.setDate(date);

            replyEntity= replyRepository.save(replyEntity);
            return new ResponseVO<>(replyEntity.getId(),"Save Reply Success");

        }catch (Exception e){
            e.printStackTrace();
            throw  new BadRequestException(e.getMessage());
        }

    }

    @Override
    public ResponseVO<String> createCompleteComment(CommentVO commentVO) {
        try{
            commentinfo info=commentVO.getInfo();
            this.createComment(info);
            Rectangleinfo boundingRect=commentVO.getBoundingRect();
            this.createRectangle(boundingRect);

            for(Rectangleinfo rectangleinfo:commentVO.getRects()){
                this.createRectangle(rectangleinfo);
            }
            return new  ResponseVO<>("Save Success!");

        }catch (Exception e){
            e.printStackTrace();
            throw  new BadRequestException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<String> initComment(CommentinfoVO commentinfoVO) {
        try{
            CommentEntity commentEntity = new CommentEntity();
            commentEntity.setPagenumber(commentinfoVO.getPosition().getPageNumber());
            if(commentinfoVO.getContent().getImage()==null){
                //文字内容
                commentEntity.setComment_type(0);
                commentEntity.setContent(commentinfoVO.getContent().getText());
            } else {
                // 批注目标是图片
                commentEntity.setComment_type(1);

                String imgBase64Str=commentinfoVO.getContent().getImage();
                int idx=imgBase64Str.indexOf(',');
                if(idx != -1)
                    imgBase64Str=imgBase64Str.substring(idx+1);
                  BASE64Decoder decoder=new BASE64Decoder();
                  byte[] b=decoder.decodeBuffer(imgBase64Str);
                    for (int i=0; i<b.length; i++){
                       if(b[i]<0)
                         b[i]+=256;
                    }

                    commentEntity.setContent(fileService.uploadCommentImg(b));
            }

            Date date= new Date();
            commentEntity.setDate(date);
            commentEntity.setUserid(commentinfoVO.getInfos().getUserid());
            commentEntity.setDid(commentinfoVO.getInfos().getDid());
            commentEntity.setComment_text(commentinfoVO.getComment().getText());
            commentEntity.setComment_emoji(commentinfoVO.getComment().getEmoji());

            commentEntity=commentRepository.save(commentEntity);

            int id=commentEntity.getId();

            RectangleVO boundVO=  commentinfoVO.getPosition().getBoundingRect();
            RectangleEntity boundRect= boundVO.toEntity();

            boundRect.setType(0);
            boundRect.setCommentid(id);

            rectangleRepository.save(boundRect);

            for(RectangleVO rectangleVO:commentinfoVO.getPosition().getRects()){
                RectangleEntity entity= rectangleVO.toEntity();
                entity.setType(1);
                entity.setCommentid(id);
                rectangleRepository.save(entity);
            }

            return new  ResponseVO<>("Save Success");

        }catch (Exception e){
            e.printStackTrace();
            throw  new BadRequestException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<List<CompletecommentVO>> searchCommentBydid(int did) {
        try{
            List<CommentEntity> entities = commentRepository.findByDidOrderByDate(did);
            List<CompletecommentVO> result= new ArrayList<>();



            for(CommentEntity entity:entities){
                CompletecommentVO commentinfoVO= new CompletecommentVO();

                //内容
                Content content= new Content();
                if(entity.getComment_type()==0){
                    content.setText(entity.getContent());
                }else{
                    content.setImage(entity.getContent());
                }
                commentinfoVO.setContent(content);
                //id和页码
                commentinfoVO.setId(entity.getId());

                //位置
                Poisition poisition=new Poisition();
                poisition.setPageNumber(entity.getPagenumber());
                List<RectangleEntity> rectangleEntities=rectangleRepository.findByCommentid(commentinfoVO.getId());
                List<RectangleVO> rects=new ArrayList<>();
                for(RectangleEntity a:rectangleEntities){
                    if(a.getType()==0){
                        poisition.setBoundingRect(a.toRectVO());
                    }else{
                        RectangleVO rectangleVO= a.toRectVO();
                        rects.add(rectangleVO);
                    }
                }
                poisition.setRects(rects);
                commentinfoVO.setPosition(poisition);

                //批注信息
                Comment comment= new Comment();
                comment.setEmoji(entity.getComment_emoji());
                comment.setText(entity.getComment_text());
                commentinfoVO.setComment(comment);

                //作者相关信息
                UserInfos userInfos= new UserInfos();
                userInfos.setDid(entity.getDid());
                userInfos.setUserid(entity.getUserid());

                SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     // 北京
                bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置北京时区
                userInfos.setDate(bjSdf.format(entity.getDate()));

                Long userid=new Long((long) entity.getUserid());
                UserEntity userEntity=userService.getUserEntityById(userid);
                String username=userEntity.getUsername();
                String portait=userEntity.getPortrait();
                userInfos.setPortrait(portait);
                userInfos.setUsername(username);

                commentinfoVO.setUserInfos(userInfos);

                result.add(commentinfoVO);


            }


            return new ResponseVO<>(result);


        }
        catch (Exception e){
            e.printStackTrace();
            throw  new BadRequestException(e.getMessage());
        }

    }

    @Override
    public ResponseVO<String> deleteComment(int commentid) {
     try {  CommentEntity commentEntity=commentRepository.findById(commentid);
        if(commentEntity.getComment_type()==1){
            String url =commentEntity.getContent();
            fileService.deleteCommentImg(url);
        }
        //Long id=new Long((long) commentEntity.getId());

        commentRepository.deleteById(commentid);
        rectangleRepository.deleteAllByCommentid(commentid);
        replyRepository.deleteAllByCommentid(commentid);

        return new  ResponseVO<String>("Delete Success");
        }catch (Exception e){
         e.printStackTrace();
         throw  new BadRequestException(e.getMessage());
     }
    }


}
