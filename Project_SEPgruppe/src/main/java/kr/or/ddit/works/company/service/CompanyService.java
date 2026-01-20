package kr.or.ddit.works.company.service;


import java.util.List;

import kr.or.ddit.works.company.vo.CompanyVO;

public interface CompanyService {

	// 마이페이지 회원정보 조회
	public CompanyVO selectCompany(String contactId);
	
	// 마이페이지 회원정보 수정
	public boolean updateCompany(CompanyVO member);
	public boolean authenticateMember(String contactId, String contactPw);
	
	public List<CompanyVO> companyList();

}
