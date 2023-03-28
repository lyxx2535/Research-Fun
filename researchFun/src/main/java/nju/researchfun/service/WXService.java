package nju.researchfun.service;

import nju.researchfun.vo.user.LoginRet;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

public interface WXService {
    /**
     * 获取生成的二维码 url连接
     *
     * @param state 参数码 防止伪造站点攻击
     * @return 返回生成二维码 url
     */
    String getAuthorizationUrl(String state);


    /**
     * web端用户授权之后的登录
     */
    LoginRet wxLoginWeb(String code, String state);

    /**
     * web端用户微信绑定
     */
    String bound(HttpSession session, Long uid);
}
