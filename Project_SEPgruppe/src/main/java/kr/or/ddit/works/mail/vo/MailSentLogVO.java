package kr.or.ddit.works.mail.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "mailSentNo")
public class MailSentLogVO implements Serializable{

	private Long mailSentNo;      	//메일 발신 번호
	private String empId;      		//사원 아이디
	private String mailId;      	//메일 아이디
	private String toEmail;      	//수신자 이메일 주소
	private String ccEmail;      	//참조 수신자 이메일 주소
	private String bccEmail;      	//숨은 참조 이메일 주소
	private String mailSubject;     //메일 제목
	private String sentCotentLog;   //메일 본문
	private Long fileGroupNo;      	//파일그룹번호
	private Timestamp mailDate;     //메일 발송 시간
	
}
