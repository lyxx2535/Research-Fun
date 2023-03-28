package nju.researchfun.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import nju.researchfun.entity.user.User_Group;
import nju.researchfun.entity.user.Student;
import nju.researchfun.entity.user.User;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.NotFoundException;
import nju.researchfun.mapper.StudentMapper;
import nju.researchfun.mapper.User_GroupMapper;
import nju.researchfun.service.*;
import nju.researchfun.vo.user.StudentDetailedInfo;
import nju.researchfun.vo.user.StudentWithGroupInfo;
import nju.researchfun.vo.user.UserSimpleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private User_GroupMapper user_groupMapper;

    @Autowired
    private UserService userService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ResearchGroupService researchGroupService;


    @Override
    public boolean existsById(Long uid) {
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);

        return studentMapper.selectOne(wrapper) != null;
    }

    @Override
    public void addOneStudent(Student student) {
        studentMapper.insert(student);
    }

    // 这东西没用了
/*    @Override
    public String editInstructor(Long studentId, Long teacherId) {
        Student student = getStudentById(studentId);
        if (student.getTeacherId() != null && student.getTeacherId().equals(teacherId))
            throw new BadRequestException("要更换的导师和当前的导师一样！");
        if (!teacherService.existsById(teacherId))
            throw new BadRequestException("不存在id为" + teacherId + "的老师");
        Long lastTeacherId = student.getTeacherId();

        //更新数据库
        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("uid", studentId).set("teacher_id", teacherId);
        studentMapper.update(null, wrapper);

        messageService.sendMessage(studentId, lastTeacherId, "您的学生{sender}选择了另一名导师");
        messageService.sendMessage(studentId, teacherId, "{sender}选择您成为他的导师！");

        return "success";
    }*/


    @Override
    public void editDefaultResearchDirection(Long studentId, String newDirection) {
        User user = userService.getUserById(studentId);
        if (user.getGroupId() == null)
            throw new BadRequestException("您尚未加入研究组或无选定研究组，无法设置默认研究方向");


        UpdateWrapper<Student> wrapper = new UpdateWrapper<>();
        wrapper.eq("uid", studentId).set("default_research_direction", newDirection);

        studentMapper.update(null, wrapper);
    }

    @Override
    public String getDefaultResearchDirection(Long studentId) {
        return getStudentById(studentId).getDefaultResearchDirection();
    }

    /**
     * todo 待测试
     */
    @Override
    public IPage<StudentWithGroupInfo> getAllStudentWithGroupInfos(int currentPage, int pageSize) {

        Page<Student> page = new Page<>(currentPage, pageSize);
        IPage<Student> studentIPage = studentMapper.selectPage(page, null);

        List<StudentWithGroupInfo> list = new ArrayList<>();
        for (Student student : studentIPage.getRecords())
            list.add(new StudentWithGroupInfo(student, userService, researchGroupService));

        Page<StudentWithGroupInfo> res = new Page<>(currentPage, pageSize, studentIPage.getTotal());
        res.setRecords(list);
        return res;
    }

    /**
     * TODO 看看谁调用这个方法 要不要改
     */
    @Override
    public StudentDetailedInfo getDetailedInfoById(Long studentId) {
        if (!existsById(studentId))
            throw new NotFoundException("不存在id为" + studentId + "的学生");
        User user = userService.getUserById(studentId);
        StudentDetailedInfo res = StudentDetailedInfo.builder()
                .userId(studentId)
                .username(user.getUsername())
                .trueName(user.getTrueName())
                .email(user.getEmail())
                .portrait(user.getPortrait())
                .build();
        // TODO 设置当前研究组信息 看看这里要不要改
        if (user.getGroupId() != null)
            res.setGroupInfo(researchGroupService.getResearchGroupSimpleInfo(user.getGroupId()));
        // TODO 设置指导老师信息
        /*Student student = getStudentById(studentId);
        if (student.getTeacherId() != null)
            res.setInstructorInfo(userService.getUserSimpleInfoById(student.getTeacherId()));*/
        return res;
    }


    /**
     * TODO 这个有什么用？ 谁会调用这个方法？
     */
    @Override
    public List<UserSimpleInfo> getAllStudentsUnderTheTeacher(Long teacherId) {
        List<Student> students = findByTeacherId(teacherId);
        List<UserSimpleInfo> res = new ArrayList<>(students.size());
        for (Student student : students) {
            res.add(student.toUserSimpleInfo(userService));
        }
        return res;
    }

    @Override
    public List<StudentWithGroupInfo> findStudentByUsernameAndTrueName(String key) {
        List<Student> List = studentMapper.findStudentByUsernameAndTrueName(key);
        List<StudentWithGroupInfo> res = new ArrayList<>(List.size());
        for (Student student : List)
            res.add(new StudentWithGroupInfo(student, userService, researchGroupService));
        return res;
    }


    private Student getStudentById(Long studentId) {

        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", studentId);

        Student student = studentMapper.selectOne(wrapper);
        if (student == null)
            throw new NotFoundException("不存在id为" + studentId + "的学生");
        return student;
    }

    //找到这个老师的所有组，然后找出该组里的所有学生
    private List<Student> findByTeacherId(Long teacherId) {

        List<Student> res = new ArrayList<>();
        User_Group user_group = new User_Group();
        user_group.setUid(teacherId);
        List<User_Group> list = user_groupMapper.selectList(new QueryWrapper<>(user_group));
        for (User_Group user : list) {//每一个组
            for (User stu : userService.getAllStusInGroup(user.getGid())) {//组里的每一个学生
                res.add(getStudentById(stu.getId()));
            }
        }
        return res;
    }


}
