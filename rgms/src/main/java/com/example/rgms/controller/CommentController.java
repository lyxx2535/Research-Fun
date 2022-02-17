package com.example.rgms.controller;

import com.example.rgms.service.CommentService;
import com.example.rgms.vo.comment.*;
import com.example.rgms.vo.response.PageResponseVO;
import com.example.rgms.vo.response.ResponseVO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@Api(tags = "批注相关接口")


public class CommentController {
    private final CommentService commentService;
    @Autowired
    public  CommentController(CommentService commentService){
        this.commentService=commentService;
    }

    @GetMapping("/getcomment")
    @ApiOperation(value = "搜索批注简单信息接口",notes = "返回结果")
    @ApiImplicitParam(name = "did", value = "文件的id", required = true,
            defaultValue = "1")

    public ResponseVO<List> getCommentBydid(@RequestParam int did){
        return  commentService.getCommentBydid(did);
    }

    @GetMapping("/getcommentinfo")
    @ApiOperation(value = "搜索批注详细信息接口",notes = "返回结果")
    @ApiImplicitParam(name = "did", value = "文件的id", required = true,
            defaultValue = "1")

    public ResponseVO<List<CommentVO>> getCommentinfoBydid(@RequestParam int did){

        return  commentService.getCommentinfoBydid(did);
    }







    @PostMapping("/getreply")
    @ApiOperation(value = "搜索评论接口", notes = "失败会返回失败原因")
    @ApiImplicitParams({
           @ApiImplicitParam(name = "commentid", value = "批注id", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true)
    })
    public  PageResponseVO<replyinfo> getReplyBycommentid(@RequestParam int commentid,
                                                          @RequestParam int currentPage,
                                                          @RequestParam int pageSize)
    {
        return  commentService.getReplyByComment(commentid,currentPage,pageSize);
    }


    @GetMapping("/getrectangle")
    @ApiOperation(value = "搜索批注位置接口",notes = "返回结果")
    @ApiImplicitParam(name = "commentid", value = "批注的id", required = true,
            defaultValue = "1")
   public ResponseVO<List<Rectangleinfo>> getRectangleBycommentid(@RequestParam int commentid){
        return  commentService.getRectangleByCommentid(commentid);
    }

    @PutMapping("/savecommentinfo")
    @ApiOperation(value = "存储批注信息", notes = "会报错")

    @ApiParam(name = "info", value = "新的批注信息", required = true)


    public ResponseVO<Integer> saveCommentinfo(@RequestBody commentinfo info){
        return commentService.createComment(info);
    }
    @PutMapping("/saverectangleinfo")
    @ApiOperation(value = "存储批注位置信息", notes = "会报错")

    @ApiParam(name = "rectangleinfo", value = "新的批注位置信息", required = true)

    public  ResponseVO<Integer> saveRectangleInfo(@RequestBody Rectangleinfo rectangleinfo){
        return  commentService.createRectangle(rectangleinfo);
    }

    @PutMapping("/savereplyinfo")
    @ApiOperation(value = "存储评论信息", notes = "会报错")

    @ApiParam(name = "info", value = "新的评论信息", required = true)


    public  ResponseVO<Integer> saveReplyinfo(@RequestBody replyinfo info){
        return commentService.createReply(info);
    }



    @PutMapping("/savecommentvoinfo")
    @ApiOperation(value = "存储批注详细信息", notes = "会报错")

    @ApiParam(name = "commentVO", value = "批注详细信息", required = true)

   public  ResponseVO<String> saveCompleteCommentVO(@RequestBody CommentVO commentVO){
        return commentService.createCompleteComment(commentVO);
    }


    /*
    * 4.2更新
    *
    *
    * */
    @PutMapping("/saveCompleteCommentinfo")
    @ApiOperation(value = "存储批注详细信息2.0", notes = "根据前端接口要求更改了数据结构，目前只能存储文字批注，不能存图片批注")

    @ApiParam(name = "commentinfoVO", value = "批注详细信息", required = true)
    public ResponseVO<String> advanceSaveComment(@RequestBody CommentinfoVO commentinfoVO){
        return commentService.initComment(commentinfoVO);
    }

    @GetMapping("/getCompletecommentinfo")
    @ApiOperation(value = "搜索批注详细信息接口2.0",notes = "根据前端接口要求更改了数据结构")
    @ApiImplicitParam(name = "did", value = "文件的id", required = true,
            defaultValue = "1")

    public ResponseVO<List<CompletecommentVO>> advanceSearchComment(@RequestParam int did){
        return  commentService.searchCommentBydid(did);
    }

    @GetMapping("/deletecomment")
    @ApiOperation(value = "删除批注接口",notes = "批注的id删除批注和位置信息")
    @ApiImplicitParam(name = "commentid", value = "批注的id", required = true,
            defaultValue = "1")

    public ResponseVO<String> deleteCommen(@RequestParam int commentid){
        return commentService.deleteComment(commentid);
    }


}
