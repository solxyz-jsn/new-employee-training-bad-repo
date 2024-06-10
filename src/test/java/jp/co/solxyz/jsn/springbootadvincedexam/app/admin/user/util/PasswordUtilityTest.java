package jp.co.solxyz.jsn.springbootadvincedexam.app.user.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PasswordUtilityTest {

    @InjectMocks
    private PasswordUtility passwordUtility;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("パスワードのハッシュ化が正常に動作する")
    void shouldHashPasswordSuccessfully() {
        String rawPassword = "rawPassword";
        String hashedPassword = "hashedPassword";
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);

        String result = passwordUtility.hashPassword(rawPassword);

        assertThat(result).isEqualTo(hashedPassword);
    }
}
