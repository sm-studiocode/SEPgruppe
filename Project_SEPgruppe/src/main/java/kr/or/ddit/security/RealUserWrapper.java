package kr.or.ddit.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.or.ddit.works.login.vo.AllUserVO;
import kr.or.ddit.works.organization.vo.AuthoritiesDTO;
import lombok.ToString;

@ToString
public class RealUserWrapper implements UserDetails {
    private final AllUserVO realUser;
    

    public RealUserWrapper(AllUserVO realUser) {
    	super();
        this.realUser = realUser;
    }

    public AllUserVO getRealUser() {
        return realUser;
    }
    
    

    @Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<AuthoritiesDTO> roles = realUser.getAuthorities();
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		authorities.add(new SimpleGrantedAuthority(roles.get(0).getTarget()));
		for(AuthoritiesDTO role : roles) {
			String roleName = role.getRoleName();
	        if (roleName != null && !roleName.trim().isEmpty()) {
	            authorities.add(new SimpleGrantedAuthority(roleName));
	        }
		}
		
		if (realUser.getUserId() != null && realUser.getUserId().toLowerCase().contains("admin")) {
		    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		   
		return authorities;
	}
    
    @Override
    public String getPassword() {
        return realUser.getUserPw();
    }
    
    @Override
    public String getUsername() {
        return realUser.getUserId();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return !realUser.isRetire();
    }
}











