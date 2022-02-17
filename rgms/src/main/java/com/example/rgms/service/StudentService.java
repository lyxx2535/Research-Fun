package com.example.rgms.service;

import com.example.rgms.entity.user.StudentEntity;
import com.example.rgms.vo.user.StudentDetailedInfo;
import com.example.rgms.vo.user.StudentWithGroupInfo;
import com.example.rgms.vo.user.UserSimpleInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {
    boolean existsById(Long userId);

    void addOneStudent(StudentEntity studentEntity);

    List<UserSimpleInfo> getAllStudentsUnderTheTeacher(Long teacherId);

    String editInstructor(Long studentId, Long teacherId);

    StudentDetailedInfo getDetailedInfoById(Long studentId);

    Long getInstructorId(Long studentId);

    Page<StudentWithGroupInfo> getAllStudentWithGroupInfos(int currentPage, int pageSize);

    List<StudentWithGroupInfo> findStudentByUsernameAndTrueName(String key);

    void editDefaultResearchDirection(Long studentId, String newDirection);

    String getDefaultResearchDirection(Long studentId);
}
