package kr.or.ddit.works.mail.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "mailRecpNo")
public class MailReceptionVO implements Serializable{

	private Long mailRecpNo;      		//메일 수신 번호
	private String empId;      			//사원 아이디
	private Long mailCategoryNo;      	//메일 카테고리 번호
	private String mailId;      		//메일 아이디
	private String fromEmail;      		//발신자 이메일 주소
	private String toEmail;      		//수신자 이메일 주소
	private String ccEmail;      		//참조 수신자 이메일 주소
	private String mailSubject;      	//메일 제목
	private String mailBody;      		//메일 본문
	private Long fileGroupNo;      		//파일그룹번호
	private Timestamp mailDate;      	//메일 수신 날짜
	private int mailFav;      			//즐겨찾기 여부
		
	private String formattedDate;		//날짜 포맷용

}
