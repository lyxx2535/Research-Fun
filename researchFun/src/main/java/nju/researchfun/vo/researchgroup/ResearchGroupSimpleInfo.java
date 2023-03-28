package nju.researchfun.vo.researchgroup;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResearchGroupSimpleInfo {
    private Long groupId;
    private String groupName;
    private String portrait;
    private List<String> directions;
}
