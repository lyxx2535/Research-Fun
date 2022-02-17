package com.example.rgms.controller;

import com.example.rgms.service.MessageService;
import com.example.rgms.vo.MessageVO;
import com.example.rgms.vo.response.PageResponseVO;
import com.example.rgms.vo.response.ResponseVO;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@Api(tags = "消息相关接口")
public class MessageController {
    private final MessageService messageService;
    public MessageController(MessageService messageService){
        this.messageService=messageService;
    }

    @GetMapping
    @ApiOperation(value = "获取用户所有消息", notes = "消息的body是消息的主体，其中{sender}要用sender来替换，这样设计是方便前" +
            "端对发送者高亮。如果body中没有{sender}，那就不要替换，直接展示body")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true)
    })
    public PageResponseVO<MessageVO> getAllMessagesByUserId(@RequestParam Long userId,
                                                            @RequestParam int currentPage,
                                                            @RequestParam int pageSize){
        return new PageResponseVO<>(messageService.getAllMessagesByUserId(userId, currentPage, pageSize));
    }

    @PostMapping("/markRead")
    @ApiOperation(value = "将信息标记为已读", notes = "成功则返回success，失败会返回报错信息")
    @ApiImplicitParam(name = "messageId", value = "消息id", defaultValue = "1", required = true)
    public ResponseVO<String> markMessageRead(@RequestParam Long messageId){
        return new ResponseVO<>(messageService.markMessageRead(messageId));
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除消息")
    @ApiImplicitParam(name = "messageId", value = "消息id", defaultValue = "1", required = true)
    public ResponseVO<String> deleteMessage(@RequestParam Long messageId){
        return new ResponseVO<>(messageService.deleteMessage(messageId));
    }

//    @PutMapping("/markAllRead")
//    @ApiOperation("批量标记已读")
//    @ApiParam(name = "messagesIds", value = "消息id数组", required = true)
//    public void markMessageBatchRead(@RequestBody List<Long> messageIds){
//        messageService.markMessageBatchRead(messageIds);
//    }
//
//    @DeleteMapping("/all")
//    @ApiOperation("批量删除消息")
//    @ApiParam(name = "messagesIds", value = "消息id数组", required = true)
//    public void deleteMessageBatch(@RequestBody List<Long> messageIds){
//        messageService.deleteMessageBatch(messageIds);
//    }

    @GetMapping("/unreadMessageNum")
    @ApiOperation("获取该用户未读信息数量")
    @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "2", required = true)
    public ResponseVO<Integer> getUnreadMessageNum(Long userId){
        return new ResponseVO<>(messageService.getUnreadMessageNum(userId));
    }
}
