package kr.or.ddit.works.login.service;


import kr.or.ddit.works.login.exception.LoginException;

import javax.servlet.http.HttpSession;

import kr.or.ddit.works.company.vo.CompanyVO;

public interface LoginService {

	// 회원가입
	public void joinCompany(CompanyVO company) throws LoginException;
	
	// 아이디 찾기 + 계정 존재하는지 여부 검증
	public String findContactId(CompanyVO company);

	// 회원가입 시 이메일 인증
    public void sendJoinMailAuthCode(String email, HttpSession session);
    public boolean checkJoinMailAuthCode(String userNumber, HttpSession session);

    // 비밀번호 찾기 인증용
    public void issueTempPassword(CompanyVO company);
}
