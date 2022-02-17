package com.example.rgms.entity.user;

import com.example.rgms.vo.user.UserIdAndUserType;
import com.example.rgms.vo.user.UserSimpleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // id
    private String username;    // 用户名
    private String password;    // 密码
    private String email;       // 用户邮箱
    private Long groupId;       // 用户所属项目组id
    private String trueName;    // 用户真实姓名
    private String portrait;    // 用户头像图片地址

    public UserSimpleInfo toUserSimpleInfo(){
        return UserSimpleInfo.builder()
                .userId(id)
                .username(username)
                .trueName(trueName)
                .portrait(portrait)
                .build();
    }
}
