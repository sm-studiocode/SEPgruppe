package kr.or.ddit.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.organization.vo.AuthoritiesDTO;
import lombok.ToString;

// UserDetails의 구현체
@ToString
public class RealUserWrapper implements UserDetails {

    private final AllUserVO realUser;

    public RealUserWrapper(AllUserVO realUser) {
        this.realUser = realUser;
    }

    public AllUserVO getRealUser() {
        return realUser;
    }

    // Spring Security가 권한 체크할 때 호출하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

    	// 권한을 중복없이 담기 위한 Set
        Set<String> granted = new LinkedHashSet<>();

        // 사용자 Target 가져오기 (사용자 타입 : EMPLOYEE, COMPANY, PROVIDER)
        String target = realUser.getTarget();
        
        // Target이 없는 계정은 시스템 오류로 간주
        if (target == null || target.trim().isEmpty()) {
            throw new IllegalStateException("TARGET 비어있음(userId=" + realUser.getUserId() + ")");
        }
        
        // Target을 권한 목록에 추가
        // - 화면/URL 분기 및 접근 제어에 사용
        granted.add(target);

        // DB에 저장된 ROLE 정보를 조회 -> Spring Security 권한으로 변환
        List<AuthoritiesDTO> roles = realUser.getAuthorities();
        if (roles != null) {
            for (AuthoritiesDTO role : roles) {
                String roleName = role.getRoleName();
                if (roleName != null && !roleName.trim().isEmpty()) {
                    granted.add(roleName);
                }
            }
        }

        // 아이디에 admin 포함 시 관리자 권한 강제 부여
        if (realUser.getUserId() != null && realUser.getUserId().toLowerCase().contains("admin")) {
            granted.add("ROLE_ADMIN");
        }

        // Spring Security는 문자열이 아니라 객체만 인식함
        // 권한 String 타입을 GrantedAuthority 객체 타입으로 변환
        List<GrantedAuthority> authorities = new ArrayList<>(granted.size());
        for (String a : granted) {
            authorities.add(new SimpleGrantedAuthority(a));
        }
        return authorities;
    }

    // Security가 해당 메서드에서 패스워드를 꺼내 비교
    @Override public String getPassword() { 
    	return realUser.getUserPw(); 
    }
    
    // Security가 해당 메서드에서 아이디를 꺼내 비교
    @Override public String getUsername() {
    	return realUser.getUserId(); 
    }

    // 계약 기간 끝난 계정 -> 현재 true라서 적용 안됨
    @Override public boolean isAccountNonExpired() {
    	return true; 
    }
    
    // 관리자에 의해 계정 정지 -> 현재 true라서 적용 안됨
    @Override public boolean isAccountNonLocked() { 
    	return true; 
    }
    
    // 90일 지나면 비밀번호 변경 -> 현재 true라서 적용 안됨
    @Override public boolean isCredentialsNonExpired() { 
    	return true; 
    }
    
    // 탈퇴 계정은 비활성 처리
    @Override public boolean isEnabled() { 
    	return !realUser.isRetire(); 
    }
}
