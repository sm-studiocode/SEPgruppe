package kr.or.ddit.works.organization.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 조직관리 VIEW
 */

@Data
@EqualsAndHashCode(of = "empId")
public class OrganizationVO implements Serializable{
	
	private String empId;      		//직원 ID (기본키)
	private String empPw;      		//직원 로그인 비밀번호
	private String companyNo;      	//소속 회사의 고유 번호
	private String deptName;      	//직원이 속한 부서명
	private String deptCd;			//부서코드
	private String managerEmpId;	//부서장
	private String positionName;    //직원의 직위 (예: 대리, 과장)
	private String empNo;      		//직원 사번
	private String empNm;      		//직원 이름
	private String empZip;      	//직원의 우편번호
	private String empAdd1;      	//직원의 기본 주소
	private String empAdd2;      	//직원의 상세 주소
	private String empEmail;      	//직원의 이메일 주소
	private String empPhone;      	//직원의 전화번호
	private String empRegdate;      //직원의 입사일
	private String empRetire;      	//퇴사 여부 (0: 재직, 1: 퇴사)
	
	private List<String> roleName;

}
