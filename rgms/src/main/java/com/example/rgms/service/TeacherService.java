package com.example.rgms.service;

import com.example.rgms.entity.user.TeacherEntity;
import com.example.rgms.vo.user.TeacherDetailedInfo;
import com.example.rgms.vo.user.UserSimpleInfo;

import java.util.List;

public interface TeacherService {
    boolean existsById(Long userId);

    void addOneTeacher(TeacherEntity teacherEntity);

    List<UserSimpleInfo> getAllTeacherSimpleInfos();

    TeacherDetailedInfo getDetailedInfoById(Long teacherId);
}
