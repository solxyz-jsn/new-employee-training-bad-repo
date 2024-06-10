package jp.co.solxyz.jsn.springbootadvincedexam.security;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.user.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDetailsServiceTest {

    @InjectMocks
    private MyUserDetailsService userDetailsService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("emailに一致するユーザーが見つかった場合、UserDetailsが返される")
    void shouldReturnUserDetailsWhenUserIsFound() {
        UserAccount expected = new UserAccount();
        expected.setUserId("user1");
        expected.setUsername("test@example.com");
        expected.setEmail("test@example.com");
        expected.setPassword("password");

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("user1");
        userAccount.setUsername("test@example.com");
        userAccount.setEmail("test@example.com");
        userAccount.setPassword("password");

        when(userAccountRepository.findByEmail(userAccount.getEmail())).thenReturn(userAccount);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userAccount.getEmail());

        assertThat(userDetails.getUsername()).isEqualTo(expected.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(expected.getPassword());
        verify(userAccountRepository, times(1)).findByEmail(expected.getEmail());
    }

    @Test
    @DisplayName("ユーザーが見つからない場合、UsernameNotFoundExceptionが発生する")
    void shouldThrowUsernameNotFoundExceptionWhenUserIsNotFound() {
        String email = "test@example.com";

        when(userAccountRepository.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }
}
