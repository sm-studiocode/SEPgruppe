package kr.or.ddit.works.login.service;

import kr.or.ddit.works.login.exception.LoginException;

import kr.or.ddit.works.mail.service.MailService;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.common.TempPasswordGenerator;
import kr.or.ddit.works.company.vo.CompanyDivisionVO;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.mybatis.mappers.LoginMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private LoginMapper mapper;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private PasswordEncoder passwordEncoder; 

	// Session 키 상수 (이메일 인증 상태 관리용)
	private static final String JOIN_MAIL_CODE = "JOIN_MAIL_CODE";
	private static final String JOIN_MAIL_EMAIL = "JOIN_MAIL_EMAIL";
	private static final String JOIN_MAIL_VERIFIED = "JOIN_MAIL_VERIFIED";
	
	// 회원가입 서비스 로직
	@Override
	@Transactional
	public void joinCompany(CompanyVO company) {

		// ID에 admin을 포함할 수 없음
		String id = company.getContactId();
		if (id != null && id.toLowerCase().contains("admin")) {
		    throw new LoginException("아이디에 'admin'은 포함될 수 없습니다.");
		}
		
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

	// 비밀번호 찾기 시 임시 비밀번호 발급 + 저장
    @Override
    public void issueTempPassword(CompanyVO company) {

    	// 일치하는 사용자 정보가 있는지 확인
        int exists = mapper.existsForPwReset(company);
        if (exists == 0) {
            throw new LoginException("입력한 정보와 일치하는 계정이 없습니다.");
        }

        // 임시비밀번호 생성 호출 -> 랜덤 임시 비밀번호 발급
        String tempPw = TempPasswordGenerator.generate();

        // 임시비밀번호 암호화
        company.setContactPw(passwordEncoder.encode(tempPw));

        // 임시비밀번호 DB 저장
        int updated = mapper.updateContactPw(company);
        if (updated == 0) {
            throw new LoginException("임시 비밀번호 발급에 실패했습니다.");
        }

        mailService.sendTempPasswordMail(company.getContactEmail(), tempPw);
    }
    
	// 회원가입 인증 메일 발송 + session 저장
	@Override
	public void sendJoinMailAuthCode(String email, HttpSession session) {

	    if(email == null || email.trim().isEmpty()) {
	        throw new LoginException("이메일을 입력하세요.");
	    }

	    // 인증 코드 생성+메일 발송을 MailService에 위임
	    String code = mailService.sendAuthMail(email);

	    session.setAttribute(JOIN_MAIL_CODE, code);
	    session.setAttribute(JOIN_MAIL_EMAIL, email);
	    session.setAttribute(JOIN_MAIL_VERIFIED, false);
	}

	// 회원가입 시 입력한 인증번호 검증
	@Override
	public boolean checkJoinMailAuthCode(String email, String userNumber, HttpSession session) {

	    String savedCode = (String) session.getAttribute(JOIN_MAIL_CODE);
	    String savedEmail = (String) session.getAttribute(JOIN_MAIL_EMAIL);
	    
		// session에 인증번호가 없거나, 사용자가 입력하지 않으면 실패
	    if(savedCode == null || userNumber == null) return false;

	    // 인증한 이메일과 현재 입력 이메일이 다르면 실패 처리
	    if (savedEmail == null || email == null || !email.trim().equals(savedEmail)) {
	        session.setAttribute(JOIN_MAIL_VERIFIED, false);
	        return false;
	    }
	    
	    // 사용자가 입력한 코드와 저장된 코드 비교
	    boolean ok = userNumber.trim().equals(savedCode);
	    session.setAttribute(JOIN_MAIL_VERIFIED, ok);


	    return ok;
	}
}
