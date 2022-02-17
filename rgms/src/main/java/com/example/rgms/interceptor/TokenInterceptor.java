package com.example.rgms.interceptor;

import com.example.rgms.exception.BadRequestException;
import com.example.rgms.exception.NotFoundException;
import com.example.rgms.utils.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler){
        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        if(!(request.getRequestURI().contains("swagger-")||request.getRequestURI().contains("api-docs")||request.getRequestURI().contains("favicon"))){
            // 如果不是swagger的请求，对其进行token的检测
            response.setCharacterEncoding("utf-8");
            String token = request.getHeader("token");
            if(token ==null)
                throw new NotFoundException(request.getRequestURL().toString()+": 请求中没有发现token");
            boolean approve = TokenUtil.verify(token);
            if(!approve)
                throw new BadRequestException(request.getRequestURL().toString()+": token未通过验证");
            System.out.println("token经过验证通过拦截器");
        }
        return true;
    }
}