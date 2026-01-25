package kr.or.ddit.works.mybatis.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.company.vo.CompanyDivisionVO;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.login.vo.AllUserVO;

@Mapper
public interface LoginMapper {

	// COMPANY_NO SEQ 조회
	public long selectCompanySeq();
	
	// 회원가입 MAPPER (고객사 정보 생성)
	public void insertCompanyDivision(CompanyDivisionVO division);
	
	// 회원가입 MAPPER (회사 생성)
	public void joinCompany(CompanyVO company);
	
	// 회원가입 시 아이디 중복확인 MAPPER
	public int existsContactId(@Param(value = "contactId") String contactId);
	
	// 로그인 처리
	// 서비스 로직 없음, 스프링 시큐리티가 처리
	public AllUserVO login(@Param("userId") String userId);
	
	// 아이디 찾기 + 계정 존재하는지 여부 검증 MAPPER
	public String findContactId(CompanyVO company);
	
	// 비밀번호 찾기 계정 검증 MAPPER
	public int existsForPwReset(CompanyVO company);

	// 비밀번호 재설정 MAPPER
	public int updateContactPw(CompanyVO company);

}
