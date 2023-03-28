package nju.researchfun.service;


import nju.researchfun.entity.user.Teacher;
import nju.researchfun.vo.user.TeacherDetailedInfo;
import nju.researchfun.vo.user.UserSimpleInfo;

import java.util.List;

public interface TeacherService {
    boolean existsById(Long uid);

    void addOneTeacher(Teacher teacher);

    List<UserSimpleInfo> getAllTeacherSimpleInfos();

    TeacherDetailedInfo getDetailedInfoById(Long teacherId);


}
