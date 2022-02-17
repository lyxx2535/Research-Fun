package com.example.rgms.service.impl;

import com.example.rgms.entity.ResearchGroupEntity;
import com.example.rgms.exception.BadRequestException;
import com.example.rgms.exception.NotFoundException;
import com.example.rgms.repository.user.ResearchGroupRepository;
import com.example.rgms.service.ResearchGroupService;
import com.example.rgms.service.StudentService;
import com.example.rgms.service.TeacherService;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.researchgroup.GroupInfo;
import com.example.rgms.vo.researchgroup.ResearchGroupDetailedInfo;
import com.example.rgms.vo.researchgroup.ResearchGroupSimpleInfo;
import com.example.rgms.vo.user.UserSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ResearchGroupServiceImpl implements ResearchGroupService {
    @Value(value = "${research-group.direction-separator}")
    private String directionSep;

    private final ResearchGroupRepository researchGroupRepository;

    @Autowired @Lazy
    private UserService userService;
    @Autowired @Lazy
    private StudentService studentService;
    @Autowired @Lazy
    private TeacherService teacherService;


    @Autowired
    public ResearchGroupServiceImpl(ResearchGroupRepository researchGroupRepository){
        this.researchGroupRepository=researchGroupRepository;
    }

    @Override
    public ResearchGroupEntity addOneResearchGroup(ResearchGroupEntity researchGroupEntity) {
        if(existsByGroupName(researchGroupEntity.getGroupName()))
            throw new BadRequestException("已经存在名称为”"+researchGroupEntity.getGroupName()+"“的研究组了");
        if(researchGroupEntity.getId()!=null && researchGroupRepository.existsById(researchGroupEntity.getId()))
            throw new BadRequestException("已经存在id为"+researchGroupEntity.getId()+"的研究组了");
        return researchGroupRepository.save(researchGroupEntity);
    }

    @Override
    public boolean existsByGroupName(String groupName) {
        return researchGroupRepository.existsByGroupName(groupName);
    }

    @Override
    public ResearchGroupEntity getResearchGroupEntityById(Long groupId) {
        Optional<ResearchGroupEntity> maybeEntity=researchGroupRepository.findById(groupId);
        if(!maybeEntity.isPresent())
            throw new NotFoundException("不存在id为"+groupId+"的研究组");
        return maybeEntity.get();
    }

    @Override
    public ResearchGroupSimpleInfo getResearchGroupSimpleInfo(Long groupId) {
        return getResearchGroupEntityById(groupId).toResearchGroupSimpleInfo(directionSep);
    }

    @Override
    public List<ResearchGroupSimpleInfo> getAllResearchGroupSimpleInfos() {
        List<ResearchGroupEntity> groupEntities=researchGroupRepository.findAll();
        List<ResearchGroupSimpleInfo> res=new ArrayList<>(groupEntities.size());
        for(ResearchGroupEntity groupEntity : groupEntities)
            res.add(groupEntity.toResearchGroupSimpleInfo(directionSep));
        return res;
    }

    @Override
    public ResearchGroupDetailedInfo getDetailedInfoById(Long groupId) {
        ResearchGroupEntity groupEntity=getResearchGroupEntityById(groupId);
        ResearchGroupDetailedInfo res= ResearchGroupDetailedInfo.builder()
                .groupId(groupId)
                .groupName(groupEntity.getGroupName())
                .description(groupEntity.getDescription())
                .portrait(groupEntity.getPortrait())
                .directions(new ArrayList<>(Arrays.asList(groupEntity.getDirections().split(directionSep))))
                .creatorInfo(userService.getUserSimpleInfoById(groupEntity.getCreatorId()))
                .build();

        List<UserSimpleInfo> allMemberInfos=userService.getAllMemberSimpleInfosInTheGroup(groupId);
        List<UserSimpleInfo> studentInfos=new ArrayList<>(), teacherInfos=new ArrayList<>();
        for(UserSimpleInfo userSimpleInfo : allMemberInfos){
            Long userId=userSimpleInfo.getUserId();
            if(studentService.existsById(userId))
                studentInfos.add(userSimpleInfo);
            else if(teacherService.existsById(userId))
                teacherInfos.add(userSimpleInfo);
            else
                throw new BadRequestException("该用户（id为"+userId+"）既不是学生也不是老师！");
        }
        res.setStudentInfos(studentInfos);
        res.setTeacherInfos(teacherInfos);
        return res;
    }

    @Override
    public boolean existsById(Long groupId) {
        return researchGroupRepository.existsById(groupId);
    }

    @Override
    public void editGroupInfo(GroupInfo form) {
        if(form.getDirections().size()==0)
            throw new BadRequestException("研究方向的数量不能为0");
        ResearchGroupEntity groupEntity=this.getResearchGroupEntityById(form.getId());
        groupEntity.setGroupName(form.getGroupName());
        groupEntity.setDescription(form.getDescription());
        groupEntity.setPortrait(form.getPortrait());
        groupEntity.setDirections(form.gainDirectionStr(directionSep));
        researchGroupRepository.save(groupEntity);
    }

    @Override
    public Long getGroupCreator(Long groupId) {
        return getResearchGroupEntityById(groupId).getCreatorId();
    }

    @Override
    public void deleteGroup(Long groupId) {
        researchGroupRepository.deleteById(groupId);
    }

    @Override
    public List<String> getDirections(Long groupId) {
        ResearchGroupEntity entity=getResearchGroupEntityById(groupId);
        return new ArrayList<>(Arrays.asList(entity.getDirections().split(directionSep)));
    }
}
