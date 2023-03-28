package nju.researchfun.vo.user;

import nju.researchfun.entity.user.Student;
import nju.researchfun.entity.user.User;
import nju.researchfun.service.ResearchGroupService;
import nju.researchfun.service.UserService;
import nju.researchfun.vo.researchgroup.ResearchGroupSimpleInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentWithGroupInfo {
    private Long studentId;
    private String username;
    private String trueName;
    private ResearchGroupSimpleInfo groupInfo;

    public StudentWithGroupInfo(Student student, UserService userService, ResearchGroupService groupService) {
        this.studentId = student.getUid();
        User user = userService.getUserById(this.studentId);
        this.username = user.getUsername();
        this.trueName = user.getTrueName();
        if (user.getGroupId() != null)//todo 这里可能出问题 看看谁调用这个方法
            this.groupInfo = groupService.getResearchGroupSimpleInfo(user.getGroupId());
    }
}
