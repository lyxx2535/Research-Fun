package com.example.rgms.exception;

import org.springframework.http.HttpStatus;

public class InternalServerError extends RgmsException{
    public InternalServerError(String msg) {
        super(msg);
        this.httpStatus= HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
