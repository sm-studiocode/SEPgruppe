package kr.or.ddit.works.login.vo;

import java.io.Serializable;
import java.util.List;

import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.organization.vo.AuthoritiesDTO;
import lombok.Data;

/**
 * 로그인 / 인증용 공통 사용자 정보 DTO
 */
@Data
public class AllUserVO implements Serializable{

	private String userId;
	private String userPw;
	private boolean retire;
	private String target;
	
	private List<AuthoritiesDTO> authorities;


}
