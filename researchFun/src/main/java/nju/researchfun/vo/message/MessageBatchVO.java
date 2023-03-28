package nju.researchfun.vo.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class MessageBatchVO {

    @ApiModelProperty(value = "批量操作类型", notes = "1表示批量已读 2表示批量删除", example = "1")
    private int key;

    @ApiModelProperty(value = "消息id列表", example = "[2, 3, 4]")
    List<Long> messageIds;
}
