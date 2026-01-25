package kr.or.ddit.works.company.service;


import java.util.List;

import kr.or.ddit.works.company.vo.CompanyVO;

public interface CompanyService {

	// 마이페이지 회원정보 조회
	public CompanyVO selectCompany(String contactId);
	
	// 마이페이지 회원정보 수정을 위한 비밀번호 검증
	public boolean authenticateMember(String contactId, String contactPw);
	
	// 마이페이지 회원정보 수정
	public boolean updateCompany(CompanyVO member);

	// 관리자페이지 대시보드 전제 목록 가져오기 - ProviderController에서 사용
	public List<CompanyVO> companyList();

}
