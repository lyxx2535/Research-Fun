package nju.researchfun.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nju.researchfun.config.WXConfig;
import nju.researchfun.service.WXService;
import nju.researchfun.vo.response.ResponseVO;
import nju.researchfun.vo.user.LoginForm;
import nju.researchfun.vo.user.LoginRet;
import nju.researchfun.vo.user.WeChatLoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * web端微信登录
 */
@RestController
@RequestMapping("/wx")
@Api(tags = "微信登录接口")
public class WXController {

    @Autowired
    private WXService wxService;

    @PostMapping("/qrCode")
    @ApiOperation(
            value = "获取微信登录二维码链接接口",
            notes = "该方法会返回一个微信登录二维码的url，前端只需跳转到此url即可，不需要传递任何参数.\r\n" +
                    "若用户允许授权，则会重定向为 http://rgm.zhiyou.tech/login?code=CODE&state=STATE ," +
                    "若用户禁止授权，则不会发生重定向"
    )
    public ResponseVO<String> qrCode(HttpSession session) {//用这个sessionID做为状态码
        return new ResponseVO<>(wxService.getAuthorizationUrl(session.getId()));
    }

    @PostMapping("/webLogin")
    @ApiOperation(
            value = "用户授权之后微信登录",
            notes = "该方法会先判断state是否有效（用于防止恶意网络攻击），然后再登录验证。" +
                    "登录成功后就和正常的登录成功一样，返回token等信息"
    )
    @ApiParam(name = "WeChatLoginForm", required = true)
    public ResponseVO<LoginRet> webLogin(@RequestBody WeChatLoginForm form) {//用这个sessionID做为状态码
        return new ResponseVO<>(wxService.wxLoginWeb(form.getCode(), form.getState()));
    }


    @PostMapping("/bound")
    @ApiOperation(
            value = "用户进行微信绑定",
            notes = "该方法会先判断state是否有效（用于防止恶意网络攻击），然后进行绑定," +
                    "不报错即为成功"
    )
    @ApiImplicitParam(name = "uid", value = "用户id", defaultValue = "1", required = true)
    public ResponseVO<String> boundQrCode(HttpSession session, @RequestParam Long uid) {//用这个sessionID做为状态码
         return new ResponseVO<>(wxService.bound(session,uid));
    }

/*    @PostMapping("/bound")
    @ApiOperation(
            value = "用户进行微信绑定",
            notes = "该方法会先判断state是否有效（用于防止恶意网络攻击），然后进行绑定," +
                    "不报错即为成功"
    )
    @ApiParam(name = "WeChatLoginForm", required = true)
    public void bound(@RequestBody WeChatLoginForm form) {//用用户id作为state
        wxService.bound(form.getCode(), form.getState());
    }*/

}
