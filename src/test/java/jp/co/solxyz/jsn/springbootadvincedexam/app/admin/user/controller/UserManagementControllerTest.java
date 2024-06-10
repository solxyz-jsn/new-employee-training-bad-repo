package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model.UserManagementModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class UserManagementControllerTest {

    @InjectMocks
    UserManagementController controller;

    @Mock
    UserManagementService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ユーザー管理画面が正常に表示される")
    void shouldDisplayUserManagementPageSuccessfully() {
        UserManagementModel user1 = new UserManagementModel();
        UserManagementModel user2 = new UserManagementModel();
        List<UserManagementModel> users = List.of(user1, user2);

        when(service.getAllUsers()).thenReturn(users);

        ModelAndView mav = controller.index();

        assertThat(mav.getViewName()).isEqualTo("admin/usermanagement");
        assertThat(mav.getModel().get("users")).isEqualTo(users);
    }

    @Test
    @DisplayName("ユーザーがいない場合でも、ユーザー管理画面が正常に表示される")
    void shouldDisplayUserManagementPageSuccessfullyEvenIfNoUsers() {
        when(service.getAllUsers()).thenReturn(Collections.emptyList());

        ModelAndView mav = controller.index();

        assertThat(mav.getViewName()).isEqualTo("admin/usermanagement");
        assertThat(((List) mav.getModel().get("users"))).isEqualTo(Collections.emptyList());
    }
}
