package com.example.rgms.vo.user;

import com.example.rgms.vo.researchgroup.ResearchGroupSimpleInfo;
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
