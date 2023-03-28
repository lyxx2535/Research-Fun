package nju.researchfun.entity.doc;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import nju.researchfun.constant.ReplyType;
import nju.researchfun.entity.user.User;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.comment.ReplyInfo;

import java.util.Date;

/**
 * 评论
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reply {

    @TableId
    private Long id;
    private Long cid;
    private ReplyType type;
    private Long replyid;
    private String content;
    private Long userid;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date date;

    public static ReplyInfo toReplyInfo(Reply reply, UserService userService) {
        ReplyInfo info = new ReplyInfo();

        info.setId(reply.getId());
        info.setCommentid(reply.getCid());

        info.setContent(reply.getContent());
        info.setDate(reply.getDate());

        info.setType(reply.getType());
        info.setReplyid(reply.getReplyid());

        info.setUserid(reply.getUserid());
        Long userid = reply.getUserid();
        User user = userService.getUserById(userid);
        String username = user.getUsername();
        String portait = user.getPortrait();
        info.setUsername(username);
        info.setPortrait(portait);
        return info;
    }
}
