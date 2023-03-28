package nju.researchfun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import nju.researchfun.entity.user.Teacher;
import nju.researchfun.entity.user.User;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.mapper.TeacherMapper;
import nju.researchfun.service.ResearchGroupService;
import nju.researchfun.service.StudentService;
import nju.researchfun.service.TeacherService;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.user.TeacherDetailedInfo;
import nju.researchfun.vo.user.UserSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ResearchGroupService researchGroupService;

    @Autowired
    private StudentService studentService;


    @Override
    public boolean existsById(Long userId) {//不为空 则存在

        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", userId);
        return teacherMapper.selectOne(wrapper) != null;
    }

    @Override
    public void addOneTeacher(Teacher teacher) {
        if (!existsById(teacher.getUid()))//找不到才能加老师
            teacherMapper.insert(teacher);
        else
            throw new BadRequestException("已经存在该老师账户！");
    }

    @Override
    public List<UserSimpleInfo> getAllTeacherSimpleInfos() {
        List<Teacher> teacherList = teacherMapper.selectList(null);
        List<UserSimpleInfo> res = new ArrayList<>(teacherList.size());
        for (Teacher teacher : teacherList)
            res.add(teacher.toTeacherSimpleInfo(userService));
        return res;
    }

    @Override
    public TeacherDetailedInfo getDetailedInfoById(Long teacherId) {
        if (!existsById(teacherId)) // 如果该用户不是老师
            throw new BadRequestException("该用户不是老师");
        User user = userService.getUserById(teacherId);
        TeacherDetailedInfo res = TeacherDetailedInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .trueName(user.getTrueName())
                .email(user.getEmail())
                .portrait(user.getPortrait())
                .build();

        // 设置研究组信息
        if (user.getGroupId() != null)
            res.setGroupInfo(researchGroupService.getResearchGroupSimpleInfo(user.getGroupId()));
        // 设置名下学生信息
        //todo 这里是导师名下的所有学生了 不局限于某个特定的研究组了
        res.setStudentInfos(studentService.getAllStudentsUnderTheTeacher(teacherId));

        return res;
    }

}
