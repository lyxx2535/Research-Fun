package nju.researchfun.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.researchfun.vo.researchgroup.ResearchGroupSimpleInfo;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 研究组实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_researchgroup")
public class ResearchGroup {

    @TableId
    private Long id;
    private String groupName;
    private String description;
    private Long creatorId;
    private String portrait;
    private String directions; // 研究方向。不同的研究方向用"; "隔开  更改实现：拆表，一对多

    public ResearchGroupSimpleInfo toResearchGroupSimpleInfo(String directionSep){
        return ResearchGroupSimpleInfo.builder()
                .groupId(id)
                .groupName(groupName)
                .portrait(portrait)
                // 研究方向查表 别几把查了 懒得改了
                .directions(new ArrayList<>(Arrays.asList(directions.split(directionSep))))
                .build();
    }
}
