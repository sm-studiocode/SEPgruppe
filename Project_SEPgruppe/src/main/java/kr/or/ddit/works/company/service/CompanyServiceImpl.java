package kr.or.ddit.works.company.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.ddit.security.CustomUserDetailService;
import kr.or.ddit.works.company.vo.CompanyDivisionVO;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.mybatis.mappers.CompanyMapper;
import kr.or.ddit.works.organization.service.EmployeeService;
import kr.or.ddit.works.organization.vo.EmployeeVO;


@Service
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private CompanyMapper mapper;
	
	@Autowired
	private EmployeeService empService;   

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private CustomUserDetailService userDetailService;

	
	// 마이페이지 회원정보 조회
	@Override
	public CompanyVO selectCompany(String contactId) {
		return mapper.selectCompany(contactId);
	}
	
	// 마이페이지 정보수정 비밀번호 인증 (현재 비밀번호 확인)
	@Override
	public boolean authenticateMember(String contactId, String contactPw) {
		
		String realPw = mapper.selectCompany(contactId).getContactPw();
		return passwordEncoder.matches(contactPw, realPw);
		
	}

	// 마이페이지 정보 수정
	@Override
	public boolean updateCompany(CompanyVO member) {

		// 사용자가 새로운 비밀번호를 입력하지 않았다면 false : 비밀번호 변경 안 함
		// 사용자가 비밀번호를 입력했다면 true
		String plainPw = member.getContactPw();
		boolean pwChanged = (plainPw != null && !plainPw.isEmpty());

		// 비밀번호 입력한 경우에만 암호화해서 세팅 
		if (pwChanged) {
			String encoded = passwordEncoder.encode(plainPw);
			member.setContactPw(encoded);
		}

		// 정보수정 처리
		boolean result = mapper.updateCompany(member) > 0;

		// 비밀번호가 변경된 경우 SecurityContext를 새로 만들어서 교체
		if (result && pwChanged) {
			createNewAuthentication();
		}

		return result;
	}

	// 변경된 정보로 새로운 인증 생성 (비밀번호 변경 시에만 호출)
	private void createNewAuthentication() {
		
		// 1. 기존 인증정보 가져오기
		Authentication beforeAuth = SecurityContextHolder.getContext().getAuthentication();

		// 2. 최신 사용자 정보 로드
		UserDetails principal = userDetailService.loadUserByUsername(beforeAuth.getName());
		
		// 3. 권한 정보 유지
		Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
		
		// 4. 새로운 인증 객체 생성 (null : 이미 인증된 상태라 비번 필요 없음)
		UsernamePasswordAuthenticationToken newAuthentication =
				UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities);

		// 5. 기존 세부 정보 유지
	    newAuthentication.setDetails(beforeAuth.getDetails());
		
		// 6. SecurityContext 교체 (로그아웃 없이 인증정보 갱신 완료)
	    SecurityContextHolder.getContext().setAuthentication(newAuthentication);

	}
	
	// 관리자페이지 대시보드 전제 목록 가져오기 - ProviderController에서 사용
	@Override
	public List<CompanyVO> companyList() {
		return mapper.companyList();
	}

	// 구독 성공 후 회사의 기본 조직 구조 및 관리자 계정 자동 세팅 - PaymentServiceImpl에서 사용
	@Override
	public void ensureAdminSetup(String contactId) {

		// 1. 등록된 회사가 있는지 확인
	    CompanyVO company = mapper.selectCompany(contactId);
	    if (company == null) {
	        throw new IllegalStateException("회사 정보 없음: " + contactId);
	    }

	    try {
		    // 2. COMPANY 테이블에 등록된 회사가 있으면 DOMPANY_DIVISION INSERT
	        CompanyDivisionVO div = new CompanyDivisionVO();
	        div.setCompanyNo(company.getCompanyNo()); 
	        div.setContactId(company.getContactId());
	        mapper.insertCompanyDivision(div);
	    } catch (DuplicateKeyException e) {
	        // 이미 있으면 스킵
	    }

	    // 3. EMPLOYEE 관리자 계정 생성
	    String adminEmpId = contactId + "_admin";

	    // 4. 값 넣기
	    EmployeeVO member = new EmployeeVO();
	    member.setEmpId(adminEmpId);
	    member.setCompanyNo(company.getCompanyNo());
	    member.setEmpNm(company.getCompanyName());
	    member.setEmpZip(company.getCompanyZip());
	    member.setEmpAdd1(company.getCompanyAdd1());
	    member.setEmpAdd2(company.getCompanyAdd2());
	    // 임시 비밀번호 발송을 위한 이메일 세팅 
	    member.setEmpEmail(company.getContactEmail());

	    // EMPLOYEE 테이블 insert
	    boolean created = empService.createAdminWithTempPassword(member);

	    // 새로 만들어졌을 때만 회사 관리자 ID 업데이트
	    if (created) {
	        mapper.updateCompanyAdmin(member.getEmpId(), contactId);
	    }
	}

}
