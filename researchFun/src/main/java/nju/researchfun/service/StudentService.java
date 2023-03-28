package nju.researchfun.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import nju.researchfun.entity.user.Student;
import nju.researchfun.vo.user.StudentDetailedInfo;
import nju.researchfun.vo.user.StudentWithGroupInfo;
import nju.researchfun.vo.user.UserSimpleInfo;

import java.util.List;

public interface StudentService {

    boolean existsById(Long uid);

    void addOneStudent(Student student);

    void editDefaultResearchDirection(Long studentId, String newDirection);

    String getDefaultResearchDirection(Long studentId);

  //  String editInstructor(Long studentId, Long teacherId);

    List<UserSimpleInfo> getAllStudentsUnderTheTeacher(Long teacherId);

    StudentDetailedInfo getDetailedInfoById(Long studentId);

    IPage<StudentWithGroupInfo> getAllStudentWithGroupInfos(int currentPage, int pageSize);

    List<StudentWithGroupInfo> findStudentByUsernameAndTrueName(String key);
}
