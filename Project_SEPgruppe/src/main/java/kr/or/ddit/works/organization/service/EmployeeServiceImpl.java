package kr.or.ddit.works.organization.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.or.ddit.common.TempPasswordGenerator;
import kr.or.ddit.works.mail.service.MailService;
import kr.or.ddit.works.mybatis.mappers.EmployeeMapper;
import kr.or.ddit.works.organization.vo.EmployeeVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeMapper mapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MailService mailService;


	// PaymentServiceImpl.insertEmpAdminIfNeeded()에서 관리자 직원이 없으면 
	// 관리자 직원 Insert하는 Service
	// Employee에서 관리자 생성 + 임시비번 메일 발송까지 진행하는 Service
	@Override
	public boolean createAdminWithTempPassword(EmployeeVO member) {
		// Insert 시 패스워드는 임시 비밀번호로 생성
        String tempPw = TempPasswordGenerator.generate();

        // EMPLOYEE 테이블에 저장할 비밀번호 암호화
        member.setEmpPw(passwordEncoder.encode(tempPw));

        // EMPLOYEE INSERT 시도
        try {
            mapper.insertEmployee(member);
		} catch (DuplicateKeyException e) {
			// PK를 기준으로 중복된 값이 있으면 INSERT 취소
	        return false;
		}
        // 임시 비밀번호 메일 발송
        // try~catch (메일 발송 실패해도 DB EMPLOYEE 테이블 INSERT 볼백 안 됨)
        try {
            mailService.sendTempPasswordMail(member.getEmpEmail(), tempPw);
        } catch (Exception e) {
            log.error("관리자 임시비밀번호 메일 발송 실패: {}", member.getEmpEmail(), e);
        }
        
        return true; // 모든 항목이 통과하면 성공
    }

}