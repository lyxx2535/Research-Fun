package nju.researchfun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nju.researchfun.service.CheckCodeService;
import nju.researchfun.session.MySessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

@RestController
@RequestMapping("/session")
@Api(tags = "session相关接口")
public class SessionController {

    @Autowired
    private CheckCodeService checkCodeService;
//
//    @PostMapping("/getSession")
//    @ApiOperation(
//            value = "获取session测试",
//            notes = "现在用于验证码测试"
//      )
//    public String getSession(@RequestParam String sessionId) {
//        MySessionContext myc = MySessionContext.getInstance();
//        HttpSession session = myc.getSession(sessionId);
//        return (String) session.getAttribute("CHECKCODE_SERVER");
//    }
//
//    @RequestMapping("/getCheckCode")
//    @ApiOperation(
//            value = "获取验证码测试",
//            notes = "现在用于验证码测试"
//    )
//    public void getCheckCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        checkCodeService.proCheckCode(request,response);
//    }

    @PostMapping("/getSession")
    @ApiOperation(
            value = "获取本次回话的sessionId",
            notes = "会返回一个sessionId字符串，在登录和注册的页面加载完成时调用"
    )
    public String getSession(HttpSession session) {
        return session.getId();
    }

    @GetMapping("/getAll")
    @ApiOperation(
            value = "获取所有会话的sessionId",
            notes = "仅用于调试"
    )
    public String getAll() throws NoSuchFieldException, IllegalAccessException {
        Field field = MySessionContext.class.getDeclaredField("mymap");
        field.setAccessible(true);
        StringBuilder sb = new StringBuilder();
        Map<String, HttpSession> map = (Map<String, HttpSession>) field.get(MySessionContext.getInstance());

        for (String s : map.keySet()) {
            String ss = s+" : "+ map.get(s).getId();
            System.out.println(ss);
            sb.append(ss).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
