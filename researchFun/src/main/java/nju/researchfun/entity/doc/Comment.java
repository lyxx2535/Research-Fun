package nju.researchfun.entity.doc;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.constant.CommentType;
import nju.researchfun.entity.user.User;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.comment.Commentinfo;

import java.util.Date;

/**
 * 批注信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @TableId
    private Long id;
    private Long did;
    private CommentType commentType;
    private String content;
    private String commentText;
    private String commentEmoji;
    private Integer pagenumber;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date date;
    private Long userid;

    public Commentinfo toCommentInfo(UserService userService) {
        Commentinfo info = new Commentinfo();
        info.setId(this.getId());
        info.setDid(this.getDid());

        info.setComment_type(this.getCommentType());
        info.setContent(this.getContent());
        info.setComment_text(this.getCommentText());
        info.setComment_emoji(this.getCommentEmoji());

        info.setPagenumber(this.getPagenumber());
        info.setDate(this.getDate());
        info.setUserid(this.getUserid());

        Long userid = this.getUserid();
        User user = userService.getUserById(userid);
        info.setUsername(user.getUsername());
        info.setPortrait(user.getPortrait());
        return info;
    }

}
