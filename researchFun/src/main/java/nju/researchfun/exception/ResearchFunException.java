package nju.researchfun.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResearchFunException extends RuntimeException {
    protected HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public ResearchFunException(String msg) {
        super(msg);
    }
}
