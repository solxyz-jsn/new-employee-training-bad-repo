package jp.co.solxyz.jsn.springbootadvincedexam.security;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class MyUserDetails implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 7985440768523436212L;
    private final UserAccount userAccount;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userAccount.getIsAdmin()
                ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public String getUserId() {
        return this.userAccount.getUserId();
    }

    @Override
    public String getPassword() {
        return this.userAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return this.userAccount.getUsername();
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
        return true;
    }
}
