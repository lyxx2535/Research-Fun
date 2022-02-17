package com.example.rgms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class DefaultExceptionHandler {
    /**
     * 主动throw的异常
     */
    @ExceptionHandler(RgmsException.class)
    public ErrorMessage handleUnProccessableServiceException(
            RgmsException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        return new ErrorMessage(e.getHttpStatus().value()+"", e.getMessage());
    }
}