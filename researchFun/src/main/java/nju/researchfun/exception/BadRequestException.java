package nju.researchfun.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ResearchFunException {
    public BadRequestException(String msg) {
        super(msg);
        this.httpStatus= HttpStatus.BAD_REQUEST;
    }
}
