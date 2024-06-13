package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.api;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.json.AccountProfile;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.service.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserManagementApiControllerTest {

    @InjectMocks
    UserManagementApiController controller;

    @Mock
    UserManagementService service;

    @Mock
    BindingResult bindingResult;

    MockMvc mockMvc;

    private AccountProfile expectedForm;

    private AccountProfile form;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserManagementApiController(service))
                .build();

        expectedForm = new AccountProfile();
        expectedForm.setUserId("123e4567-e89b-12d3-a456-426614174000");
        expectedForm.setUserName("user1");
        expectedForm.setEmail("test@solxyz.co.jp");
        expectedForm.setPassword("password");
        expectedForm.setIsAdmin(false);

        form = new AccountProfile();
        form.setUserId("123e4567-e89b-12d3-a456-426614174000");
        form.setUserName("user1");
        form.setEmail("test@solxyz.co.jp");
        form.setPassword("password");
        form.setIsAdmin(false);
    }

    @Test
    @DisplayName("ユーザー情報の追加が成功する場合")
    void shouldAddUserSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);
        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.addUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).addUser(expectedForm);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("ユーザー情報の追加時に1件バリデーションエラーが発生する場合、1件のエラーメッセージが返却される")
    void shouldFailToAddUserDueToValidationError() {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("email", "メールアドレスが不正です。"));

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.addUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(0)).addUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("メールアドレスが不正です。");
    }

    @Test
    @DisplayName("ユーザー情報の追加時に2件バリデーションエラーが発生する場合、2件のエラーメッセージが返却される")
    void shouldFailToAddUserDueToMultipleValidationErrors() {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("email", "メールアドレスが不正です。"));
        errors.add(new ObjectError("password", "パスワードが不正です。"));

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.addUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(0)).addUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("メールアドレスが不正です。");
        assertThat(response.getBody().get(1).getDefaultMessage()).isEqualTo("パスワードが不正です。");
    }

    @Test
    @DisplayName("ユーザー情報の追加が失敗する場合、JpaSystemExceptionが発生しエラーメッセージが返される")
    void shouldFailToAddUserDueToJpaSystemException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(JpaSystemException.class).when(service).addUser(form);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.addUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).addUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("既に登録されているメールアドレスです。");
    }

    @Test
    @DisplayName("ユーザー情報の追加が失敗する場合、DataAccessExceptionのサブクラスであるDataAccessResourceFailureExceptionが発生しエラーメッセージが返される")
    void shouldFailToAddUserDueToDataAccessException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(DataAccessResourceFailureException.class).when(service).addUser(form);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.addUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).addUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("ユーザ情報の登録に失敗しました。");
    }

    @Test
    @DisplayName("ユーザー情報の更新が成功する場合")
    void shouldUpdateUserSuccessfully() {
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.updateUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).updateUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    @DisplayName("ユーザー情報の更新時に1件バリデーションエラーが発生する場合、1件のエラーメッセージが返却される")
    void shouldFailToUpdateUserDueToValidationError() {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("email", "メールアドレスが不正です。"));

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.updateUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(0)).updateUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("メールアドレスが不正です。");
    }

    @Test
    @DisplayName("ユーザー情報の更新時に2件バリデーションエラーが発生する場合、2件のエラーメッセージが返却される")
    void shouldFailToUpdateUserDueToMultipleValidationErrors() {
        List<ObjectError> errors = new ArrayList<>();
        errors.add(new ObjectError("email", "メールアドレスが不正です。"));
        errors.add(new ObjectError("password", "パスワードが不正です。"));

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.updateUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(0)).updateUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("メールアドレスが不正です。");
        assertThat(response.getBody().get(1).getDefaultMessage()).isEqualTo("パスワードが不正です。");
    }

    @Test
    @DisplayName("ユーザー情報の更新が失敗する場合、NoSuchElementExceptionが発生しエラーメッセージが返される")
    void shouldFailToUpdateUserDueToNoSuchElementException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new NoSuchElementException("ユーザがみつかりません。")).when(service).updateUser(form);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.updateUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).updateUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("ユーザがみつかりません。");
    }

    @Test
    @DisplayName("ユーザー情報の更新が失敗する場合、OptimisticLockingFailureExceptionが発生しエラーメッセージが返される")
    void shouldFailToUpdateUserDueToOptimisticLockingFailureException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new OptimisticLockingFailureException("他の管理者によって更新されました。")).when(service).updateUser(form);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.updateUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).updateUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("他の管理者によって更新されました。");
    }

    @Test
    @DisplayName("ユーザー情報の更新が失敗する場合、JpaSystemExceptionが発生しエラーメッセージが返される")
    void shouldFailToUpdateUserDueToJpaSystemException() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(JpaSystemException.class).when(service).updateUser(form);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.updateUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).updateUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("既に登録されているメールアドレスです。");
    }

    @Test
    @DisplayName("ユーザー情報の更新が失敗する場合、DataAccessExceptionのサブクラスであるDataAccessResourceFailureExceptionが発生しエラーメッセージが返される")
    void shouldFailToUpdateUserDueToDataAccessException() {
        AccountProfile form = new AccountProfile();
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(DataAccessResourceFailureException.class).when(service).updateUser(form);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.updateUser(form, bindingResult);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).updateUser(form);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("ユーザ情報の更新に失敗しました。");
    }

    @Test
    @DisplayName("ユーザー情報の削除が成功する場合")
    void shouldDeleteUserSuccessfully() {
        AccountProfile accountProfile = new AccountProfile();
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.deleteUser(accountProfile.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).deleteUser(accountProfile.getUserId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("ユーザー情報の削除時にバリデーションエラーが発生する場合、エラーメッセージが返却される")
    void shouldFailToDeleteUserDueToValidationError() throws Exception {
        String invalidUserId = "invalidUserId";

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/management/user/" + invalidUserId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("ユーザー情報の削除が失敗する場合、NoSuchElementExceptionが発生しエラーメッセージが返される")
    void shouldFailToDeleteUserDueToNoSuchElementException() {
        String expectedUserId = "123e4567-e89b-12d3-a456-426614174000";
        String userId = "123e4567-e89b-12d3-a456-426614174000";

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new NoSuchElementException("ユーザが見つかりません。")).when(service).deleteUser(userId);

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.deleteUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).deleteUser(expectedUserId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("ユーザが見つかりません。");
    }

    @Test
    @DisplayName("ユーザー情報の削除が失敗する場合、IllegalStateExceptionが発生しエラーメッセージが返される")
    void shouldFailToDeleteUserDueToIllegalStateException() {
        AccountProfile expectedUserId = new AccountProfile();
        expectedUserId.setUserId("123e4567-e89b-12d3-a456-426614174000");

        AccountProfile accountProfile = new AccountProfile();
        accountProfile.setUserId("123e4567-e89b-12d3-a456-426614174000");

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalStateException("削除に失敗しました。")).when(service).deleteUser(accountProfile.getUserId());

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.deleteUser(accountProfile.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).deleteUser(expectedUserId.getUserId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("削除に失敗しました。");

    }

    @Test
    @DisplayName("ユーザー情報の削除が失敗する場合、DataAccessExceptionのサブクラスであるDataAccessResourceFailureExceptionが発生しエラーメッセージが返される")
    void shouldFailToDeleteUserDueToDataAccessException() {
        AccountProfile expectedUserId = new AccountProfile();
        expectedUserId.setUserId("123e4567-e89b-12d3-a456-426614174000");

        AccountProfile accountProfile = new AccountProfile();
        accountProfile.setUserId("123e4567-e89b-12d3-a456-426614174000");

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(DataAccessResourceFailureException.class).when(service).deleteUser(accountProfile.getUserId());

        ResponseEntity<List<ObjectError>> response = null;
        try {
            response = controller.deleteUser(accountProfile.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(service, times(1)).deleteUser(expectedUserId.getUserId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get(0).getDefaultMessage()).isEqualTo("ユーザ情報の削除に失敗しました。");
    }
}
