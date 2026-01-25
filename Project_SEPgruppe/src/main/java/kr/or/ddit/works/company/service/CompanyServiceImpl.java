package kr.or.ddit.works.company.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.or.ddit.security.CustomUserDetailService;
import kr.or.ddit.works.company.vo.CompanyVO;
import kr.or.ddit.works.mybatis.mappers.CompanyMapper;


@Service
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private CompanyMapper mapper;
	
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


}
