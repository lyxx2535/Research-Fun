package nju.researchfun.service.impl;

import nju.researchfun.exception.BadRequestException;
import nju.researchfun.service.CheckCodeService;
import nju.researchfun.session.MySessionContext;
import nju.researchfun.utils.VerifyCodeUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Service
public class CheckCodeServiceImpl implements CheckCodeService {

    @Override
    public void proCheckCode(String sessionId, HttpServletResponse response) throws IOException {
        //服务器通知浏览器不要缓存
        response.setHeader("pragma", "no-cache");
        response.setHeader("cache-control", "no-cache");
        response.setHeader("expires", "0");

        HttpSession session = getSession(sessionId);
        if (session == null)
            throw new BadRequestException("该会话不存在！请刷新网页再试！");

        String code = VerifyCodeUtil.generateVerifyCode(response.getOutputStream());
        System.out.println(code);
        session.setAttribute("CHECKCODE_SERVER", code);
    }

    /**
     * 判断验证码是否正确
     * 传入会话 session 和用户输入的验证码
     * 一切正常将不会报错
     */
    public void checkCode(String sessionId, String check) {

        HttpSession session = getSession(sessionId);
        if (session == null)
            throw new BadRequestException("该会话不存在！请刷新网页再试！");

        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");

        //System.out.println("验证码接口的验证码：" + checkcode_server);
        System.out.println("登录接口的sessionID：" + session.getId());
        System.out.println("登录接口传来的验证码：" + check);

        if (checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)) {//不成功
            throw new BadRequestException("验证码错误！请刷新验证码后再试！");
        }
    }


    private HttpSession getSession(String sessionId) {
        MySessionContext myc = MySessionContext.getInstance();
        return myc.getSession(sessionId);
    }
}
