package kr.or.ddit.works.organization.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 직책 VO
 */

@Data
public class EmpRoleVO implements Serializable{

	private Long roleNo;      	//직책번호
	private String empId;      	//사원 아이디
	private String roleName;    //직책명
	
}
