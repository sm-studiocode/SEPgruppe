package kr.or.ddit.works.login.service;

import kr.or.ddit.works.login.exception.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.works.company.vo.CompanyDivisionVO;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.mybatis.mappers.LoginMapper;
import kr.or.ddit.works.organization.vo.EmployeeVO;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private LoginMapper mapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 회원가입 서비스 로직
	@Override
	@Transactional
	public void joinCompany(CompanyVO company) {
		
	    if (mapper.existsContactId(company.getContactId()) > 0) {
	        throw new LoginException("이미 사용중인 아이디입니다.");
	    }

	    // 1. COMPANY_NO
	    long seq = mapper.selectCompanySeq();
	    String companyNo = "COM" + String.format("%06d", seq);
	    company.setCompanyNo(companyNo);

	    // 2. 비밀번호
	    String encodedPw = passwordEncoder.encode(company.getContactPw());
	    company.setContactPw(encodedPw);

	    // 3. adminId
	    String adminId = company.getContactId() + "_admin";
	    company.setAdminId(adminId);

	    // 4. COMPANIES (부모)
	    mapper.joinCompany(company);

	    // 5. COMPANY_DIVISION (자식)
	    CompanyDivisionVO division = new CompanyDivisionVO();
	    division.setCompanyNo(companyNo);
	    division.setContactId(company.getContactId());
	    mapper.insertCompanyDivision(division);

	}

}
