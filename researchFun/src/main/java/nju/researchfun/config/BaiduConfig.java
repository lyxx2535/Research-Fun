package nju.researchfun.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "baidu")       //配置yml文件的前置名
public class BaiduConfig {

    private String appId;           //百度翻译appid
    private String appSecret;       //百度翻译秘钥
    private String TRANS_API_HOST;  //百度翻译服务器地址

    private String default_from;    //被翻译的语言
    private String default_to;      //翻译后的语言

    private String url;             //请求url
    private Picture picture;        //图片翻译

    private Voice voice;            //语音翻译

    @Data
    public static class Picture{
        private String cuid;
        private String mac;
        private Integer version;
        private String url;         //图片翻译url
        private String postUrl;     //post的url
    }

    @Data
    public static class Voice{
        private String url;                 //语音翻译url
        private String default_format;      //默认格式
    }
}
