package com.example.rgms.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RgmsException{
    public BadRequestException(String msg) {
        super(msg);
        this.httpStatus= HttpStatus.BAD_REQUEST;
    }
}
