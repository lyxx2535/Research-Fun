package nju.researchfun.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface CheckCodeService {
    void proCheckCode(String sessionId, HttpServletResponse response) throws IOException;

    void checkCode(String sessionId, String check);
}