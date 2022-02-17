package com.example.rgms.service;

import com.example.rgms.entity.ResearchGroupEntity;
import com.example.rgms.entity.user.UserEntity;
import com.example.rgms.vo.researchgroup.GroupInfoWithoutId;
import com.example.rgms.vo.response.ResponseVO;
import com.example.rgms.vo.user.*;

import java.util.List;

public interface UserService {
    LoginRet login(LoginForm loginForm);

    Boolean existsTheUsername(String username);

    void register(RegisterForm registerForm);

    UserIdAndUserType getUserIdAndUserTypeByUserName(String username);

    void editTrueName(Long userId, String trueName);

    void editEmail(Long userId, String email);

    void exitResearchGroup(Long userId);

    void approveToJoinGroup(Long messageId);

    UserEntity getUserEntityById(Long userId);

    ResearchGroupEntity createAndJoinResearchGroup(GroupInfoWithoutId form);

    UserSimpleInfo getUserSimpleInfoById(Long userId);

    List<UserSimpleInfo> getAllMemberSimpleInfosInTheGroup(Long groupId);

    boolean existsById(Long userId);

    void updateUserTrueNameAndEmailAndPortrait(UserTrueNameAndEmailAndPortrait info);

    void deleteResearchGroup(Long userId, Long groupId);

    void applyToJoinGroup(Long userId, Long groupId);

    void refuseToJoinGroup(Long groupCreator, Long applier);

    void applyToExitGroup(Long userId);

    void approveToExitGroup(Long messageId);

    void refuseToExitGroup(Long messageId);

    Long getGroupId(Long userId);
}
