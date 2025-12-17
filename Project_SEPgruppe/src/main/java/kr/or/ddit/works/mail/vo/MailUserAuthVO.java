package kr.or.ddit.works.mail.vo;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "empId")
public class MailUserAuthVO implements Serializable{
	
	private String empId;      			//사원 아이디
	private String emailAdd;      		//이메일 주소
	private String accessToken;      	//Gmail 토큰
	private String refreshToken;      	//Gmail 갱신 토큰
	private String authType;      		//토큰 인증 유형
	private Timestamp tokenExpiry;     	//토큰 만료일
	private String firstLinkedAt;      	//최초 연동일시
	private String tokenLastUpdate;     //토큰 마지막 갱신
	
}
