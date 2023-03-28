package nju.researchfun.vo.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel
public class ScheduleFormForTeacher {
    @ApiModelProperty(value = "创建者id", example = "1")
    private Long creatorId;

    @ApiModelProperty(value = "日程时间", example = "2021-02-01")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    @ApiModelProperty(value = "提醒时间", example = "2021-02-01")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date ddl;

    @ApiModelProperty(value = "日程内容", example = "完成“添加学生”接口")
    private String content;

    @ApiModelProperty(value = "相关者id列表", example = "[2, 3, 4]")
    private List<Long> stakeHolderIds;

    public ScheduleForm toScheduleForm(){
        return ScheduleForm.builder()
                .date(date)
                .content(content)
                .build();
    }
}
