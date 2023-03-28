package nju.researchfun.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import nju.researchfun.constant.CommentType;
import nju.researchfun.constant.MessageType;
import nju.researchfun.entity.doc.Comment;
import nju.researchfun.entity.doc.Rectangle;
import nju.researchfun.entity.doc.Reply;
import nju.researchfun.entity.user.User;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.NotFoundException;
import nju.researchfun.mapper.CommentMapper;
import nju.researchfun.mapper.RectangleMapper;
import nju.researchfun.mapper.ReplyMapper;
import nju.researchfun.service.CommentService;
import nju.researchfun.service.FileService;
import nju.researchfun.service.MessageService;
import nju.researchfun.service.ResearchGroupService;
import nju.researchfun.service.SearchService;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.comment.*;
import nju.researchfun.vo.document.Documentinfo;
import nju.researchfun.vo.document.AtParams;
import nju.researchfun.vo.response.PageResponseVO;
import nju.researchfun.vo.response.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * 评论信息
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ReplyMapper replyMapper;
    @Autowired
    private RectangleMapper rectangleMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private ResearchGroupService researchGroupService;


    @Override
    public ResponseVO<List<Commentinfo>> getCommentBydid(Long did) {
        try {
            List<Comment> entities = commentMapper.selectList(new QueryWrapper<>(Comment.builder().did(did).build()));
            List<Commentinfo> result = new ArrayList<>(entities.size());

            for (Comment comment : entities) {
                Commentinfo info = comment.toCommentInfo(userService);
                result.add(info);
            }

            return new ResponseVO<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<List<CommentVO>> getCommentinfoBydid(Long did) {
        try {
            List<Comment> entities = commentMapper.selectList(new QueryWrapper<>(Comment.builder().did(did).build()));
            List<CommentVO> commentVOS = new ArrayList<>(entities.size());

            for (Comment comment : entities) {
                //初始化info对象
                Commentinfo info = comment.toCommentInfo(userService);

                //根据评论id获取批注位置信息
                List<Rectangle> rectangleEntities = rectangleMapper.selectList(new QueryWrapper<>(Rectangle.builder().cid(info.getId()).build()));
                List<Rectangleinfo> rectangleinfos = new ArrayList<>();
                Rectangleinfo boundingRect = null;
                for (Rectangle rectangle : rectangleEntities) {
                    Rectangleinfo rectangleinfo = Rectangle.toRectangleInfo(rectangle);

                    if (rectangle.getType() == 0) {
                        boundingRect = rectangleinfo;
                    } else {
                        rectangleinfos.add(rectangleinfo);
                    }
                }

                CommentVO commentVO = new CommentVO(info, boundingRect, rectangleinfos);
                commentVOS.add(commentVO);
            }

            return new ResponseVO<>(commentVOS);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }


    @Override
    @Transactional
    public ResponseVO<String> deleteComment(Long cid) {
        try {
            Comment comment = commentMapper.selectById(cid);
            if (comment.getCommentType() == CommentType.IMAGE) {
                String url = comment.getContent();
                fileService.deleteCommentImg(url);
            }

            replyMapper.delete(new QueryWrapper<>(Reply.builder().cid(cid).build()));
            rectangleMapper.delete(new QueryWrapper<>(Rectangle.builder().cid(cid).build()));
            commentMapper.deleteById(cid);

            return new ResponseVO<>("Delete Success");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * 根据日期排序
     */
    @Override
    public ResponseVO<List<CompleteCommentVO>> searchCommentBydid(Long did) {
        try {
            QueryWrapper<Comment> wrapper = new QueryWrapper<>();
            wrapper.eq("did", did).orderByAsc("date");
            List<Comment> entities = commentMapper.selectList(wrapper);

            List<CompleteCommentVO> result = new ArrayList<>();
            for (Comment c : entities) {
                CompleteCommentVO commentinfoVO = new CompleteCommentVO();

                //内容
                Content content = new Content();
                if (c.getCommentType() == CommentType.TEXT) {
                    content.setText(c.getContent());
                } else {
                    content.setImage(c.getContent());
                }
                commentinfoVO.setContent(content);
                //id和页码
                commentinfoVO.setId(c.getId());

                //位置
                Poisition poisition = new Poisition();
                poisition.setPageNumber(c.getPagenumber());

                List<Rectangle> rectangleEntities = rectangleMapper.selectList(new QueryWrapper<>(Rectangle.builder().cid(commentinfoVO.getId()).build()));

                List<RectangleVO> rects = new ArrayList<>();
                for (Rectangle a : rectangleEntities) {
                    if (a.getType() == 0) {
                        poisition.setBoundingRect(Rectangle.toRectVO(a));
                        rects.add(Rectangle.toRectVO(a));
                    } else {
                        RectangleVO rectangleVO = Rectangle.toRectVO(a);
                        rects.add(rectangleVO);
                    }
                }
                poisition.setRects(rects);
                commentinfoVO.setPosition(poisition);

                //批注信息
                nju.researchfun.vo.comment.Comment comment = new nju.researchfun.vo.comment.Comment();
                comment.setEmoji(c.getCommentEmoji());
                comment.setText(c.getCommentText());
                commentinfoVO.setComment(comment);

                //作者相关信息
                UserInfos userInfos = new UserInfos();
                userInfos.setDid(c.getDid());
                userInfos.setUserid(c.getUserid());

                SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     // 北京
                bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置北京时区
                userInfos.setDate(bjSdf.format(c.getDate()));

                Long userid = c.getUserid();
                User user = userService.getUserById(userid);
                String username = user.getUsername();
                String portait = user.getPortrait();
                userInfos.setPortrait(portait);
                userInfos.setUsername(username);

                commentinfoVO.setUserInfos(userInfos);

                result.add(commentinfoVO);
            }
            return new ResponseVO<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * 根据评论在原页面的位置进行排序后返回
     * 排序规则：先比较页数，再比较矩形框的左上角坐标值
     */
    @Override
    public ResponseVO<List<CompleteCommentVO>> searchCommentOrderedBydid(Long did) {
        List<CompleteCommentVO> result = getCompleteCommentVOByDid(did);
        sortByPos(result);
        return new ResponseVO<>(result);
    }

    /**
     * 根据文献 id查询并封装评论集 未排序
     */
    private List<CompleteCommentVO> getCompleteCommentVOByDid(Long did) {
        List<Comment> entities = commentMapper.selectList(new QueryWrapper<>(Comment.builder().did(did).build()));
        List<CompleteCommentVO> result = new ArrayList<>();
        for (Comment c : entities) {
            //内容
            Content content = new Content();
            if (c.getCommentType() == CommentType.TEXT) {
                content.setText(c.getContent());
            } else {
                content.setImage(c.getContent());
            }
            //位置
            Poisition poisition = new Poisition();
            poisition.setPageNumber(c.getPagenumber());
            List<Rectangle> rectangleEntities = rectangleMapper.selectList(new QueryWrapper<>(Rectangle.builder().cid(c.getId()).build()));

            List<RectangleVO> rects = new ArrayList<>();
            for (Rectangle a : rectangleEntities) {
                if (a.getType() == 0) {
                    poisition.setBoundingRect(Rectangle.toRectVO(a));
                    rects.add(Rectangle.toRectVO(a));
                } else {
                    RectangleVO rectangleVO = Rectangle.toRectVO(a);
                    rects.add(rectangleVO);
                }
            }
            poisition.setRects(rects);

            //批注信息
            nju.researchfun.vo.comment.Comment comment = new nju.researchfun.vo.comment.Comment();
            comment.setEmoji(c.getCommentEmoji());
            comment.setText(c.getCommentText());

            //作者相关信息
            UserInfos userInfos = new UserInfos();
            userInfos.setDid(c.getDid());
            userInfos.setUserid(c.getUserid());

            SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     // 北京
            bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));  // 设置北京时区
            userInfos.setDate(bjSdf.format(c.getDate()));

            Long userid = c.getUserid();
            User user = userService.getUserById(userid);
            String username = user.getUsername();
            String portrait = user.getPortrait();
            userInfos.setPortrait(portrait);
            userInfos.setUsername(username);

            CompleteCommentVO commentVO = CompleteCommentVO.builder()
                    .content(content)
                    .id(c.getId())
                    .position(poisition)
                    .comment(comment)
                    .userInfos(userInfos)
                    .build();
            result.add(commentVO);
        }
        return result;
    }

    void sortByPos(List<CompleteCommentVO> result) {
        result.sort(
                (r1, r2) -> r1.getPosition().getPageNumber() == r2.getPosition().getPageNumber() ?
                        (int) ((r1.getPosition().getBoundingRect().getY1() == r2.getPosition().getBoundingRect().getY1()) ?
                                r1.getPosition().getBoundingRect().getX1() - r2.getPosition().getBoundingRect().getX1() :
                                r1.getPosition().getBoundingRect().getY1() - r2.getPosition().getBoundingRect().getY1()) :
                        r1.getPosition().getPageNumber() - r2.getPosition().getPageNumber());
    }

    @Override
    public PageResponseVO<ReplyInfo> getReplyByComment(Long cid, int currentPage, int pageSize) {
        try {
            Page<Reply> page = new Page<>(currentPage, pageSize);
            IPage<Reply> iPage = replyMapper.selectPage(page, new QueryWrapper<>(Reply.builder().cid(cid).build()));

            List<ReplyInfo> list = new ArrayList<>();
            for (Reply reply : iPage.getRecords()) {
                list.add(Reply.toReplyInfo(reply, userService));
            }

            Page<ReplyInfo> newPage = new Page<>(currentPage, pageSize, list.size());
            newPage.setRecords(list);
            return new PageResponseVO<>(newPage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<List<Rectangleinfo>> getRectangleByCommentId(Long cid) {
        try {
            List<Rectangle> rectangleEntities = rectangleMapper.selectList(new QueryWrapper<>(Rectangle.builder().cid(cid).build()));
            List<Rectangleinfo> rectangleinfos = new ArrayList<>();
            for (Rectangle rectangle : rectangleEntities) {
                rectangleinfos.add(Rectangle.toRectangleInfo(rectangle));
            }
            return new ResponseVO<>(rectangleinfos);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<Long> createComment(Commentinfo info) {
        try {
            Comment comment = Comment.builder()
                    .did(info.getDid())
                    .commentType(info.getComment_type())
                    .content(info.getContent())
                    .commentText(info.getContent())
                    .commentEmoji(info.getComment_emoji())
                    .pagenumber(info.getPagenumber())
                    .userid(info.getUserid())
                    .date(new Date(System.currentTimeMillis()))
                    .build();

            commentMapper.insert(comment);
            comment = commentMapper.selectOne(new QueryWrapper<>(comment));

            return new ResponseVO<>(comment.getId(), "Save Success!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<Long> createRectangle(Rectangleinfo rectangleinfo) {
        try {
            Rectangle rectangle = Rectangle.builder()
                    .cid(rectangleinfo.getCommentid())
                    .type(rectangleinfo.getType())
                    .x1(rectangleinfo.getX1())
                    .x2(rectangleinfo.getX2())
                    .y1(rectangleinfo.getY1())
                    .y2(rectangleinfo.getY2())
                    .width(rectangleinfo.getWidth())
                    .height(rectangleinfo.getHeight())
                    .build();

            rectangleMapper.insert(rectangle);
            rectangle = rectangleMapper.selectOne(new QueryWrapper<>(rectangle));
            return new ResponseVO<>(rectangle.getId(), "Save Success!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<Long> createReply(ReplyInfo info) {
        try {
            Reply reply = Reply.builder()
                    .cid(info.getCommentid())
                    .type(info.getType())
                    .replyid(info.getReplyid())
                    .content(info.getContent())
                    .userid(info.getUserid())
                    .date(new Date())
                    .build();

            replyMapper.insert(reply);
            return new ResponseVO<>(reply.getId(), "Save Reply Success");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public ResponseVO<String> createCompleteComment(CommentVO commentVO) {
        try {
            Commentinfo info = commentVO.getInfo();
            this.createComment(info);
            Rectangleinfo boundingRect = commentVO.getBoundingRect();
            this.createRectangle(boundingRect);

            for (Rectangleinfo rectangleinfo : commentVO.getRects()) {
                this.createRectangle(rectangleinfo);
            }
            return new ResponseVO<>("Save Success!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }


    @Override
    public ResponseVO<String> initComment(CommentInfoVO commentinfoVO) {
        try {
            Comment comment = Comment.builder()
                    .pagenumber(commentinfoVO.getPosition().getPageNumber())
                    .date(new Date())
                    .userid(commentinfoVO.getInfos().getUserid())
                    .commentText(commentinfoVO.getComment().getText())
                    .commentEmoji(commentinfoVO.getComment().getEmoji())
                    .did(commentinfoVO.getInfos().getDid())
                    .build();
            if (commentinfoVO.getContent().getImage() == null) {
                //文字内容
                comment.setCommentType(CommentType.TEXT);
                comment.setContent(commentinfoVO.getContent().getText());
            } else {
                comment.setCommentType(CommentType.IMAGE);
                //todo 实现批注目标是图片
                String imgBase64Str = commentinfoVO.getContent().getImage();
                int idx = imgBase64Str.indexOf(',');
                if (idx != -1)
                    imgBase64Str = imgBase64Str.substring(idx + 1);
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] b = decoder.decodeBuffer(imgBase64Str);
                for (int i = 0; i < b.length; i++) {
                    if (b[i] < 0)
                        b[i] += 256;
                }
                comment.setContent(fileService.uploadCommentImg(b));
            }

            commentMapper.insert(comment);
            Long id = comment.getId();

            RectangleVO boundVO = commentinfoVO.getPosition().getBoundingRect();
            Rectangle boundRect = boundVO.toEntity();

            boundRect.setType(0);//框框
            boundRect.setCid(id);

            rectangleMapper.insert(boundRect);

            for (RectangleVO rectangleVO : commentinfoVO.getPosition().getRects()) {
                Rectangle entity = rectangleVO.toEntity();
                entity.setType(1);//文字框
                entity.setCid(id);
                rectangleMapper.insert(entity);
            }

            return new ResponseVO<>("Save Success");

        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * AT其他人的接口
     */
    @Override
    public void at(AtVO vo) {
        Documentinfo doc = searchService.searchCompletedoc(vo.getDid());
        String docURL = doc.getPdflink();
        Long gid = doc.getGroupid();
        String docName = doc.getTitle();
        String groupName = researchGroupService.getName(gid);
        String body = groupName + " 成员：" + vo.getSendName() + " 在文献：" + docName +
                " 中的评论：" + vo.getComment() + " 中提到了你，并留言：" + vo.getNotice();

        AtParams model = AtParams.builder().did(vo.getDid()).pdflink(docURL).
                id(vo.getId()).content(vo.getContent()).position(vo.getPosition()).build();
        String json = new Gson().toJson(model);

        messageService.sendMessage(vo.getSendId(), vo.getReceiverId(), body, MessageType.DOC_AT, null, json);
    }

}
