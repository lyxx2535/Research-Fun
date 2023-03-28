package nju.researchfun.service;


import nju.researchfun.entity.ResearchGroup;
import nju.researchfun.vo.researchgroup.GroupInfo;
import nju.researchfun.vo.researchgroup.ResearchGroupDetailedInfo;
import nju.researchfun.vo.researchgroup.ResearchGroupSimpleInfo;

import java.util.List;

public interface ResearchGroupService {

    Long getGroupCreator(Long groupId);

    boolean existsByGroupName(String groupName);

    ResearchGroup getResearchGroupByGroupName(String groupName);

    List<ResearchGroupSimpleInfo> getAllResearchGroupSimpleInfos();

    ResearchGroupDetailedInfo getDetailedInfoById(Long groupId);

    ResearchGroup getResearchGroupById(Long groupId);

    void editGroupInfo(GroupInfo form);

    List<String> getDirections(Long groupId);

    void deleteGroup(Long groupId);

    ResearchGroup addOneResearchGroup(ResearchGroup researchGroup);

    boolean selectById(Long groupId);

    ResearchGroupSimpleInfo getResearchGroupSimpleInfo(Long groupId);

    /**
     * 根据用户id查找所处的所有研究组
     */
    List<ResearchGroup> findGroupsByUid(long uid);

    /**
     * 根据研究组id获取组名
     */
    String getName(Long groupId);
}
