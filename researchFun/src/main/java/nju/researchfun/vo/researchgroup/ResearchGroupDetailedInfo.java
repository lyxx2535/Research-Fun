package nju.researchfun.vo.researchgroup;

import nju.researchfun.vo.user.UserSimpleInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 研究组信息类
 * 研究组的基本信息+老师+学生的个人信息
 */
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
