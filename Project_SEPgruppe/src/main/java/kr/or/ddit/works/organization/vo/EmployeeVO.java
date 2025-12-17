package kr.or.ddit.works.organization.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import kr.or.ddit.works.login.vo.AllUserVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "empId")
public class EmployeeVO extends AllUserVO implements Serializable {
	private String empId;      		// 사원 아이디
	@NotBlank
	private String companyNo;      	// 고객사번호
	private String positionCd;      // 직위코드
	private String positionName;	// 직책이름
	private String deptCd;      	// 부서코드
	private String deptName;		// 부서이름
	@NotBlank
	private String empNo;      		// 사원번호
	@NotBlank
	private String empNm;     		// 사원이름
	private String empPw; 			// 사원 비밀번호
	@NotBlank
	private String empZip;      	// 우편번호
	@NotBlank
	private String empAdd1;      	// 주소
	private String empAdd2;      	// 상세주소
	private String empEmail;      	// 이메일
	private String empPhone;      	// 사원 전화번호
	private String empRegdate;      // 입사년도
	private String empImg;			// 파일 경로
	private String empBank;			// 은행명
	private String empDepositor;	// 예금주
	private String empBankno;		// 계좌번호
	private String empRetire;		// 퇴사여부
	private String empGender;		// 성별
}
