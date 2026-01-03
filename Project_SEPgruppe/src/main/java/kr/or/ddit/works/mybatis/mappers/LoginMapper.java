package kr.or.ddit.works.mybatis.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.or.ddit.works.company.vo.CompanyDivisionVO;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.organization.vo.EmployeeVO;

// 로그인 mapper
@Mapper
public interface LoginMapper {

	// COMPANY_NO SEQ 조회
	public long selectCompanySeq();
	
	// EMPLOYY_NO SEQ 조회
	public long selectEmpSeq();
	
	// 회원가입 MAPPER (고객사 정보 생성)
	public void insertCompanyDivision(CompanyDivisionVO division);

	// 회원가입 MAPPER (관리자 생성)
	public void joinEmployee(EmployeeVO employee);
	
	// 회원가입 MAPPER (회사 생성)
	public void joinCompany(CompanyVO company);
	
	// 회원가입 시 아이디 중복확인 MAPPER
	public int existsContactId(@Param(value = "contactId") String contactId);
}
