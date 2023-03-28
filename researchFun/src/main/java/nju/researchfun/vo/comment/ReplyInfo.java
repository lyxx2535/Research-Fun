package nju.researchfun.vo.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nju.researchfun.constant.ReplyType;

import java.util.Date;

@Data
@ApiModel
@Getter
@Setter


public class ReplyInfo {
    @ApiModelProperty(value = "id", example = "1")
    private Long id;
    @ApiModelProperty(value = "评论对应的批注id", example = "2")
    private Long commentid;
    @ApiModelProperty(value = "评论类型 1单纯的评论2回复", example = "1")
    private ReplyType type;
    @ApiModelProperty(value = "回复的评论id", example = "1")
    private Long replyid;
    @ApiModelProperty(value = "评论内容", example = "这波不亏")
    private String content;

    @ApiModelProperty(value = "时间", example = "2021-02-19 02:11:45.546000")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date date;
    @ApiModelProperty(value = "用户id", example = "1")
    private  Long userid;
    @ApiModelProperty(value = "用户姓名", example = "芜湖男酮大司马")
    private String username;
    @ApiModelProperty(value = "用户头像url", example = "119.26.185")
    private String portrait;

}
