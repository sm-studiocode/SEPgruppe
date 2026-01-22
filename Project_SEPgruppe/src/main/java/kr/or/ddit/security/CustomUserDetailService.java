package kr.or.ddit.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.mybatis.mappers.LoginMapper;
import lombok.extern.slf4j.Slf4j;


// 스프링 시큐리티가 인증(Authentication)을 수행할 수 있도록
// 사용자 정보를 제공하는 UserDetailsService 구현체

// 1. 사용자가 로그인 폼에서 ID/PW 입력
// 2. 입력한 아이디를 추출
// 3. CustomUserDetailService.loadUserByUsername(username) 호출
// 4. DB에서 사용자 조회
// 5. 비밀번호 비교 (Security가 수행)
// 6. 성공 또는 실패 판단
@Component
@Slf4j
public class CustomUserDetailService implements UserDetailsService{
	
	@Autowired
	private LoginMapper mapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// 아이디로 사용자 조회
		AllUserVO user = mapper.login(username);
		
		// 사용자가 없을 경우 예외 발생
		if(user==null) throw new UsernameNotFoundException(String.format("%s 사용자 없음.", username));
		
		// 로그 확인
		log.info("🔍 로그인 시도 - ID: {}", username);

		// UserDetails 구현체 (RealUserWrapper)로 반환
		return new RealUserWrapper(user);
	}

}















