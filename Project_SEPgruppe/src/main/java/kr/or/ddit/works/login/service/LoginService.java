package kr.or.ddit.works.login.service;


import kr.or.ddit.works.login.exception.LoginException;
import kr.or.ddit.works.company.vo.CompanyVO;

public interface LoginService {

	// 회원가입
	public void joinCompany(CompanyVO company) throws LoginException;
	
	// 아이디 찾기 + 계정 존재하는지 여부 검증
	public String findContactId(CompanyVO company);
	
	// 비밀번호 재설정
	public void updateContactPw(CompanyVO company);



}
