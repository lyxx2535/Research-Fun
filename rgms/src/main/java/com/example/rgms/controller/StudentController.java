package com.example.rgms.controller;

import com.example.rgms.service.StudentService;
import com.example.rgms.vo.response.PageResponseVO;
import com.example.rgms.vo.response.ResponseVO;
import com.example.rgms.vo.user.StudentDetailedInfo;
import com.example.rgms.vo.user.StudentWithGroupInfo;
import com.example.rgms.vo.user.UserSimpleInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

//import javax.xml.ws.Response;
import java.util.List;

@RestController
@RequestMapping("/student")
@Api(tags = "学生相关接口")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService){
        this.studentService=studentService;
    }

    @PostMapping("/editInstructor")
    @ApiOperation(value = "编辑导师接口", notes = "成功返回success，失败返回报错信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "studentId", value = "学生的用户id", defaultValue = "1", required = true),
            @ApiImplicitParam(name = "teacherId", value = "选择的老师的用户id", defaultValue = "2", required = true)
    })
    public ResponseVO<String> editInstructor(@RequestParam Long studentId, @RequestParam Long teacherId){
        return new ResponseVO<>(studentService.editInstructor(studentId, teacherId));
    }

    @GetMapping("/getAllUnderTheTeacher")
    @ApiOperation(value = "获取老师名下所有的学生", notes = "输入老师id，获取该老师名下所有学生")
    @ApiImplicitParam(name = "teacherId", value = "老师的用户id", defaultValue = "2", required = true)
    public ResponseVO<List<UserSimpleInfo>> getAllStudentsUnderTheTeacher(@RequestParam Long teacherId){
        return new ResponseVO<>(studentService.getAllStudentsUnderTheTeacher(teacherId));
    }

    @GetMapping("/detailedInfo")
    @ApiOperation(value = "获取学生详细信息接口")
    @ApiImplicitParam(name = "studentId", value = "学生的用户id", defaultValue = "1", required = true)
    public ResponseVO<StudentDetailedInfo> getDetailedInfoById(@RequestParam Long studentId){
        return new ResponseVO<>(studentService.getDetailedInfoById(studentId));
    }

    @GetMapping("/allStudentWithGroupInfos")
    @ApiOperation(value = "分页获取所有学生简单信息，包括学生所属研究组的信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页，从0开始", defaultValue = "0", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页面大小", defaultValue = "10", required = true)
    })
    public PageResponseVO<StudentWithGroupInfo> getAllStudentWithGroupInfos(@RequestParam int currentPage,
                                                                            @RequestParam int pageSize){
        return new PageResponseVO<>(studentService.getAllStudentWithGroupInfos(currentPage, pageSize));
    }

    @GetMapping("/studentWithGroupInfo")
    @ApiOperation(value = "搜索学生信息（包括所属研究组信息）", notes = "只列出用户名或者真名匹配关键字的学生")
    @ApiImplicitParam(name = "key", value = "输入的关键字", defaultValue = "1", required = true)
    public ResponseVO<List<StudentWithGroupInfo>> findStudentByUsernameAndTrueName(@RequestParam String key){
        return new ResponseVO<>(studentService.findStudentByUsernameAndTrueName(key));
    }

    @PutMapping("/defaultResearchDirection")
    @ApiOperation(value = "编辑学生默认研究方向", notes = "如果学生尚未加入研究组，则编辑失败")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "studentId", value = "学生id", defaultValue = "2", required = true),
            @ApiImplicitParam(name = "newDirection", value = "新的默认研究方向", defaultValue = "机器学习", required = true)
    })
    public ResponseVO<String> editDefaultResearchDirection(@RequestParam Long studentId, @RequestParam String newDirection){
        studentService.editDefaultResearchDirection(studentId, newDirection);
        return new ResponseVO<>("success");
    }

    @GetMapping("/defaultResearchDirection")
    @ApiOperation(value = "获取学生默认研究方向")
    @ApiImplicitParam(name = "studentId", value = "学生id", defaultValue = "2", required = true)
    public ResponseVO<String> getDefaultResearchDirection(Long studentId){
        return new ResponseVO<>(studentService.getDefaultResearchDirection(studentId));
    }
}
