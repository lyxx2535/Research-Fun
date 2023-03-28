package nju.researchfun.controller;

import io.swagger.annotations.*;
import nju.researchfun.service.MessageService;
import nju.researchfun.vo.message.MessageBatchVO;
import nju.researchfun.vo.message.MessageVO;
import nju.researchfun.vo.response.PageResponseVO;
import nju.researchfun.vo.response.ResponseVO;
import nju.researchfun.vo.schedule.ScheduleFormForTeacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@Api(tags = "消息相关接口")
public class MessageController {
    @Autowired
    private MessageService messageService;

/*

    @GetMapping
    @ApiOperation(value = "获取用户所有消息", notes = "消息的body是消息的主体，其中{sender}要用sender来替换，" +
            "这样设计是方便前端对发送者高亮。如果body中没有{sender}，那就不要替换，直接展示body")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true)
    })
    public PageResponseVO<MessageVO> getAllMessagesByUserId(@RequestParam Long userId,
                                                            @RequestParam int currentPage,
                                                            @RequestParam int pageSize) {
        return new PageResponseVO<>(messageService.getAllMessagesByUserId(userId, currentPage, pageSize));
    }
*/

    @GetMapping
    @ApiOperation(value = "获取用户所有消息改进版", notes = "消息的body是消息的主体，其中{sender}要用sender来替换，" +
            "这样设计是方便前端对发送者高亮。如果body中没有{sender}，那就不要替换，直接展示body。\r\n" +
            "可以根据研究组/日程分类来查询信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页消息个数", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "key", value = "查询类别,0表示查询全部消息，1表示查询研究组消息，2表示查询日程消息，3表示查询文献相关信息", defaultValue = "1", required = true)
    })
    public PageResponseVO<MessageVO> getAllMessagesByUserIdAndKey(@RequestParam Long userId,
                                                            @RequestParam int currentPage,
                                                            @RequestParam int pageSize,
                                                            @RequestParam int key) {
        return new PageResponseVO<>(messageService.getAllMessagesByUserIdAndKey(userId, currentPage, pageSize, key));
    }

    @PostMapping("/markRead")
    @ApiOperation(value = "将信息标记为已读", notes = "成功则返回success，失败会返回报错信息")
    @ApiImplicitParam(name = "messageId", value = "消息id", defaultValue = "1", required = true)
    public ResponseVO<String> markMessageRead(@RequestParam Long messageId) {
        return new ResponseVO<>(messageService.markMessageRead(messageId));
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除消息")
    @ApiImplicitParam(name = "messageId", value = "消息id", defaultValue = "1", required = true)
    public ResponseVO<String> deleteMessage(@RequestParam Long messageId) {
        return new ResponseVO<>(messageService.deleteMessage(messageId));
    }

    @GetMapping("/unreadMessageNum")
    @ApiOperation("获取该用户未读信息数量")
    @ApiImplicitParam(name = "userId", value = "用户id", defaultValue = "2", required = true)
    public ResponseVO<Integer> getUnreadMessageNum(Long userId) {
        return new ResponseVO<>(messageService.getUnreadMessageNum(userId));
    }

    @PostMapping("/messageBatch")
    @ApiOperation(value = "批量操作消息接口",
            notes = "参数key的值：1 表示批量已读, 2 表示批量删除")
    @ApiParam(name = "form", required = true)
    public void messageBatch(@RequestBody MessageBatchVO form) {
        messageService.messageBatch(form.getMessageIds(),form.getKey());
    }
}
