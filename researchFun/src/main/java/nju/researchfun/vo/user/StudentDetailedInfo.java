package nju.researchfun.vo.user;

import nju.researchfun.vo.researchgroup.ResearchGroupSimpleInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDetailedInfo {
    private Long userId;
    private String username;
    private String trueName;
    private String email;
    private String portrait;
    private UserSimpleInfo instructorInfo;
    private ResearchGroupSimpleInfo groupInfo;
}
