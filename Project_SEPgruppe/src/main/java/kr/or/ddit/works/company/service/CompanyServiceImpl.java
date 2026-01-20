package kr.or.ddit.works.company.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
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

	// 마이페이지 정보 수정
	@Override
	public boolean updateCompany(CompanyVO member) {

		String plainPw = member.getContactPw();
		boolean pwChanged = (plainPw != null && !plainPw.isEmpty());

		// 비밀번호 입력한 경우에만 암호화해서 세팅 (입력 안 했으면 mapper 쪽 if로 UPDATE에서 빠짐)
		if (pwChanged) {
			String encoded = passwordEncoder.encode(plainPw);
			member.setContactPw(encoded);
		}

		boolean result = mapper.updateCompany(member) > 0;

		// ✅ 비밀번호가 바뀐 경우에만 인증정보 갱신
		if (result && pwChanged) {
			createNewAuthentication();
		}

		return result;
	}

	// 마이페이지 정보수정 비밀번호 인증 (현재 비밀번호 확인)
	@Override
	public boolean authenticateMember(String contactId, String contactPw) {
		String realPw = mapper.selectCompany(contactId).getContactPw();
		return passwordEncoder.matches(contactPw, realPw);
	}

	// 변경된 정보로 새로운 인증 생성 (비밀번호 변경 시에만 호출)
	private void createNewAuthentication() {
		Authentication beforeAuth = SecurityContextHolder.getContext().getAuthentication();

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		UserDetails principal = userDetailService.loadUserByUsername(beforeAuth.getName());
		Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
		Object details = beforeAuth.getDetails();

		UsernamePasswordAuthenticationToken newAuthentication =
				UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities);

		newAuthentication.setDetails(details);
		context.setAuthentication(newAuthentication);
		SecurityContextHolder.setContext(context);
	}

	@Override
	public List<CompanyVO> companyList() {
		return mapper.companyList();
	}


}
