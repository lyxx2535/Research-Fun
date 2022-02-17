package com.example.rgms.vo.user;

import com.example.rgms.vo.researchgroup.ResearchGroupSimpleInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeacherDetailedInfo {
    private Long userId;

    private String username;
    private String trueName;
    private String email;
    private String portrait;
    private ResearchGroupSimpleInfo groupInfo;
    private List<UserSimpleInfo> studentInfos;
}
