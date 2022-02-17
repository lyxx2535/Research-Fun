package com.example.rgms.service;

import com.example.rgms.entity.ResearchGroupEntity;
import com.example.rgms.vo.researchgroup.GroupInfo;
import com.example.rgms.vo.researchgroup.ResearchGroupDetailedInfo;
import com.example.rgms.vo.researchgroup.ResearchGroupSimpleInfo;

import java.util.List;

public interface ResearchGroupService {
    ResearchGroupEntity addOneResearchGroup(ResearchGroupEntity researchGroupEntity);

    boolean existsByGroupName(String groupName);

    ResearchGroupEntity getResearchGroupEntityById(Long groupId);

    ResearchGroupSimpleInfo getResearchGroupSimpleInfo(Long groupId);

    List<ResearchGroupSimpleInfo> getAllResearchGroupSimpleInfos();

    ResearchGroupDetailedInfo getDetailedInfoById(Long groupId);

    boolean existsById(Long groupId);

    void editGroupInfo(GroupInfo form);

    Long getGroupCreator(Long groupId);

    void deleteGroup(Long groupId);

    List<String> getDirections(Long groupId);
}
