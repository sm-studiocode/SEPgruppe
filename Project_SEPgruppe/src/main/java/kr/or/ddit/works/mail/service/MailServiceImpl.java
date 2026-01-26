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
    private JavaMailSender mailSender; // Spring 메일 전송 엔진

    @Value("${mail.username}")
    private String senderEmail;
    
    // MimeMessage 객체 안에 포함된 것 : 받는사람, 제목, 내용, 헤더, 인코딩, 첨부파일 정보
    
    // 회원가입 시 이메일 인증번호 발송 -> LoginServiceImpl에서 사용
    @Override
    public String sendAuthMail(String toEmail) {

    	// 1. 인증번호 난수 생성
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        try {
        	// 2. 메일 내용을 담는 객체 생성
            MimeMessage message = mailSender.createMimeMessage();
            // 3. HTML 메일 작성 도와줌
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // toEmail : LoginServiceImpl에서 호출 시 클라이언트에게 받은 email 주소
            // 이메일 내용
            helper.setTo(toEmail);
            helper.setSubject("[SEP] 이메일 인증번호");
            helper.setText(
                "<h3>인증번호</h3><h1>" + code + "</h1>",
                true
            );

            // SMTP 서버 통해 외부로 메일이 발송 됨 -> LoginServiceImpl에서 인증번호를 session에 저장
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("메일 전송 실패", e);
        }

        return code;
    }

    // 임시 비밀번호 발송 -> LoginServiceImpl, EmployeeServiceImpl에서 사용
    @Override
    public void sendTempPasswordMail(String email, String tempPw) {
    	
    	// 1. 메일 내용을 담는 객체 생성
        MimeMessage message = mailSender.createMimeMessage();

        try {
        	// 2. 메일 내용 설정
        	// 2-1. 발신자 설정
            message.setFrom(senderEmail);
            // 2-2. 수신자 설정
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            // 2-3. 메일 제목 설정
            message.setSubject("[SEP] 임시 비밀번호 발급");

            String body = "";
            // 2-4. 메일 내용 설정
            // tempPw : LoginServiceImpl, EmployeeServiceImpl에서 패스워드 암호화 하여 DB 저장
            body += "<h3>임시 비밀번호가 발급되었습니다.</h3>";
            body += "<p>아래 임시 비밀번호로 로그인 후, 마이페이지에서 비밀번호를 변경하세요.</p>";
            body += "<h2 style='letter-spacing:1px;'>" + tempPw + "</h2>";
            body += "<p style='color:#888;'>보안을 위해 로그인 후 즉시 변경을 권장합니다.</p>";

            // 3. HTML 메일 설정
            message.setText(body, "UTF-8", "html");

            // 4. 메일 발송
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("임시 비밀번호 메일 발송 실패", e);
        }
    }

}

