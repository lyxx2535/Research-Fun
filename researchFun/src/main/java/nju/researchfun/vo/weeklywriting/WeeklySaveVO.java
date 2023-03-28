package nju.researchfun.vo.weeklywriting;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.entity.weekly.Weekly;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class WeeklySaveVO {

    @ApiModelProperty(value = "用户id", example = "1")
    private Long uid;
    @ApiModelProperty(value = "用户所在研究组id", example = "1")
    private Long gid;

    @ApiModelProperty(value = "开始时间", example = "2021-02-01")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    @ApiModelProperty(value = "结束时间", example = "2021-03-01")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;

    @ApiModelProperty(value = "阅读过的文献名", example = "[西瓜书, 摸鱼导论, 为什么今天什么都没做]")
    private List<String> docs;

    @ApiModelProperty(value = "已完成", example = "写完了周报数据结构")
    private String done;
    @ApiModelProperty(value = "待完成", example = "模式识别作业还没做")
    private String todo;

    public static WeeklySaveVO Weekly2WeeklySaveVO(Weekly vo) {
        return WeeklySaveVO.builder()
                .uid(vo.getUid())
                .gid(vo.getGid())
                .done(vo.getDone())
                .todo(vo.getTodo())
                .startDate(vo.getStartDate())
                .endDate(vo.getEndDate())
                .build();
    }

    public static WeeklySaveVO param2VO(Long uid, Long gid, String startDate, String endDate,
                                        String docs,
                                        String done, String todo) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date s = null;
        Date e = null;
        try {
            s = sdf.parse(startDate);
            e = sdf.parse(endDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        docs = docs.replaceAll("[\\[ \\]]", "");
        List<String> docsList = new ArrayList<>(Arrays.asList(docs.split(",")));

        return WeeklySaveVO.builder().uid(uid).gid(gid).startDate(s).endDate(e).docs(docsList).done(done).todo(todo).build();
    }
}
