package nju.researchfun.service.impl;

import com.google.gson.Gson;
import nju.researchfun.config.WXConfig;
import nju.researchfun.constant.UserType;
import nju.researchfun.entity.user.User;
import nju.researchfun.entity.user.UserOpenId;
import nju.researchfun.entity.model.WeChatWebModel;
import nju.researchfun.exception.BadRequestException;
import nju.researchfun.exception.InternalServerError;
import nju.researchfun.exception.ResearchFunException;
import nju.researchfun.service.UserOpenIdService;
import nju.researchfun.service.UserService;
import nju.researchfun.service.WXService;
import nju.researchfun.session.MySessionContext;
import nju.researchfun.utils.TokenUtil;
import nju.researchfun.vo.user.LoginRet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class WXServiceImpl implements WXService {

    /**
     * wx ：读取AppID相关配置信息静态类
     */
    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private UserOpenIdService userOpenIdService;

    @Autowired
    private UserService userService;

    @Override
    public String getAuthorizationUrl(String state) {
        String url = wxConfig.getQrCodeUrl();
        String callbackUrl = wxConfig.getServer() + "/direct";
        String urlState = "";
        try {
            callbackUrl = URLEncoder.encode(callbackUrl, "utf-8");
            urlState = state;
        } catch (UnsupportedEncodingException e) {
            throw new InternalServerError("服务器错误！请稍后再试！");
        }
        // 权限
        String scope = "snsapi_login";
        url = String.format(url, wxConfig.getAppId(), callbackUrl, scope, urlState);
        return url;
    }

    @Override
    public LoginRet wxLoginWeb(String code, String state) {
        WeChatWebModel weChatWebModel = getAccessToken(code);

        Long uid = checkState(state);
        if (uid != null) {//应该是需要绑定的
            wxBound(weChatWebModel.getOpenid(), uid);
        }

        UserOpenId userOpenId = userOpenIdService.check(weChatWebModel.getOpenid());
        if (userOpenId == null)
            throw new BadRequestException("该用户尚未绑定！请使用账号密码登录");

        User user = userService.getUserById(userOpenId.getUserId());
        if (user == null)
            throw new BadRequestException("该微信账号绑定的用户已注销！");

        UserType userType;
        if ("STUDENT".equals(user.getUserType())) {
            userType = UserType.STUDENT;
        } else if ("TEACHER".equals(user.getUserType())) {
            userType = UserType.TEACHER;
        } else {
            throw new ResearchFunException("id为" + user.getId() + "的用户无法在用户类型中找到！");
        }
        return LoginRet.builder().
                userId(user.getId()).
                userType(userType).
                token(TokenUtil.sign(user.getUsername())).
                isBound(true).
                build();
    }

    @Override
    public String bound(HttpSession session, Long uid) {
        //TODO 没想好怎么写
        //绑定的时候给这个session加上uid
        session.setAttribute("uid", uid);

        String url = wxConfig.getQrCodeUrl();
        String callbackUrl = wxConfig.getServer() + "/direct";
        String urlState = session.getId();
        try {
            callbackUrl = URLEncoder.encode(callbackUrl, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 权限
        String scope = "snsapi_login";
        url = String.format(url, wxConfig.getAppId(), callbackUrl, scope, urlState);
        return url;
    }

    private void wxBound(String openId, Long uid) {
        UserOpenId userOpenId = userOpenIdService.checkByUserId(uid);
        if (userOpenId == null) {//绑定新用户
            userOpenIdService.insert(UserOpenId.builder().userId(uid).openId(openId).build());
        } else { //已绑定另外的账号，则绑定新账号
            userOpenIdService.update(UserOpenId.builder().userId(uid).openId(openId).build());
        }
    }


    /**
     * 根据 code 网络请求微信服务端 获取用户信息
     */
    private WeChatWebModel getAccessToken(String code) {
        WeChatWebModel weChatWebModel;

        String url = wxConfig.getTokenUrl();
        url = String.format(url, wxConfig.getAppId(), wxConfig.getAppSecret(), code);
        ResponseEntity<String> responseEntity = new RestTemplate().exchange(url, HttpMethod.GET, null, String.class);
        //if (responseEntity.getStatusCode() == HttpStatus.OK) {

        System.out.println("成功获取openId");
        String body = responseEntity.getBody();
        Gson gson = new Gson();
        weChatWebModel = gson.fromJson(body, WeChatWebModel.class);
        //}
        System.out.println("openId:" + weChatWebModel.getOpenid());

        if (weChatWebModel.getErrcode() != null) //无法获取信息
            throw new BadRequestException(weChatWebModel.getErrmsg());

        return weChatWebModel;
    }

    /**
     * 如果该session没有 uid 则返回 null
     * 返回uid
     * TODO 没想好怎么写
     */
    private Long checkState(String state) {
        MySessionContext myc = MySessionContext.getInstance();
        HttpSession session = myc.getSession(state);
        if (session == null)
            throw new BadRequestException("该状态码不存在！请重新扫码登录！");
        return (Long) session.getAttribute("uid");
    }


}
