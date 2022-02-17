package com.example.rgms.entity.user;

import com.example.rgms.service.UserService;
import com.example.rgms.vo.user.UserSimpleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teacher")
public class TeacherEntity {
    @Id
    private Long userId;

    public UserSimpleInfo toTeacherSimpleInfo(UserService userService){
        UserSimpleInfo res=new UserSimpleInfo();
        res.setUserId(this.userId);
        UserEntity userEntity=userService.getUserEntityById(this.userId);
        res.setUsername(userEntity.getUsername());
        res.setTrueName(userEntity.getTrueName());
        return res;
    }
}
