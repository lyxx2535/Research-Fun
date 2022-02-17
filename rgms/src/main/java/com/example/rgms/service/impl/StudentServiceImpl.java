package com.example.rgms.service.impl;

import com.example.rgms.entity.user.StudentEntity;
import com.example.rgms.entity.user.UserEntity;
import com.example.rgms.exception.BadRequestException;
import com.example.rgms.exception.NotFoundException;
import com.example.rgms.exception.RgmsException;
import com.example.rgms.repository.user.StudentRepository;
import com.example.rgms.service.*;
import com.example.rgms.vo.user.StudentDetailedInfo;
import com.example.rgms.vo.user.StudentWithGroupInfo;
import com.example.rgms.vo.user.UserSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final MessageService messageService;
    private final ResearchGroupService researchGroupService;

    @Autowired @Lazy
    private UserService userService;
    @Autowired @Lazy
    private TeacherService teacherService;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, MessageService messageService,
                              ResearchGroupService researchGroupService){
        this.studentRepository=studentRepository;
        this.messageService=messageService;
        this.researchGroupService=researchGroupService;
    }

    @Override
    public boolean existsById(Long userId) {
        return studentRepository.existsById(userId);
    }

    @Override
    public void addOneStudent(StudentEntity studentEntity) {
        if(!studentRepository.existsById(studentEntity.getUserId()))
            studentRepository.save(studentEntity);
        else
            throw new BadRequestException("已经存在该学生账户！");
    }

    @Override
    public List<UserSimpleInfo> getAllStudentsUnderTheTeacher(Long teacherId) {
        List<StudentEntity> studentEntities=studentRepository.findByTeacherId(teacherId);
        List<UserSimpleInfo> res=new ArrayList<>(studentEntities.size());
        for(StudentEntity studentEntity : studentEntities){
            res.add(studentEntity.toUserSimpleInfo(userService));
        }
        return res;
    }

    @Override
    public String editInstructor(Long studentId, Long teacherId) {
        StudentEntity studentEntity=getStudentEntityById(studentId);
        if(studentEntity.getTeacherId().equals(teacherId))
            throw new BadRequestException("要更换的导师和当前的导师一样！");
        if(!teacherService.existsById(teacherId))
            throw new BadRequestException("不存在id为"+teacherId+"的老师");
        Long lastTeacherId=studentEntity.getTeacherId();
        studentEntity.setTeacherId(teacherId);

        messageService.sendMessage(studentId, lastTeacherId, "您的学生{sender}选择了另一名导师");
        messageService.sendMessage(studentId, teacherId, "{sender}选择您成为他的导师！");

        return "success";
    }

    @Override
    public StudentDetailedInfo getDetailedInfoById(Long studentId) {
        if(!existsById(studentId))
            throw new NotFoundException("不存在id为"+studentId+"的学生");
        UserEntity userEntity=userService.getUserEntityById(studentId);
        StudentDetailedInfo res=StudentDetailedInfo.builder()
                .userId(studentId)
                .username(userEntity.getUsername())
                .trueName(userEntity.getTrueName())
                .email(userEntity.getEmail())
                .portrait(userEntity.getPortrait())
                .build();
        // 设置研究组信息
        if(userEntity.getGroupId()!=null)
            res.setGroupInfo(researchGroupService.getResearchGroupSimpleInfo(userEntity.getGroupId()));
        // 设置指导老师信息
        StudentEntity studentEntity=getStudentEntityById(studentId);
        if(studentEntity.getTeacherId()!=null)
            res.setInstructorInfo(userService.getUserSimpleInfoById(studentEntity.getTeacherId()));
        return res;
    }

    @Override
    public Long getInstructorId(Long studentId) {
        StudentEntity entity=getStudentEntityById(studentId);
        return entity.getTeacherId();
    }

    private StudentEntity getStudentEntityById(Long studentId){
        Optional<StudentEntity> maybeEntity=studentRepository.findById(studentId);
        if(!maybeEntity.isPresent())
            throw new NotFoundException("不存在id为"+studentId+"的学生");
        return maybeEntity.get();
    }

    @Override
    public Page<StudentWithGroupInfo> getAllStudentWithGroupInfos(int currentPage, int pageSize) {
        Page<StudentEntity> entityPage=studentRepository.findAll(PageRequest.of(currentPage, pageSize));
        List<StudentWithGroupInfo> list=new ArrayList<>(entityPage.getNumberOfElements());
        for(StudentEntity entity : entityPage)
            list.add(new StudentWithGroupInfo(entity, userService, researchGroupService));
        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), entityPage.getTotalElements());
    }

    @Override
    public List<StudentWithGroupInfo> findStudentByUsernameAndTrueName(String key) {
        List<StudentEntity> entityList=studentRepository.findStudentByUsernameAndTrueName(key);
        List<StudentWithGroupInfo> res=new ArrayList<>(entityList.size());
        for(StudentEntity entity : entityList)
            res.add(new StudentWithGroupInfo(entity, userService, researchGroupService));
        return res;
    }

    @Override
    public void editDefaultResearchDirection(Long studentId, String newDirection) {
        UserEntity userEntity=userService.getUserEntityById(studentId);
        if(userEntity.getGroupId()==null)
            throw new BadRequestException("您尚未加入研究组，无法设置默认研究方向");
        StudentEntity studentEntity=getStudentEntityById(studentId);
        studentEntity.setDefaultResearchDirection(newDirection);
        studentRepository.save(studentEntity);
    }

    @Override
    public String getDefaultResearchDirection(Long studentId) {
        return getStudentEntityById(studentId).getDefaultResearchDirection();
    }

}
