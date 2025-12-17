package kr.or.ddit.works.organization.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "deptCd")
public class DepartmentVO implements Serializable{

	private String deptCd;      		//부서코드
	private String parentDeptCd;      	//상위 부서 코드
	private String deptName;      		//부서명
	private String managerEmpId;      	//부서장
	private String createAt;      		//부서 생성일
	private String companyNo;			//고객사 번호
	
}
