package nju.researchfun.entity.weekly;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.vo.weeklywriting.WeeklySaveVO;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Weekly {
    @TableId
    private Long id;
    private Long uid;
    private Long gid;
    private String did;//这里由于多对多表的意义不大，所以直接用; 分割了

    private Date startDate;
    private Date endDate;

    private String done;
    private String todo;

    public static Weekly WeeklySaveVO2Weekly(WeeklySaveVO vo){
        return Weekly.builder()
                .uid(vo.getUid())
                .gid(vo.getGid())
                .done(vo.getDone())
                .todo(vo.getTodo())
                .startDate(vo.getStartDate())
                .endDate(vo.getEndDate())
                .build();
    }
}
