package kr.or.ddit.works.mail.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
	
    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.username}")
    private String senderEmail;
    
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

    @Override
    public void sendTempPasswordMail(String email, String tempPw) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[SEP] 임시 비밀번호 발급");

            String body = "";
            body += "<h3>임시 비밀번호가 발급되었습니다.</h3>";
            body += "<p>아래 임시 비밀번호로 로그인 후, 마이페이지에서 비밀번호를 변경하세요.</p>";
            body += "<h2 style='letter-spacing:1px;'>" + tempPw + "</h2>";
            body += "<p style='color:#888;'>보안을 위해 로그인 후 즉시 변경을 권장합니다.</p>";

            message.setText(body, "UTF-8", "html");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("임시 비밀번호 메일 발송 실패", e);
        }
    }

}

