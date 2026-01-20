package kr.or.ddit.works.login.service;

import kr.or.ddit.works.login.exception.LoginException;
import kr.or.ddit.works.mail.service.MailService;

import javax.servlet.http.HttpSession;

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
	
	@Autowired
	private MailService mailService;
	
	private static final String JOIN_MAIL_CODE = "JOIN_MAIL_CODE";
	private static final String JOIN_MAIL_EMAIL = "JOIN_MAIL_EMAIL";
	private static final String JOIN_MAIL_VERIFIED = "JOIN_MAIL_VERIFIED";
	
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
	
	// 아이디 찾기 서비스 로직
	@Override
	public String findContactId(CompanyVO company) {
	    String contactId = mapper.findContactId(company);
	    
	    if (contactId == null) {
	        throw new LoginException("계정이 존재하지 않습니다.");
	    }
	    
		return contactId;
	}

	// 비밀번호 찾기 서비스 로직
	@Override
	public void updateContactPw(CompanyVO company) {
		
		// 1. ID가 존재할 경우
	    String encodedPw = passwordEncoder.encode(company.getContactPw());
		company.setContactPw(encodedPw);

		// 2. 비밀번호 변경 시도
		int updated = mapper.updateContactPw(company);
		
		if(updated == 0) {
			throw new LoginException("비밀번호 변경에 실패했습니다.");
		}
		
	}

	@Override
	public void sendJoinMailAuthCode(String email, HttpSession session) {

	    if(email == null || email.trim().isEmpty()) {
	        throw new LoginException("이메일을 입력하세요.");
	    }

	    String code = mailService.sendAuthMail(email);

	    session.setAttribute(JOIN_MAIL_CODE, code);
	    session.setAttribute(JOIN_MAIL_EMAIL, email);
	    session.setAttribute(JOIN_MAIL_VERIFIED, false);
	}

	@Override
	public boolean checkJoinMailAuthCode(String userNumber, HttpSession session) {

	    String savedCode = (String) session.getAttribute(JOIN_MAIL_CODE);

	    if(savedCode == null || userNumber == null) return false;

	    boolean ok = userNumber.trim().equals(savedCode);

	    if(ok) {
	        session.setAttribute(JOIN_MAIL_VERIFIED, true);
	    }

	    return ok;
	}

	private String generateTempPw() {
	    // 12자리: 영문(대/소) + 숫자
	    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < 12; i++) {
	        int idx = (int) (Math.random() * chars.length());
	        sb.append(chars.charAt(idx));
	    }
	    return sb.toString();
	}
	
    @Override
    public void issueTempPassword(CompanyVO company) {

        int exists = mapper.existsForPwReset(company);
        if (exists == 0) {
            throw new LoginException("입력한 정보와 일치하는 계정이 없습니다.");
        }

        String tempPw = generateTempPw();

        String encoded = passwordEncoder.encode(tempPw);
        company.setContactPw(encoded);

        int updated = mapper.updateContactPw(company);
        if (updated == 0) {
            throw new LoginException("임시 비밀번호 발급에 실패했습니다.");
        }

        // 입력한 이메일 == DB 이메일이 일치해야 existsForPwReset이 통과하므로, 이 이메일로 발송해도 안전
        mailService.sendTempPasswordMail(company.getContactEmail(), tempPw);
    }
}
