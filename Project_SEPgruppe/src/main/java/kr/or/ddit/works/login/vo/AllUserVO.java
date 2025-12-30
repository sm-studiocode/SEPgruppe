package kr.or.ddit.works.login.vo;

import java.io.Serializable;
import java.util.List;

import kr.or.ddit.works.company.vo.CompaniesVO;
import kr.or.ddit.works.organization.vo.AuthoritiesDTO;
import lombok.Data;

/**
 * ALL_USER VIEW
 */

@Data
public class AllUserVO implements Serializable{

	private String userId;
	private String userPw;
	private boolean retire;
	private String target;
	
	private List<AuthoritiesDTO> authorities;


}
