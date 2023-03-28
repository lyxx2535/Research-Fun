package nju.researchfun.service;

import javax.servlet.http.HttpSession;

public interface SessionService {
    /**
     * 根据sessionId session
     */
    HttpSession getSession(String sessionId);
}
