package nju.researchfun.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ResearchFunException {
    public NotFoundException(String msg) {
        super(msg);
        this.httpStatus= HttpStatus.NOT_FOUND;
    }
}
