package nju.researchfun.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

/*
    @Value("${file.download}")
    private String showPath;
    @Value("${file.base}")
    private String upPath;
*/

    //解决跨域问题
    //为什么加了这个就可以了
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(false)
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE", "HEAD")
                .allowedOrigins("*");
    }



   /* @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowedOrigins("http://192.168.67.1")
                .allowedOrigins("http://172.28.120.162")
                .allowedOrigins("http://172.17.233.179")
                .allowedOrigins("http://42.193.37.120")
                .allowedOrigins("http://rgm.zhiyou.tech");
    }*/

/*    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(showPath).addResourceLocations("file:" + upPath);
    }*/

}
