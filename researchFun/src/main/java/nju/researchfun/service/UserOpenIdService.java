package nju.researchfun.service;

import nju.researchfun.entity.user.UserOpenId;

public interface UserOpenIdService{
    int insert(UserOpenId userOpenId);
    UserOpenId check(String openid);
    UserOpenId checkByUserId(Long id);

    /**
     * 更新 用于微信重新绑定新的账号
     */
    int update(UserOpenId userOpenId);
}
