package kr.or.ddit.works.address.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "adbkNo")		
public class AddressbookVO implements Serializable{
	
	@NotNull
	private Long adbkNo;      		// 주소록번호
	private String empId;      		// 사원 아이디
	private String adbkName;     	// 이름
	private String adbkCompany;     // 회사명
	private String adbkDept;      	// 부서
	private String adbkPosition;    // 직급
	private String adbkEmail;      	// 이메일
	private String adbkPhone;      	// 휴대폰번호
	private String adbkMemo;      	// 메모
	private String adbkDelYn;      	// 삭제 여부
	private String adbkIsExternal;  // 내부/외부 구분 (I/E)
	
	private String companyNo;		// 고객사 ID (URL/세션에서 전달되는 값, DB 컬럼 아님)

	
}
