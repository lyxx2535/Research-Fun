package nju.researchfun.vo.schedule;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class ScheduleIdAndContentAndStakeHolders {
    @ApiModelProperty(value = "日程id", example = "32")
    private Long scheduleId;

    @ApiModelProperty(value = "日程内容", example = "完成“新建日程”接口")
    private String content;

    @ApiModelProperty(value = "相关者id列表", example = "[4, 5, 6]")
    private List<Long> stakeHolderIds;
}
