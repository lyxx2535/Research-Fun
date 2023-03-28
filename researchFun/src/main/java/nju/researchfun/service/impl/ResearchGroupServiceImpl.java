package nju.researchfun.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import nju.researchfun.entity.ResearchGroup;
import nju.researchfun.entity.user.User;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.NotFoundException;
import nju.researchfun.mapper.ResearchGroupMapper;
import nju.researchfun.service.ResearchGroupService;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.researchgroup.GroupInfo;
import nju.researchfun.vo.researchgroup.ResearchGroupDetailedInfo;
import nju.researchfun.vo.researchgroup.ResearchGroupSimpleInfo;
import nju.researchfun.vo.user.UserSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ResearchGroupServiceImpl implements ResearchGroupService {
    @Value(value = "${research-group.direction-separator}")
    private String directionSep;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private ResearchGroupMapper researchGroupMapper;


    @Override
    public Long getGroupCreator(Long groupId) {
        return getResearchGroupById(groupId).getCreatorId();
    }

    @Override
    public boolean existsByGroupName(String groupName) {
        QueryWrapper<ResearchGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("group_name", groupName);
        ResearchGroup group = researchGroupMapper.selectOne(wrapper);
        return group != null;//不为空即为存在
    }

    @Override
    public ResearchGroup getResearchGroupByGroupName(String groupName) {
        QueryWrapper<ResearchGroup> wrapper = new QueryWrapper<>();
        wrapper.eq("group_name", groupName);
        ResearchGroup group = researchGroupMapper.selectOne(wrapper);

        if (group == null)
            throw new NotFoundException("不存在该名称的研究组！");
        return group;
    }

    @Override
    public List<ResearchGroupSimpleInfo> getAllResearchGroupSimpleInfos() {
        List<ResearchGroup> groups = researchGroupMapper.selectList(null);
        List<ResearchGroupSimpleInfo> res = new ArrayList<>(groups.size());
        for (ResearchGroup group : groups)
            res.add(group.toResearchGroupSimpleInfo(directionSep));
        return res;
    }

    @Override
    public ResearchGroupDetailedInfo getDetailedInfoById(Long groupId) {
        ResearchGroup group = getResearchGroupById(groupId);
        ResearchGroupDetailedInfo res = ResearchGroupDetailedInfo.builder()
                .groupId(groupId)
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .portrait(group.getPortrait())
                .directions(new ArrayList<>(Arrays.asList(group.getDirections().split(directionSep))))
                .creatorInfo(userService.getUserSimpleInfoById(group.getCreatorId()))
                .build();

        //List<UserSimpleInfo> allMemberInfos = userService.getAllMemberSimpleInfosInTheGroup(groupId);
        List<User> users = userService.getAllMembersInTheGroup(groupId);//所有的用户
        List<UserSimpleInfo> studentInfos = new ArrayList<>(), teacherInfos = new ArrayList<>();
        for (User user : users) {//分类 老师还是学生
            if ("STUDENT".equals(user.getUserType())) {
                studentInfos.add(user.toUserSimpleInfo());
            } else if ("TEACHER".equals(user.getUserType())) {
                teacherInfos.add(user.toUserSimpleInfo());
            } else
                throw new BadRequestException("该用户（id为" + user.getId() + "）既不是学生也不是老师！");
        }
        res.setStudentInfos(studentInfos);
        res.setTeacherInfos(teacherInfos);
        return res;
    }

    @Override
    public ResearchGroup getResearchGroupById(Long groupId) {
        ResearchGroup group = researchGroupMapper.selectById(groupId);
        if (group == null)
            throw new NotFoundException("不存在id为" + groupId + "的研究组");
        return group;
    }

    @Override
    public void editGroupInfo(GroupInfo form) {
        if (form.getDirections().size() == 0)
            throw new BadRequestException("研究方向的数量不能为0");
        ResearchGroup group = this.getResearchGroupById(form.getId());//查到这个研究组
        group.setGroupName(form.getGroupName());
        group.setDescription(form.getDescription());
        group.setPortrait(form.getPortrait());
        group.setDirections(form.gainDirectionStr(directionSep));
        researchGroupMapper.updateById(group);
    }

    @Override
    public List<String> getDirections(Long groupId) {
        ResearchGroup group = getResearchGroupById(groupId);
        return new ArrayList<>(Arrays.asList(group.getDirections().split(directionSep)));
    }

    @Override
    public void deleteGroup(Long groupId) {
        researchGroupMapper.deleteById(groupId);
    }

    @Override
    public boolean selectById(Long groupId) {//不为空即为存在
        return researchGroupMapper.selectById(groupId) != null;
    }

    // todo 这里为什么要返回 ResearchGroup
    //  正常来说是不会有这个研究组id的
    @Override
    public ResearchGroup addOneResearchGroup(ResearchGroup researchGroup) {
        if (existsByGroupName(researchGroup.getGroupName()))
            throw new BadRequestException("已经存在名称为”" + researchGroup.getGroupName() + "“的研究组了");
        if (researchGroup.getId() != null && selectById(researchGroup.getId()))
            throw new BadRequestException("已经存在id为" + researchGroup.getId() + "的研究组了");
        researchGroupMapper.insert(researchGroup);
        return researchGroup;
    }

    @Override
    public ResearchGroupSimpleInfo getResearchGroupSimpleInfo(Long groupId) {
        return getResearchGroupById(groupId).toResearchGroupSimpleInfo(directionSep);
    }

    @Override
    public List<ResearchGroup> findGroupsByUid(long uid) {
        return researchGroupMapper.findByUid(uid);
    }

    @Override
    public String getName(Long groupId) {
        return getResearchGroupById(groupId).getGroupName();
    }

}
