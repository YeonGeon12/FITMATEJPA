package kopo.fitmate.service;

import jakarta.servlet.http.HttpSession;
import kopo.fitmate.dto.MailDTO;

public interface IMailService {

    //메일 발송
    int doSendMail(MailDTO pDTO);

    // 인증번호 발송 및 세션 저장
    int sendAuthCode(String email, HttpSession session) throws Exception;

}