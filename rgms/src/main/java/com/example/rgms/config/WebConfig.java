package com.example.rgms.config;

import com.example.rgms.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    private final TokenInterceptor tokenInterceptor;

    public WebConfig(TokenInterceptor tokenInterceptor){
        this.tokenInterceptor=tokenInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(false)
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE", "HEAD")
                .allowedOrigins("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        List<String> excludePath = new ArrayList<>();
        //排除拦截，除了注册登录(此时还没token)，其他都拦截
        excludePath.add("/user/register");  //注册
        excludePath.add("/user/login");     //登录
        excludePath.add("/swagger-ui.html"); // swagger
        excludePath.add("/");
        excludePath.add("/csrf");
        excludePath.add("/comment/**");
        excludePath.add("/file/download/**");

        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
        WebMvcConfigurer.super.addInterceptors(registry);
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowCredentials(false)
//                        .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
//                        .allowedOrigins("*");
//            }
//        };
//    }
}
