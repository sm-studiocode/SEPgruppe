package kr.or.ddit.works.company.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "contactId")
public class CompaniesVO implements Serializable{

	private String contactId;		// 고객사 아이디
	private String companyName;		// 회사이름
	@NotBlank
	private String contactNm;		// 고객사 관리자 이름
	@NotBlank
	private String contactPhone;	// 고객사 관리자 연락처
	@NotBlank
	private String contactEmail;	// 고객사 관리자 이메일
	@NotBlank
	private String companyZip;		// 고객사 비밀번호
	private String companyAdd1;		// 사업자 등록번호
	private String companyAdd2;		// 탈퇴여부
	@NotBlank
	private String contactPw;		// 고객사 번호
	private String businessRegNo;	// 그룹웨어 관리자 아이디
	private String isDeleted;		// 고객사 우편번호
	private String companyNo;		// 고객사 주소
	private String adminId;			// 고객사 상세주소
	private String signUp;			// 고객사 가입일
}
