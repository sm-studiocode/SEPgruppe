package kr.or.ddit.works.organization.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * DB 저장용이 아닌, 데이터 전달용 DTO
 */

@Data
public class AuthoritiesDTO implements Serializable{

	private Long roleNo;      	//직책번호
	private String empId;      	//사원 아이디
	private String roleName;    //직책명
	private String target;
	
}
