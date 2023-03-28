package nju.researchfun.exception;

import nju.researchfun.service.MailService;
import nju.researchfun.utils.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class DefaultExceptionHandler {

    @Autowired
    private MailService mailService;

    @Value("${maintainer.email}")
    private String email;


    /**
     * 主动throw的异常
     */
    @ExceptionHandler(Exception.class)
    public ErrorMessage handleUnProccessableServiceException(
            Exception e, HttpServletResponse response) throws Exception {

        if(e.getMessage().equals("Request method 'GET' not supported")){
            e.printStackTrace();
            throw e;
        }

        if (e instanceof ResearchFunException) {//主动抛出的异常
            ResearchFunException re = (ResearchFunException) e;
            response.setStatus(HttpStatus.OK.value());
            return new ErrorMessage(re.getHttpStatus().value() + "", re.getMessage());
        }
        //System.out.println(email);
        String subject;
        String text;
        if (e instanceof IOException) {//IO异常
            text = ExceptionUtil.getExceptionDetailInformation(e);
            subject = "研坊——学研小管家出现了IO异常";

        } else {//其他异常
            text = ExceptionUtil.getExceptionDetailInformation(e);
            subject = "研坊——学研小管家出现了未知异常";
        }

        mailService.sendSimpleMail(email, subject, text);
        e.printStackTrace();
        throw new Exception("服务器错误！");
    }
}