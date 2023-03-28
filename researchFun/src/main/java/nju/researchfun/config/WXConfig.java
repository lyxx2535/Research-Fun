package nju.researchfun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx")       //配置yml文件的前置名
public class WXConfig {

    private String appId;        //公众号标识

    private String appSecret;    //公众号密码

    private String server;        //服务器域名地址，用于微信服务器回调。

    private String qrCodeUrl;    //获取code接口

    private String tokenUrl;    //获取token接口

    private String openIdUrl;   //获取openid接口

    private String userInfoUrl;   //获取用户信息接口

    private String token;       //验证接口的标识
}
