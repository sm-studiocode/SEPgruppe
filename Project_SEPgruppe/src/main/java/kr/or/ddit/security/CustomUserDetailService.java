package kr.or.ddit.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.mybatis.mappers.LoginMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 시 입력한 사용자가 조회되는지 검증
 */

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

		// UserDetails 구현체로 반환
		return new RealUserWrapper(user);
	}

}















