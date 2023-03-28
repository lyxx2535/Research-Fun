package nju.researchfun.service;

import nju.researchfun.entity.ResearchGroup;
import nju.researchfun.entity.user.User;
import nju.researchfun.vo.researchgroup.GroupInfoWithoutId;
import nju.researchfun.vo.researchgroup.ResearchGroupExtremelySimpleInfo;
import nju.researchfun.vo.response.ResponseVO;
import nju.researchfun.vo.user.*;

import java.util.List;

public interface UserService {
    LoginRet login(LoginForm loginForm);

    User getUserById(Long userId);

    void register(RegisterForm registerForm);

    Boolean existsTheUsername(String username);

    UserIdAndUserType getUserIdAndUserTypeByUserName(String username);

    void applyToJoinGroup(Long uid, Long groupId);

    void refuseToJoinGroup(Long messageId);

    void updateUserTrueNameAndEmailAndPortrait(UserTrueNameAndEmailAndPortrait info);

    UserSimpleInfo getUserSimpleInfoById(Long userId);

    List<UserSimpleInfo> getAllMemberSimpleInfosInTheGroup(Long groupId);

    List<User> getAllMembersInTheGroup(Long groupId);

    void deleteResearchGroup(Long uid, Long groupId);

    ResearchGroup createAndJoinResearchGroup(GroupInfoWithoutId form);

    boolean existsById(Long uid);

    Long getGroupId(Long uid);

    void applyToExitGroup(Long uid);

    void approveToJoinGroup(Long messageId);

    void approveToExitGroup(Long messageId);

    void refuseToExitGroup(Long messageId);

    boolean exitsTheEmail(String email);

    List<User> getAllTeasInGroup(Long groupid);

    List<User> getAllStusInGroup(Long groupid);

    boolean active(String code);

    void updatePwd(UserIdAndPwd info);

    void findPwd(findForm findForm);

    int activePwd(String code);

    int insert(User user);
    ResponseVO<LoginRet> loginWX(String code);

    void changeGroup(UserAndGroup info);

    List<ResearchGroupExtremelySimpleInfo> getUserAllGroups(Long uid);
}
