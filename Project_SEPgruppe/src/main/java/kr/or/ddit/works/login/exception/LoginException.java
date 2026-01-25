package kr.or.ddit.works.login.exception;

// 로그인과 회원가입 도중 발생하는 비즈니스 에러 처리
public class LoginException extends RuntimeException{

	
	public LoginException(String message) {
		super(message);
	}
	
	public LoginException(String message, Throwable cause) {
		super(message, cause);
	}
}
