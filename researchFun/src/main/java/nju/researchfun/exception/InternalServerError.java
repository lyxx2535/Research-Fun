package nju.researchfun.exception;

import org.springframework.http.HttpStatus;

public class InternalServerError extends ResearchFunException {
    public InternalServerError(String msg) {
        super(msg);
        this.httpStatus= HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
