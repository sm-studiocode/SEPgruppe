package kr.or.ddit.works.login.service;


import kr.or.ddit.works.login.exception.LoginException;

import kr.or.ddit.works.company.vo.CompanyVO;

public interface LoginService {

	// 회원가입 MAPPER
	public void joinCompany(CompanyVO company) throws LoginException;
	


}
