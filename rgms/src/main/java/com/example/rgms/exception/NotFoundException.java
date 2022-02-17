package com.example.rgms.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RgmsException{
    public NotFoundException(String msg) {
        super(msg);
        this.httpStatus= HttpStatus.NOT_FOUND;
    }
}
