package kr.or.ddit.works.organization.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 직책 VO
 */

@Data
public class EmpRoleVO implements Serializable{

    private Long roleNo;      // 직책번호(SEQ_EMP_ROLE)
    private String empId;     // 사원 아이디
    private String roleName;  // 권한명
    private String deptCd;    // 권한 스코프(부서코드). 전사권한이면 NULL
	
}
