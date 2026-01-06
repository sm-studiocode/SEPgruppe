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
	private PasswordEncoder passwordEncoder; // 패스워드 인코딩
	
	// 회원가입 서비스 로직
	@Override
	@Transactional
	public void joinCompany(CompanyVO company) {
		
		// DB를 통해 아이디 중복 여부 확인
		// 중복일 경우 커스텀 LoginException 발생 (컨트롤러 try~catch 블럭에서 처리)
	    if (mapper.existsContactId(company.getContactId()) > 0) {
	        throw new LoginException("이미 사용중인 아이디입니다.");
	    }

	    // 1. COMPANY_NO
	    // COMPANY_NO 체계 만들어 저장
	    long seq = mapper.selectCompanySeq();
	    String companyNo = "COMPANY" + String.format("%06d", seq);
	    company.setCompanyNo(companyNo);

	    // 2. 비밀번호
	    // 사용자가 입력한 ContactPw 평문 비밀번호를 BCrypt로 암호화하여 저장
	    String encodedPw = passwordEncoder.encode(company.getContactPw());
	    company.setContactPw(encodedPw);

	    // 3. adminId
	    // ADMIN_ID 체계 만들어 저장 (사용자가 입력한 아이디 + _admin)
	    String adminId = company.getContactId() + "_admin";
	    company.setAdminId(adminId);

	    // 4. COMPANIES (부모)
	    // 부모 테이블 먼저 INSERT
	    mapper.joinCompany(company);

	    // 5. COMPANY_DIVISION (자식)
	    // 자식 테이블 INSERT
	    CompanyDivisionVO division = new CompanyDivisionVO();
	    division.setCompanyNo(companyNo);
	    division.setContactId(company.getContactId());
	    mapper.insertCompanyDivision(division);

	}

}
