package kopo.fitmate.service.impl;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import kopo.fitmate.dto.MailDTO;
import kopo.fitmate.service.IMailService;
import kopo.fitmate.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public int doSendMail(MailDTO pDTO) {
        log.info("{}.doSendMail start!", this.getClass().getName());
        int res = 1;

        if (pDTO == null) {
            pDTO = new MailDTO();
        }

        String toMail = CmmUtil.nvl(pDTO.getToMail());
        String title = CmmUtil.nvl(pDTO.getTitle());
        String contents = CmmUtil.nvl(pDTO.getContents());

        log.info("toMail : {} / title : {} / contents : {}", toMail, title, contents);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, "UTF-8");
            messageHelper.setTo(toMail);
            messageHelper.setFrom(fromMail);
            messageHelper.setSubject(title);
            messageHelper.setText(contents);

            mailSender.send(message);
        } catch (Exception e) {
            res = 0;
            log.info("[ERROR] doSendMail : {}", e);
        }

        log.info("{}.doSendMail end!", this.getClass().getName());
        return res;
    }

    @Override
    public int sendAuthCode(String email, HttpSession session) throws Exception {
        log.info("sendAuthCode to email: {}", email);

        // 1. 인증번호 생성 (6자리 숫자)
        String authCode = String.format("%06d", new Random().nextInt(999999));
        log.info("Generated authCode: {}", authCode);

        // 2. 인증번호를 세션에 저장 (유효 시간 설정 가능)
        session.setAttribute("EMAIL_AUTH_CODE", authCode);

        // 3. 이메일 내용 구성
        MailDTO mailDTO = new MailDTO();
        mailDTO.setToMail(email);
        mailDTO.setTitle("FITMATE 이메일 인증번호입니다.");
        mailDTO.setContents("인증번호는 [" + authCode + "] 입니다. 입력 창에 입력해주세요.");

        return doSendMail(mailDTO); // 기존 메서드 재활용
    }
}
