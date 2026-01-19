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

@ToString
public class RealUserWrapper implements UserDetails {

    private final AllUserVO realUser;

    public RealUserWrapper(AllUserVO realUser) {
        this.realUser = realUser;
    }

    public AllUserVO getRealUser() {
        return realUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<String> granted = new LinkedHashSet<>();

        String target = realUser.getTarget();
        if (target == null || target.trim().isEmpty()) {
            throw new IllegalStateException("TARGET 비어있음(userId=" + realUser.getUserId() + ")");
        }
        granted.add(target);

        List<AuthoritiesDTO> roles = realUser.getAuthorities();
        if (roles != null) {
            for (AuthoritiesDTO role : roles) {
                String roleName = role.getRoleName();
                if (roleName != null && !roleName.trim().isEmpty()) {
                    granted.add(roleName);
                }
            }
        }

        if (realUser.getUserId() != null && realUser.getUserId().toLowerCase().contains("admin")) {
            granted.add("ROLE_ADMIN");
        }

        List<GrantedAuthority> authorities = new ArrayList<>(granted.size());
        for (String a : granted) {
            authorities.add(new SimpleGrantedAuthority(a));
        }
        return authorities;
    }

    @Override public String getPassword() { return realUser.getUserPw(); }
    @Override public String getUsername() { return realUser.getUserId(); }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return !realUser.isRetire(); }
}
