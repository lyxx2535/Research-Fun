package nju.researchfun.vo.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class UserInfos extends Infos {

    @ApiModelProperty(value = "时间", example = "2021-02-19 02:11:45.546000")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private String date;
    @ApiModelProperty(value = "用户姓名", example = "LiMing")
    private String username;
    @ApiModelProperty(value = "用户头像url", example = "119.26.185")
    private String portrait;

}
