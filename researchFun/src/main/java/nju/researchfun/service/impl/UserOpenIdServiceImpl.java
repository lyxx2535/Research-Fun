package nju.researchfun.service.impl;

import nju.researchfun.entity.user.UserOpenId;
import nju.researchfun.mapper.UserOpenIdMapper;
import nju.researchfun.service.UserOpenIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserOpenIdServiceImpl implements UserOpenIdService {
    @Autowired
    UserOpenIdMapper userOpenIdMapper;

    @Override
    public int insert(UserOpenId userOpenId) {
        return userOpenIdMapper.insert(userOpenId);
    }

    @Override
    public UserOpenId check(String openid) {
        return userOpenIdMapper.check(openid);
    }

    @Override
    public UserOpenId checkByUserId(Long id) {
        return userOpenIdMapper.checkByUserId(id);
    }

    @Override
    public int update(UserOpenId userOpenId) {
        return userOpenIdMapper.updateByOpenId(userOpenId);
    }
}
