package com.example.rgms.service.impl;

import com.example.rgms.entity.user.TeacherEntity;
import com.example.rgms.entity.user.UserEntity;
import com.example.rgms.exception.BadRequestException;
import com.example.rgms.repository.user.TeacherRepository;
import com.example.rgms.service.ResearchGroupService;
import com.example.rgms.service.StudentService;
import com.example.rgms.service.TeacherService;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.user.TeacherDetailedInfo;
import com.example.rgms.vo.user.UserSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final ResearchGroupService researchGroupService;
    private final StudentService studentService;

    @Autowired
    @Lazy   // 懒加载，避免循环依赖
    private UserService userService;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository, ResearchGroupService researchGroupService,
                              StudentService studentService){
        this.teacherRepository=teacherRepository;
        this.researchGroupService=researchGroupService;
        this.studentService=studentService;
    }

    @Override
    public boolean existsById(Long userId) {
        return teacherRepository.existsById(userId);
    }

    @Override
    public void addOneTeacher(TeacherEntity teacherEntity) {
        if(!teacherRepository.existsById(teacherEntity.getUserId()))
            teacherRepository.save(teacherEntity);
        else
            throw new BadRequestException("已经存在该老师账户！");
    }

    @Override
    public List<UserSimpleInfo> getAllTeacherSimpleInfos() {
        List<TeacherEntity> teacherEntityList=teacherRepository.findAll();
        List<UserSimpleInfo> res=new ArrayList<>(teacherEntityList.size());
        for(TeacherEntity teacherEntity : teacherEntityList)
            res.add(teacherEntity.toTeacherSimpleInfo(userService));
        return res;
    }

    @Override
    public TeacherDetailedInfo getDetailedInfoById(Long teacherId) {
        if(!existsById(teacherId)) // 如果该用户不是老师
            throw new BadRequestException("该用户不是老师");
        UserEntity userEntity=userService.getUserEntityById(teacherId);
        TeacherDetailedInfo res=TeacherDetailedInfo.builder()
                .userId(userEntity.getId())
                .username(userEntity.getUsername())
                .trueName(userEntity.getTrueName())
                .email(userEntity.getEmail())
                .portrait(userEntity.getPortrait())
                .build();

        // 设置研究组信息
        if(userEntity.getGroupId()!=null)
            res.setGroupInfo(researchGroupService.getResearchGroupSimpleInfo(userEntity.getGroupId()));

        // 设置名下学生信息
        res.setStudentInfos(studentService.getAllStudentsUnderTheTeacher(teacherId));

        return res;
    }


}
