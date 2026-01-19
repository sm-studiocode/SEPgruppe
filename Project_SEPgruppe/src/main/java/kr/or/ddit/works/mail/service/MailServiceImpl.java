package kr.or.ddit.works.mail.service;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
	
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public String sendAuthMail(String toEmail) {

        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[SEP] 이메일 인증번호");
            helper.setText(
                "<h3>인증번호</h3><h1>" + code + "</h1>",
                true
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("메일 전송 실패", e);
        }

        return code;
    }
}

