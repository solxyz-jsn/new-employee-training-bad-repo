package jp.co.solxyz.jsn.springbootadvincedexam.security;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsTest {

    private UserAccount userAccount;
    private MyUserDetails userDetails;

    @BeforeEach
    void setUp() {
        userAccount = new UserAccount();
        userAccount.setIsAdmin(true);
        userAccount.setUserId("testUser");
        userAccount.setPassword("testPassword");
        userAccount.setUsername("testUsername");
        userDetails = new MyUserDetails(userAccount);
    }

    @Test
    @DisplayName("ユーザーが管理者の場合、適切な権限が返される")
    void shouldReturnCorrectAuthoritiesWhenUserIsAdmin() {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("ユーザーが管理者でない場合、適切な権限が返される")
    void shouldReturnCorrectAuthoritiesWhenUserIsNotAdmin() {
        userAccount.setIsAdmin(false);
        userDetails = new MyUserDetails(userAccount);

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("ユーザー情報が正しく返される")
    void shouldReturnCorrectUserInformation() {
        assertThat(userDetails.getUsername()).isEqualTo(userAccount.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(userAccount.getPassword());
        assertThat(userDetails.getUserId()).isEqualTo(userAccount.getUserId());
    }

    @Test
    @DisplayName("アカウントの状態が正しく返される")
    void shouldReturnCorrectAccountStatus() {
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        assertThat(userDetails.isEnabled()).isTrue();
    }
}

