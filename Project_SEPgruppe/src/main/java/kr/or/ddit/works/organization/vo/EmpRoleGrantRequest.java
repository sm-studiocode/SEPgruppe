package kr.or.ddit.works.organization.vo;

import lombok.Data;

@Data
public class EmpRoleGrantRequest {
    private String empId;
    private String roleName; // ROLE_NOTICE_ADMIN, ROLE_NOTICE_DEPT_ADMIN 등
    private String deptCd;   // ROLE_NOTICE_DEPT_ADMIN일 때만 사용(그 외는 null)
}