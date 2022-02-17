package com.example.rgms.vo.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel
public class UserIdAndDate {
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;
    @ApiModelProperty(value = "日期", example = "2021-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
}
