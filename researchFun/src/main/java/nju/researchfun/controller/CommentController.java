package nju.researchfun.controller;


import io.swagger.annotations.*;
import nju.researchfun.service.CommentService;
import nju.researchfun.vo.comment.*;
import nju.researchfun.vo.response.PageResponseVO;
import nju.researchfun.vo.response.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@Api(tags = "批注相关接口")


public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/getcomment")
    @ApiOperation(value = "根据文件id搜索批注简单信息接口", notes = "返回结果")
    @ApiImplicitParam(name = "did", value = "文件的id", required = true,
            defaultValue = "1")
    public ResponseVO<List<Commentinfo>> getCommentBydid(@RequestParam int did) {
        return commentService.getCommentBydid((long) did);
    }

    @GetMapping("/getcommentinfo")
    @ApiOperation(value = "搜索批注详细信息接口", notes = "返回结果")
    @ApiImplicitParam(name = "did", value = "文件的id", required = true, defaultValue = "1")
    public ResponseVO<List<CommentVO>> getCommentinfoBydid(@RequestParam int did) {
        return commentService.getCommentinfoBydid((long) did);
    }


    @PostMapping("/getreply")
    @ApiOperation(value = "搜索评论接口", notes = "失败会返回失败原因")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentid", value = "批注id", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true)
    })
    public PageResponseVO<ReplyInfo> getReplyBycommentid(@RequestParam int commentid,
                                                         @RequestParam int currentPage,
                                                         @RequestParam int pageSize) {
        return commentService.getReplyByComment((long) commentid, currentPage, pageSize);
    }

    @GetMapping("/getrectangle")
    @ApiOperation(value = "搜索批注位置接口", notes = "返回结果")
    @ApiImplicitParam(name = "commentid", value = "批注的id", required = true, defaultValue = "1")
    public ResponseVO<List<Rectangleinfo>> getRectangleBycommentid(@RequestParam int commentid) {
        return commentService.getRectangleByCommentId((long) commentid);
    }

    @PutMapping("/savecommentinfo")
    @ApiOperation(value = "创建新的批注信息", notes = "会报错")
    @ApiParam(name = "info", value = "新的批注信息", required = true)
    public ResponseVO<Long> saveCommentinfo(@RequestBody Commentinfo info) {
        return commentService.createComment(info);
    }

    @PutMapping("/saverectangleinfo")
    @ApiOperation(value = "存储批注位置信息", notes = "会报错")
    @ApiParam(name = "rectangleinfo", value = "新的批注位置信息", required = true)
    public ResponseVO<Long> saveRectangleInfo(@RequestBody Rectangleinfo rectangleinfo) {
        return commentService.createRectangle(rectangleinfo);
    }

    @PutMapping("/savereplyinfo")
    @ApiOperation(value = "存储评论信息", notes = "会报错")
    @ApiParam(name = "info", value = "新的评论信息", required = true)
    public ResponseVO<Long> saveReplyinfo(@RequestBody ReplyInfo info) {
        return commentService.createReply(info);
    }

    @PutMapping("/savecommentvoinfo")
    @ApiOperation(value = "存储批注详细信息", notes = "会报错")
    @ApiParam(name = "commentVO", value = "批注详细信息", required = true)
    public ResponseVO<String> saveCompleteCommentVO(@RequestBody CommentVO commentVO) {
        return commentService.createCompleteComment(commentVO);
    }

    @GetMapping("/deletecomment")
    @ApiOperation(value = "删除批注接口", notes = "批注的id删除批注和位置信息")
    @ApiImplicitParam(name = "commentid", value = "批注的id", required = true,
            defaultValue = "1")
    public ResponseVO<String> deleteComment(@RequestParam int commentid) {
        return commentService.deleteComment((long) commentid);
    }

    @GetMapping("/getCompletecommentinfo")
    @ApiOperation(value = "搜索批注详细信息接口2.0", notes = "根据前端接口要求更改了数据结构")
    @ApiImplicitParam(name = "did", value = "文件的id", required = true, defaultValue = "1")
    public ResponseVO<List<CompleteCommentVO>> advanceSearchComment(@RequestParam int did) {
        return commentService.searchCommentOrderedBydid((long) did);
    }


    @PutMapping("/saveCompleteCommentinfo")
    @ApiOperation(value = "存储批注详细信息2.0", notes = "根据前端接口要求更改了数据结构，目前只能存储文字批注，不能存图片批注")
    @ApiParam(name = "commentinfoVO", value = "批注详细信息", required = true)
    public ResponseVO<String> advanceSaveComment(@RequestBody CommentInfoVO commentinfoVO) {
        return commentService.initComment(commentinfoVO);
    }

    @PostMapping("/at")
    @ApiOperation(value = "@其他人接口", notes = "成功了就返回success")
    @ApiParam(name = "AtVO", value = "at表单", required = true)
    public ResponseVO<String> getReplyBycommentid(@RequestBody AtVO vo) {
        commentService.at(vo);
        return new ResponseVO<>("success");
    }

}
