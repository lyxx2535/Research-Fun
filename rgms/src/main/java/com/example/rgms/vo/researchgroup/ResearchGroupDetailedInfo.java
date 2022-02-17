package com.example.rgms.vo.researchgroup;

import com.example.rgms.vo.user.UserSimpleInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ResearchGroupDetailedInfo {
    private Long groupId;
    private String groupName;
    private String description;
    private String portrait;
    private List<String> directions;
    private UserSimpleInfo creatorInfo;
    private List<UserSimpleInfo> studentInfos;
    private List<UserSimpleInfo> teacherInfos;
}
