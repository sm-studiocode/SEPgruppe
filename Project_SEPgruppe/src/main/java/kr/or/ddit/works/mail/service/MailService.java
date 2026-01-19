package kr.or.ddit.works.mail.service;

public interface MailService {
	
	// 가입 시 메일 인증 담당 구현체
	public String sendAuthMail(String toEmail);
}
