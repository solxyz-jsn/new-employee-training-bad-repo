package jp.co.solxyz.jsn.springbootadvincedexam.app.user.login.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("/loginにアクセスした場合、ログイン画面が表示されること")
    void shouldReturnLoginViewWhenLoginEndpointIsHit() {
        String view = loginController.login();

        assertThat(view).isEqualTo("auth/login");
    }

    @Test
    @DisplayName("トップページにアクセスした場合、ログイン画面へリダイレクトされること")
    void shouldRedirectToLoginViewWhenHomeEndpointIsHit() {
        String view = loginController.home();

        assertThat(view).isEqualTo("redirect:/login");
    }
}
