package com.example.rgms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RgmsException extends RuntimeException{
    protected HttpStatus httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;

    public RgmsException(String msg){
        super(msg);
    }
}
