package kr.or.ddit.works.account.credential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import kr.or.ddit.common.TempPasswordGenerator;
import kr.or.ddit.works.mail.service.MailService;
import kr.or.ddit.works.mail.type.MailPurpose;

@Component
public class TempPasswordIssuer {

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private MailService mailService;

    // 임시비번 생성 + 메일 발송 + 암호화된 비번 반환
    public String issueAndSend(String email, MailPurpose purpose) {
        String rawTempPw = TempPasswordGenerator.generate();

        mailService.sendTempPasswordMail(email, rawTempPw);

        return passwordEncoder.encode(rawTempPw);
    }
}
