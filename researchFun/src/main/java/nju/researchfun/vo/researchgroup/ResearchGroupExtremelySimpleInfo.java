package nju.researchfun.vo.researchgroup;

import lombok.Builder;
import lombok.Data;
import nju.researchfun.entity.ResearchGroup;

import java.util.List;

@Data
@Builder
public class ResearchGroupExtremelySimpleInfo {
    private Long groupId;
    private String groupName;


    public static ResearchGroupExtremelySimpleInfo toExtremelySimpleInfo(ResearchGroup group) {
        return ResearchGroupExtremelySimpleInfo.builder().
                groupId(group.getId()).
                groupName(group.getGroupName()).
                build();
    }
}
