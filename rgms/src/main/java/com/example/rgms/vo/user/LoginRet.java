package com.example.rgms.vo.user;

import com.example.rgms.constant.UserType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRet {
    private Long userId;
    private UserType userType;
    private String token;
}
