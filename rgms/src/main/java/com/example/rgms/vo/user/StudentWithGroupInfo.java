package com.example.rgms.vo.user;

import com.example.rgms.entity.user.StudentEntity;
import com.example.rgms.entity.user.UserEntity;
import com.example.rgms.service.ResearchGroupService;
import com.example.rgms.service.UserService;
import com.example.rgms.vo.researchgroup.ResearchGroupSimpleInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentWithGroupInfo {
    private Long studentId;
    private String username;
    private String trueName;
    private ResearchGroupSimpleInfo groupInfo;

    public StudentWithGroupInfo(StudentEntity entity, UserService userService, ResearchGroupService groupService){
        this.studentId=entity.getUserId();
        UserEntity userEntity=userService.getUserEntityById(this.studentId);
        this.username=userEntity.getUsername();
        this.trueName=userEntity.getTrueName();
        if(userEntity.getGroupId()!=null)
            this.groupInfo=groupService.getResearchGroupSimpleInfo(userEntity.getGroupId());
    }
}
