package com.example.rgms.vo.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Data
@ApiModel
@Getter
@Setter


public class commentinfo {
    @ApiModelProperty(value = "id", example = "1")
    private  int id;
    @ApiModelProperty(value = "文献id", example = "1")
    private int did;
    @ApiModelProperty(value = "批注内容的类型", example = "1图片2文字")
    private int comment_type;
    @ApiModelProperty(value = "批注文献信息", example = "seep sadakfkagf")
    private String content;
    @ApiModelProperty(value = "批注内容", example = "这波啊，这波是肉蛋葱鸡")
    private String comment_text;
    @ApiModelProperty(value = "批注表情", example = " veryGood/ 写的什么垃圾")
    private  String comment_emoji;
    @ApiModelProperty(value = "批注页码", example = "1")
    private int pagenumber;
    @ApiModelProperty(value = "时间", example = "2021-02-19 02:11:45.546000")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date date;
    @ApiModelProperty(value = "用户id", example = "1")
    private  int userid;
    @ApiModelProperty(value = "用户姓名", example = "LiMing")
    private String username;
    @ApiModelProperty(value = "用户头像url", example = "119.26.185")
    private String portrait;
}
