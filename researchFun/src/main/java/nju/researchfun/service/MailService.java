package nju.researchfun.service;

import javax.mail.MessagingException;
import java.util.Map;

public interface MailService {
    /**
     * 邮件发送
     */
    boolean sendSimpleMail(String to, String subject, String text);

    void sendHtmlMail(String to, String subject, String text) throws MessagingException;
}