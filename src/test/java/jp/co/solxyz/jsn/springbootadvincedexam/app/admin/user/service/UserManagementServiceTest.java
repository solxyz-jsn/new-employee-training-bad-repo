package jp.co.solxyz.jsn.springbootadvincedexam.app.user.service;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.dto.UserAccountDto;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.json.UserManagementForm;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.model.UserManagementModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.util.UUIDGenerator;
import jp.co.solxyz.jsn.springbootadvincedexam.component.user.UserAccountManager;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserManagementServiceTest {

    @InjectMocks
    UserManagementService service;

    @Mock
    UserAccountManager userServicesBizlogic;

    private final Instant TEST_TIME = Instant.parse("2020-01-01T00:00:00Z");

    private final String TEST_UUID = "123e4567-e89b-12d3-a456-426614174000";

    private final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";

    private MockedStatic<Instant> instantMockedStatic;

    private MockedStatic<UUIDGenerator> uuidGeneratorMockedStatic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        instantMockedStatic = Mockito.mockStatic(Instant.class);
        instantMockedStatic.when(Instant::now).thenReturn(TEST_TIME);
        uuidGeneratorMockedStatic = Mockito.mockStatic(UUIDGenerator.class);
        uuidGeneratorMockedStatic.when(UUIDGenerator::generateUserId).thenReturn(TEST_UUID);
    }

    @AfterEach
    void tearDown() {
        instantMockedStatic.close();
        uuidGeneratorMockedStatic.close();
    }

    @Test
    @DisplayName("複数のユーザ情報がある場合、全てのユーザ情報を取得できる")
    void shouldGetAllUsersSuccessfully() {
        UserManagementModel expectedUser1 = new UserManagementModel();
        expectedUser1.setUserId("123e4567-e89b-12d3-a456-426614174000");
        expectedUser1.setUserName("user1");
        expectedUser1.setEmail("test@solxyz.co.jp");
        expectedUser1.setAdmin(false);
        UserManagementModel expectedUser2 = new UserManagementModel();
        expectedUser2.setUserId("123e4567-e89b-12d3-a456-426614174001");
        expectedUser2.setUserName("user2");
        expectedUser2.setEmail("test@solxyz.co.jp");
        expectedUser2.setAdmin(true);
        List<UserManagementModel> expected = Arrays.asList(expectedUser1, expectedUser2);

        UserAccountDto user1 = new UserAccountDto();
        user1.setUserId("123e4567-e89b-12d3-a456-426614174000");
        user1.setUsername("user1");
        user1.setEmail("test@solxyz.co.jp");
        user1.setIsAdmin(false);
        user1.setUpdatedAt(TEST_TIME);
        UserAccountDto user2 = new UserAccountDto();
        user2.setUserId("123e4567-e89b-12d3-a456-426614174001");
        user2.setUsername("user2");
        user2.setEmail("test@solxyz.co.jp");
        user2.setIsAdmin(true);
        user2.setUpdatedAt(TEST_TIME);
        List<UserAccountDto> users = Arrays.asList(user1, user2);

        when(userServicesBizlogic.getAllUsersWithoutPassword()).thenReturn(users);

        List<UserManagementModel> result = service.getAllUsers();

        verify(userServicesBizlogic, times(1)).getAllUsersWithoutPassword();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("ユーザ情報が1件の場合、1件のユーザ情報を取得できる")
    void shouldGetOneUserSuccessfully() {
        UserManagementModel expected = new UserManagementModel();
        expected.setUserId("123e4567-e89b-12d3-a456-426614174000");
        expected.setUserName("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setAdmin(true);
        List<UserManagementModel> expectedList = List.of(expected);

        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(TEST_UUID);
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);
        userAccountDto.setUpdatedAt(TEST_TIME);

        List<UserAccountDto> users = List.of(userAccountDto);

        when(userServicesBizlogic.getAllUsersWithoutPassword()).thenReturn(users);

        List<UserManagementModel> result = service.getAllUsers();

        verify(userServicesBizlogic, times(1)).getAllUsersWithoutPassword();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).isEqualTo(expectedList);
    }

    @Test
    @DisplayName("ユーザ情報がない場合、空のリストを返す")
    void shouldReturnEmptyListIfNoUsers() {
        List<UserAccountDto> users = List.of();
        List<UserManagementModel> expected = List.of();

        when(userServicesBizlogic.getAllUsersWithoutPassword()).thenReturn(users);

        List<UserManagementModel> result = service.getAllUsers();

        verify(userServicesBizlogic, times(1)).getAllUsersWithoutPassword();
        assertThat(result.size()).isEqualTo(0);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("新規ユーザーの追加が正常に動作する")
    void shouldAddUserSuccessfully() {
        UserAccount expected = new UserAccount();
        expected.setUserId(TEST_UUID);
        expected.setIsAdmin(true);
        expected.setEmail("test@solxyz.co.jp");
        expected.setUsername("testUsername");
        expected.setPassword("testPassword");
        expected.setUpdatedAt(TEST_TIME);

        UserAccount userAccountWithPassword = new UserAccount();
        userAccountWithPassword.setUserId(TEST_UUID);
        userAccountWithPassword.setIsAdmin(true);
        userAccountWithPassword.setEmail("test@solxyz.co.jp");
        userAccountWithPassword.setUsername("testUsername");
        userAccountWithPassword.setPassword("testPassword");
        userAccountWithPassword.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithPassword = new UserManagementForm();
        userManagementFormWithPassword.setUserId(TEST_UUID);
        userManagementFormWithPassword.setIsAdmin(true);
        userManagementFormWithPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithPassword.setUserName("testUsername");
        userManagementFormWithPassword.setPassword("testPassword");

        doNothing().when(userServicesBizlogic).addUser(userAccountWithPassword);

        try {
            service.addUser(userManagementFormWithPassword);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(userServicesBizlogic, times(1)).addUser(expected);
    }

    @Test
    @DisplayName("新規ユーザの追加時に、JpaSystemExceptionが発生した場合、例外をスローする")
    void shouldThrowExceptionIfJpaSystemExceptionOccursWhenAddingUser() {
        UserAccount userAccountWithPassword = new UserAccount();
        userAccountWithPassword.setUserId(TEST_UUID);
        userAccountWithPassword.setIsAdmin(true);
        userAccountWithPassword.setEmail("test@solxyz.co.jp");
        userAccountWithPassword.setUsername("testUsername");
        userAccountWithPassword.setPassword("testPassword");
        userAccountWithPassword.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithPassword = new UserManagementForm();
        userManagementFormWithPassword.setUserId(TEST_UUID);
        userManagementFormWithPassword.setIsAdmin(true);
        userManagementFormWithPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithPassword.setUserName("testUsername");
        userManagementFormWithPassword.setPassword("testPassword");

        doNothing().when(userServicesBizlogic).addUser(userAccountWithPassword);
        doThrow(JpaSystemException.class).when(userServicesBizlogic).addUser(userAccountWithPassword);

        try {
            service.addUser(userManagementFormWithPassword);
            fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(JpaSystemException.class);
            verify(userServicesBizlogic, times(1)).addUser(userAccountWithPassword);
        }
    }

    @Test
    @DisplayName("新規ユーザの追加時に、DataAccessExceptionが発生した場合、例外をスローする")
    void shouldThrowExceptionIfDataAccessExceptionOccursWhenAddingUser() {
        UserAccount userAccountWithPassword = new UserAccount();
        userAccountWithPassword.setUserId(TEST_UUID);
        userAccountWithPassword.setIsAdmin(true);
        userAccountWithPassword.setEmail("test@solxyz.co.jp");
        userAccountWithPassword.setUsername("testUsername");
        userAccountWithPassword.setPassword("testPassword");
        userAccountWithPassword.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithPassword = new UserManagementForm();
        userManagementFormWithPassword.setUserId(TEST_UUID);
        userManagementFormWithPassword.setIsAdmin(true);
        userManagementFormWithPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithPassword.setUserName("testUsername");
        userManagementFormWithPassword.setPassword("testPassword");

        doNothing().when(userServicesBizlogic).addUser(userAccountWithPassword);
        doThrow(JpaSystemException.class).when(userServicesBizlogic).addUser(userAccountWithPassword);

        try {
            service.addUser(userManagementFormWithPassword);
            fail();
        } catch (JpaSystemException e) {
            verify(userServicesBizlogic, times(1)).addUser(userAccountWithPassword);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("パスワードを含めたユーザ情報の更新が正常に動作する")
    void shouldUpdateUserSuccessfullyWithPassword() {
        UserAccount expectedUserAccountWithPassword = new UserAccount();
        expectedUserAccountWithPassword.setUserId(TEST_UUID);
        expectedUserAccountWithPassword.setIsAdmin(true);
        expectedUserAccountWithPassword.setEmail("test@solxyz.co.jp");
        expectedUserAccountWithPassword.setUsername("testUsername");
        expectedUserAccountWithPassword.setPassword("testPassword");
        expectedUserAccountWithPassword.setUpdatedAt(TEST_TIME);

        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(TEST_UUID);
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);
        userAccountDto.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithPassword = new UserManagementForm();
        userManagementFormWithPassword.setUserId(TEST_UUID);
        userManagementFormWithPassword.setIsAdmin(true);
        userManagementFormWithPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithPassword.setUserName("testUsername");
        userManagementFormWithPassword.setPassword("testPassword");

        when(userServicesBizlogic.findByIdWithoutPassword(userManagementFormWithPassword.getUserId())).thenReturn(userAccountDto);

        try {
            service.updateUser(userManagementFormWithPassword);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(userServicesBizlogic, times(1)).updateUserWithPassword(expectedUserAccountWithPassword, TEST_TIME);
    }

    @Test
    @DisplayName("パスワードを含めたユーザの更新に失敗した場合、JPASystemExceptionがスローされる")
    void shouldThrowJpaSystemExceptionIfUpdateUserWithPasswordFails() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(TEST_UUID);
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);
        userAccountDto.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithPassword = new UserManagementForm();
        userManagementFormWithPassword.setUserId(TEST_UUID);
        userManagementFormWithPassword.setIsAdmin(true);
        userManagementFormWithPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithPassword.setUserName("testUsername");
        userManagementFormWithPassword.setPassword("testPassword");

        when(userServicesBizlogic.findByIdWithoutPassword(userManagementFormWithPassword.getUserId())).thenReturn(userAccountDto);
        doThrow(JpaSystemException.class).when(userServicesBizlogic).updateUserWithPassword(any(UserAccount.class), any(Instant.class));

        try {
            service.updateUser(userManagementFormWithPassword);
            fail();
        } catch (JpaSystemException e) {
            verify(userServicesBizlogic, times(1)).findByIdWithoutPassword(userManagementFormWithPassword.getUserId());
            verify(userServicesBizlogic, times(1)).updateUserWithPassword(any(UserAccount.class), any(Instant.class));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("パスワードを含めたユーザの更新に失敗した場合、DataAccessExceptionのサブクラスがスローされる")
    void shouldThrowDataAccessExceptionIfUpdateUserWithPasswordFails() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(TEST_UUID);
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);
        userAccountDto.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithPassword = new UserManagementForm();
        userManagementFormWithPassword.setUserId(TEST_UUID);
        userManagementFormWithPassword.setIsAdmin(true);
        userManagementFormWithPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithPassword.setUserName("testUsername");
        userManagementFormWithPassword.setPassword("testPassword");

        when(userServicesBizlogic.findByIdWithoutPassword(userManagementFormWithPassword.getUserId())).thenReturn(userAccountDto);
        doThrow(DataIntegrityViolationException.class).when(userServicesBizlogic).updateUserWithPassword(any(UserAccount.class), any(Instant.class));

        try {
            service.updateUser(userManagementFormWithPassword);
            fail();
        } catch (DataAccessException e) {
            verify(userServicesBizlogic, times(1)).findByIdWithoutPassword(userManagementFormWithPassword.getUserId());
            verify(userServicesBizlogic, times(1)).updateUserWithPassword(any(UserAccount.class), any(Instant.class));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("パスワードを含めたユーザの更新に失敗した場合、OptimisticLockingFailureExceptionがスローされる")
    void shouldThrowOptimisticLockingFailureExceptionIfUpdateUserWithPasswordFails() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(TEST_UUID);
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);
        userAccountDto.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithPassword = new UserManagementForm();
        userManagementFormWithPassword.setUserId(TEST_UUID);
        userManagementFormWithPassword.setIsAdmin(true);
        userManagementFormWithPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithPassword.setUserName("testUsername");
        userManagementFormWithPassword.setPassword("testPassword");

        when(userServicesBizlogic.findByIdWithoutPassword(userManagementFormWithPassword.getUserId())).thenReturn(userAccountDto);
        doThrow(OptimisticLockingFailureException.class).when(userServicesBizlogic).updateUserWithPassword(any(UserAccount.class), any(Instant.class));

        try {
            service.updateUser(userManagementFormWithPassword);
            fail();
        } catch (OptimisticLockingFailureException e) {
            verify(userServicesBizlogic, times(1)).findByIdWithoutPassword(userManagementFormWithPassword.getUserId());
            verify(userServicesBizlogic, times(1)).updateUserWithPassword(any(UserAccount.class), any(Instant.class));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("パスワードのないユーザー情報の更新が正常に動作する")
    void shouldUpdateUserSuccessfullyWithoutPassword() {
        UserAccount expectedUserAccountWithoutPassword = new UserAccount();
        expectedUserAccountWithoutPassword.setUserId(TEST_UUID);
        expectedUserAccountWithoutPassword.setIsAdmin(true);
        expectedUserAccountWithoutPassword.setEmail("test@solxyz.co.jp");
        expectedUserAccountWithoutPassword.setUsername("testUsername");
        expectedUserAccountWithoutPassword.setPassword("");
        expectedUserAccountWithoutPassword.setUpdatedAt(TEST_TIME);

        UserAccount userAccountWithoutPassword = new UserAccount();
        userAccountWithoutPassword.setUserId(TEST_UUID);
        userAccountWithoutPassword.setIsAdmin(true);
        userAccountWithoutPassword.setEmail("test@solxyz.co.jp");
        userAccountWithoutPassword.setUsername("testUsername");
        userAccountWithoutPassword.setPassword("");
        userAccountWithoutPassword.setUpdatedAt(TEST_TIME);

        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(TEST_UUID);
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);
        userAccountDto.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithoutPassword = new UserManagementForm();
        userManagementFormWithoutPassword.setUserId(TEST_UUID);
        userManagementFormWithoutPassword.setIsAdmin(true);
        userManagementFormWithoutPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithoutPassword.setUserName("testUsername");
        userManagementFormWithoutPassword.setPassword("");

        when(userServicesBizlogic.findByIdWithoutPassword(userManagementFormWithoutPassword.getUserId())).thenReturn(userAccountDto);
        doNothing().when(userServicesBizlogic).updateUserWithoutPassword(userAccountWithoutPassword, TEST_TIME);

        try {
            service.updateUser(userManagementFormWithoutPassword);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(userServicesBizlogic, times(1)).updateUserWithoutPassword(expectedUserAccountWithoutPassword, TEST_TIME);
    }

    @Test
    @DisplayName("パスワードのないユーザの更新に失敗した場合、JPASystemExceptionがスローされる")
    void shouldThrowJpaSystemExceptionIfUpdateUserWithoutPasswordFails() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(TEST_UUID);
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);
        userAccountDto.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithoutPassword = new UserManagementForm();
        userManagementFormWithoutPassword.setUserId(TEST_UUID);
        userManagementFormWithoutPassword.setIsAdmin(true);
        userManagementFormWithoutPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithoutPassword.setUserName("testUsername");
        userManagementFormWithoutPassword.setPassword("");

        when(userServicesBizlogic.findByIdWithoutPassword(userManagementFormWithoutPassword.getUserId())).thenReturn(userAccountDto);
        doThrow(JpaSystemException.class).when(userServicesBizlogic).updateUserWithoutPassword(any(UserAccount.class), any(Instant.class));

        try {
            service.updateUser(userManagementFormWithoutPassword);
            fail();
        } catch (JpaSystemException e) {
            verify(userServicesBizlogic, times(1)).findByIdWithoutPassword(userManagementFormWithoutPassword.getUserId());
            verify(userServicesBizlogic, times(1)).updateUserWithoutPassword(any(UserAccount.class), any(Instant.class));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("パスワードのないユーザの更新に失敗した場合、DataAccessExceptionのサブクラスがスローされる")
    void shouldThrowDataAccessExceptionIfUpdateUserWithoutPasswordFails() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(TEST_UUID);
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);
        userAccountDto.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithoutPassword = new UserManagementForm();
        userManagementFormWithoutPassword.setUserId(TEST_UUID);
        userManagementFormWithoutPassword.setIsAdmin(true);
        userManagementFormWithoutPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithoutPassword.setUserName("testUsername");
        userManagementFormWithoutPassword.setPassword("");

        when(userServicesBizlogic.findByIdWithoutPassword(userManagementFormWithoutPassword.getUserId())).thenReturn(userAccountDto);
        doThrow(DataIntegrityViolationException.class).when(userServicesBizlogic).updateUserWithoutPassword(any(UserAccount.class), any(Instant.class));

        try {
            service.updateUser(userManagementFormWithoutPassword);
            fail();
        } catch (DataAccessException e) {
            verify(userServicesBizlogic, times(1)).findByIdWithoutPassword(userManagementFormWithoutPassword.getUserId());
            verify(userServicesBizlogic, times(1)).updateUserWithoutPassword(any(UserAccount.class), any(Instant.class));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("パスワードのないユーザの更新に失敗した場合、OptimisticLockingFailureExceptionがスローされる")
    void shouldThrowOptimisticLockingFailureExceptionIfUpdateUserWithoutPasswordFails() {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserId(TEST_UUID);
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);
        userAccountDto.setUpdatedAt(TEST_TIME);

        UserManagementForm userManagementFormWithoutPassword = new UserManagementForm();
        userManagementFormWithoutPassword.setUserId(TEST_UUID);
        userManagementFormWithoutPassword.setIsAdmin(true);
        userManagementFormWithoutPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithoutPassword.setUserName("testUsername");
        userManagementFormWithoutPassword.setPassword("");

        when(userServicesBizlogic.findByIdWithoutPassword(userManagementFormWithoutPassword.getUserId())).thenReturn(userAccountDto);
        doThrow(OptimisticLockingFailureException.class).when(userServicesBizlogic).updateUserWithoutPassword(any(UserAccount.class), any(Instant.class));

        try {
            service.updateUser(userManagementFormWithoutPassword);
            fail();
        } catch (OptimisticLockingFailureException e) {
            verify(userServicesBizlogic, times(1)).findByIdWithoutPassword(userManagementFormWithoutPassword.getUserId());
            verify(userServicesBizlogic, times(1)).updateUserWithoutPassword(any(UserAccount.class), any(Instant.class));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("更新対象のユーザ情報が存在しない場合、NoSuchElementExceptionをスローする")
    void shouldThrowNoSuchElementExceptionIfUserToUpdateDoesNotExist() {
        UserManagementForm userManagementFormWithoutPassword = new UserManagementForm();
        userManagementFormWithoutPassword.setUserId(TEST_UUID);
        userManagementFormWithoutPassword.setIsAdmin(true);
        userManagementFormWithoutPassword.setEmail("test@solxyz.co.jp");
        userManagementFormWithoutPassword.setUserName("testUsername");
        userManagementFormWithoutPassword.setPassword("");

        doThrow(NoSuchElementException.class).when(userServicesBizlogic).findByIdWithoutPassword(userManagementFormWithoutPassword.getUserId());

        try {
            service.updateUser(userManagementFormWithoutPassword);
            fail();
        } catch (NoSuchElementException e) {
            verify(userServicesBizlogic, times(1)).findByIdWithoutPassword(userManagementFormWithoutPassword.getUserId());
            verify(userServicesBizlogic, never()).updateUserWithPassword(any(), any());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("ユーザー情報の削除が正常に動作する")
    void shouldDeleteUserSuccessfully() {
        try {
            service.deleteUser(USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(userServicesBizlogic, times(1)).deleteUser(USER_ID);
    }

    @Test
    @DisplayName("削除対象が存在しない場合、NoSuchElementExceptionをスローする")
    void shouldThrowNoSuchElementExceptionIfUserToDeleteDoesNotExist() {
        doThrow(NoSuchElementException.class).when(userServicesBizlogic).deleteUser(USER_ID);

        try {
            service.deleteUser(USER_ID);
            fail();
        } catch (NoSuchElementException e) {
            verify(userServicesBizlogic, times(1)).deleteUser(USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("削除対象のユーザ情報に未返却の書籍がある場合、IllegalStateExceptionをスローする")
    void shouldThrowIllegalStateExceptionIfUserToDeleteHasUnreturnedBooks() {
        doThrow(IllegalStateException.class).when(userServicesBizlogic).deleteUser(USER_ID);

        try {
            service.deleteUser(USER_ID);
            fail();
        } catch (IllegalStateException e) {
            verify(userServicesBizlogic, times(1)).deleteUser(USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @DisplayName("ユーザー情報の削除に失敗した場合、DataAccessExceptionのサブクラスをスローする")
    void shouldThrowDataAccessExceptionIfDeleteUserFails() {
        doThrow(DataIntegrityViolationException.class).when(userServicesBizlogic).deleteUser(USER_ID);

        try {
            service.deleteUser(USER_ID);
            fail();
        } catch (DataAccessException e) {
            verify(userServicesBizlogic, times(1)).deleteUser(USER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
