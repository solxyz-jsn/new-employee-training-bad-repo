package jp.co.solxyz.jsn.springbootadvincedexam.component.user;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.BookCheckoutHistory;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookCheckoutHistoryRepository;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.user.UserAccountRepository;
import jp.co.solxyz.jsn.springbootadvincedexam.util.PasswordUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAccountManagerTest {

    @InjectMocks
    UserAccountManager userAccountManager;

    @Mock
    UserAccountRepository userAccountRepository;

    @Mock
    BookCheckoutHistoryRepository bookCheckoutHistoryRepository;

    @Mock
    PasswordUtility passwordUtility;

    private final Instant TEST_TIME = Instant.parse("2020-01-01T00:00:00Z");

    private final Instant EXPECTED_TEST_TIME = Instant.parse("2020-01-01T00:00:00Z");

    private final Instant OPTIMISTIC_LOCK_UPDATED_AT = Instant.parse("2020-01-01T00:00:01Z");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ユーザ情報が存在しない場合、空のリストが返る")
    void shouldGetAllUsersWithoutPasswordWithNoUser() {
        when(userAccountRepository.findAll()).thenReturn(List.of());

        List<UserAccount> result = userAccountManager.getAllUsersWithoutPassword();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("ユーザ情報が１件ある場合、１件のユーザ情報が含まれたリストが返る")
    void shouldGetAllUsersWithoutPasswordWithOneUser() {
        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);

        UserAccount userAccountDto = new UserAccount();
        userAccountDto.setUserId("testUserId");
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);

        when(userAccountRepository.findAll()).thenReturn(List.of(userAccountDto));

        List<UserAccount> result = userAccountManager.getAllUsersWithoutPassword();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(expected);
    }

    @Test
    @DisplayName("ユーザ情報が複数ある場合、複数のユーザ情報が含まれたリストが返る")
    void shouldGetAllUsersWithoutPasswordSuccessfully() {
        UserAccount expected1 = new UserAccount();
        expected1.setUserId("testUserId1");
        expected1.setUsername("testUsername1");
        expected1.setEmail("test@solxyz.co.jp");
        expected1.setIsAdmin(true);
        UserAccount expected2 = new UserAccount();
        expected2.setUserId("testUserId2");
        expected2.setUsername("testUsername2");
        expected2.setEmail("test2@solxyz.co.jp");
        expected2.setIsAdmin(false);

        UserAccount userAccountDto1 = new UserAccount();
        userAccountDto1.setUserId("testUserId1");
        userAccountDto1.setUsername("testUsername1");
        userAccountDto1.setEmail("test@solxyz.co.jp");
        userAccountDto1.setIsAdmin(true);
        UserAccount userAccountDto2 = new UserAccount();
        userAccountDto2.setUserId("testUserId2");
        userAccountDto2.setUsername("testUsername2");
        userAccountDto2.setEmail("test2@solxyz.co.jp");
        userAccountDto2.setIsAdmin(false);

        when(userAccountRepository.findAll()).thenReturn(List.of(userAccountDto1, userAccountDto2));

        List<UserAccount> result = userAccountManager.getAllUsersWithoutPassword();

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(expected1);
        assertThat(result.get(1)).isEqualTo(expected2);
    }

    @Test
    @DisplayName("ユーザIDによりパスワードを除いたユーザ情報が取得できない場合、NoSuchElementExceptionが発生する")
    void shouldNotFindByIdWithoutPassword() {
        String nonexistentUserId = "nonexistentUserId";

        when(userAccountRepository.findById(nonexistentUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountManager.findByIdWithoutPassword(nonexistentUserId))
                .isInstanceOf(NoSuchElementException.class);
        verify(userAccountRepository, times(1)).findById(nonexistentUserId);
    }

    @Test
    @DisplayName("ユーザIDによりパスワードを除いたユーザ情報を1件取得する")
    void shouldFindByIdWithoutPasswordSuccessfully() {
        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);

        UserAccount userAccountDto = new UserAccount();
        userAccountDto.setUserId("testUserId");
        userAccountDto.setUsername("testUsername");
        userAccountDto.setEmail("test@solxyz.co.jp");
        userAccountDto.setIsAdmin(true);

        when(userAccountRepository.findById(userAccountDto.getUserId())).thenReturn(Optional.of(userAccountDto));

        UserAccount result = userAccountManager.findByIdWithoutPassword(userAccountDto.getUserId());

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("存在しないユーザーIDでユーザー情報の取得した場合、NoSuchElementExceptionが発生する")
    void shouldThrowNoSuchElementExceptionWhenFindByIdWithoutPasswordWithNonexistentUserId() {
        String nonexistentUserId = "nonexistentUserId";

        when(userAccountRepository.findById(nonexistentUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountManager.findByIdWithoutPassword(nonexistentUserId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("新規ユーザーの追加が正常に終了する")
    void shouldAddUserSuccessfully() {
        String rowPassword = "rawPassword";
        String hashedPassword = "hashedPassword";

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setPassword(rowPassword);

        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);
        expected.setPassword(hashedPassword);

        UserAccount hashedPasswordUserAccount = new UserAccount();
        hashedPasswordUserAccount.setUserId("testUserId");
        hashedPasswordUserAccount.setUsername("testUsername");
        hashedPasswordUserAccount.setEmail("test@solxyz.co.jp");
        hashedPasswordUserAccount.setIsAdmin(true);
        hashedPasswordUserAccount.setPassword(hashedPassword);

        when(passwordUtility.hashPassword(userAccount.getPassword())).thenReturn(hashedPassword);
        when(userAccountRepository.save(hashedPasswordUserAccount)).thenReturn(new UserAccount());

        try {
            userAccountManager.addUser(userAccount);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(passwordUtility, times(1)).hashPassword(rowPassword);
        verify(userAccountRepository, times(1)).save(expected);
    }

    @Test
    @DisplayName("新規ユーザーの追加で一意制約のある項目が重複しているとき、JpaSystemExceptionが発生する")
    void shouldThrowJpaSystemExceptionWhenAddUserWithDuplicatedUniqueField() {
        String rowPassword = "rawPassword";
        String hashedPassword = "hashedPassword";

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setPassword(rowPassword);

        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);
        expected.setPassword(hashedPassword);

        UserAccount hashedPasswordUserAccount = new UserAccount();
        hashedPasswordUserAccount.setUserId("testUserId");
        hashedPasswordUserAccount.setUsername("testUsername");
        hashedPasswordUserAccount.setEmail("test@solxyz.co.jp");
        hashedPasswordUserAccount.setIsAdmin(true);
        hashedPasswordUserAccount.setPassword(hashedPassword);

        when(passwordUtility.hashPassword(userAccount.getPassword())).thenReturn(hashedPassword);
        when(userAccountRepository.save(hashedPasswordUserAccount)).thenThrow(JpaSystemException.class);

        assertThatThrownBy(() -> userAccountManager.addUser(userAccount))
                .isInstanceOf(JpaSystemException.class);
        verify(passwordUtility, times(1)).hashPassword(rowPassword);
        verify(userAccountRepository, times(1)).save(expected);
    }

    @Test
    @DisplayName("新規ユーザーの追加でDBへの接続ができないとき、DataAccessExceptionのサブクラスであるDataAccessResourceFailureExceptionが発生する")
    void shouldThrowDataAccessExceptionWhenAddUserWithFailedDbConnection() {
        String rowPassword = "rawPassword";
        String hashedPassword = "hashedPassword";

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setPassword(rowPassword);

        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);
        expected.setPassword(hashedPassword);

        UserAccount hashedPasswordUserAccount = new UserAccount();
        hashedPasswordUserAccount.setUserId("testUserId");
        hashedPasswordUserAccount.setUsername("testUsername");
        hashedPasswordUserAccount.setEmail("test@solxyz.co.jp");
        hashedPasswordUserAccount.setIsAdmin(true);
        hashedPasswordUserAccount.setPassword(hashedPassword);

        when(passwordUtility.hashPassword(userAccount.getPassword())).thenReturn(hashedPassword);
        when(userAccountRepository.save(hashedPasswordUserAccount)).thenThrow(DataAccessResourceFailureException.class);

        assertThatThrownBy(() -> userAccountManager.addUser(userAccount))
                .isInstanceOf(DataAccessException.class);
        verify(passwordUtility, times(1)).hashPassword(rowPassword);
        verify(userAccountRepository, times(1)).save(expected);
    }

    @Test
    @DisplayName("パスワードを含めたユーザ情報の更新が正常に終了する")
    void shouldUpdateUserWithPassword() {
        String hashedPassword = "hashedPassword";

        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);
        expected.setPassword(hashedPassword);
        expected.setUpdatedAt(TEST_TIME);

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setPassword("password");
        userAccount.setUpdatedAt(TEST_TIME);

        when(passwordUtility.hashPassword(userAccount.getPassword())).thenReturn(hashedPassword);
        when(userAccountRepository.updateWithPassword(userAccount.getUserId(), userAccount.getIsAdmin(), userAccount.getEmail(),
                userAccount.getUsername(), hashedPassword, EXPECTED_TEST_TIME,
                EXPECTED_TEST_TIME)).thenReturn(1);

        try {
            userAccountManager.updateUserWithPassword(userAccount, Instant.parse("2020-01-01T00:00:00Z"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(userAccountRepository, times(1)).updateWithPassword(expected.getUserId(), expected.getIsAdmin(),
                expected.getEmail(), expected.getUsername(), expected.getPassword(), EXPECTED_TEST_TIME,
                EXPECTED_TEST_TIME);
    }

    @Test
    @DisplayName("パスワードを含めたユーザ情報を更新する際、一意制約のあるデータが重複しているとき、JpaSystemExceptionが発生する")
    void shouldThrowJpaSystemExceptionWhenUpdateUserWithPasswordWithDuplicatedUniqueField() {
        String hashedPassword = "hashedPassword";

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setPassword("password");
        userAccount.setUpdatedAt(TEST_TIME);

        when(passwordUtility.hashPassword(userAccount.getPassword())).thenReturn(hashedPassword);
        when(userAccountRepository.updateWithPassword(userAccount.getUserId(), userAccount.getIsAdmin(), userAccount.getEmail(),
                userAccount.getUsername(), hashedPassword, TEST_TIME, TEST_TIME))
                .thenThrow(JpaSystemException.class);

        assertThatThrownBy(() -> userAccountManager.updateUserWithPassword(userAccount, TEST_TIME))
                .isInstanceOf(JpaSystemException.class);

        verify(userAccountRepository, times(1)).updateWithPassword(userAccount.getUserId(), userAccount.getIsAdmin(),
                userAccount.getEmail(), userAccount.getUsername(), hashedPassword, TEST_TIME, TEST_TIME);
    }

    @Test
    @DisplayName("パスワードを含めたユーザ情報を更新する際、DBへの接続ができないとき、DataAccessExceptionのサブクラスであるDataAccessResourceFailureExceptionが発生する")
    void shouldThrowDataAccessExceptionWhenUpdateUserWithPasswordWithFailedDbConnection() {
        String hashedPassword = "hashedPassword";

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setPassword("password");
        userAccount.setUpdatedAt(TEST_TIME);

        when(passwordUtility.hashPassword(userAccount.getPassword())).thenReturn(hashedPassword);
        when(userAccountRepository.updateWithPassword(userAccount.getUserId(), userAccount.getIsAdmin(), userAccount.getEmail(),
                userAccount.getUsername(), hashedPassword, TEST_TIME, TEST_TIME))
                .thenThrow(DataAccessResourceFailureException.class);

        assertThatThrownBy(() -> userAccountManager.updateUserWithPassword(userAccount, TEST_TIME))
                .isInstanceOf(DataAccessException.class);
        verify(userAccountRepository, times(1)).updateWithPassword(userAccount.getUserId(), userAccount.getIsAdmin(),
                userAccount.getEmail(), userAccount.getUsername(), hashedPassword, TEST_TIME, TEST_TIME);
    }

    @Test
    @DisplayName("パスワードを含めたユーザ情報を更新する際、古いデータをもとに更新しようとしたとき、OptimisticLockingFailureExceptionが発生する")
    void shouldThrowOptimisticLockingFailureExceptionWhenUpdateUserWithPasswordWithOldData() {
        String hashedPassword = "hashedPassword";

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setPassword("password");
        userAccount.setUpdatedAt(TEST_TIME);

        when(passwordUtility.hashPassword(userAccount.getPassword())).thenReturn(hashedPassword);
        when(userAccountRepository.updateWithPassword(userAccount.getUserId(), userAccount.getIsAdmin(), userAccount.getEmail(),
                userAccount.getUsername(), hashedPassword, TEST_TIME, TEST_TIME)).thenReturn(0);

        assertThatThrownBy(() -> userAccountManager.updateUserWithPassword(userAccount, TEST_TIME))
                .isInstanceOf(OptimisticLockingFailureException.class);
        verify(userAccountRepository, times(1)).updateWithPassword(userAccount.getUserId(), userAccount.getIsAdmin(),
                userAccount.getEmail(), userAccount.getUsername(), hashedPassword, TEST_TIME, TEST_TIME);
    }

    @Test
    @DisplayName("パスワードを除いたユーザ情報の更新が正常に終了する")
    void shouldUpdateUserWithoutPassword() {
        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);
        expected.setUpdatedAt(TEST_TIME);

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setUpdatedAt(TEST_TIME);

        when(userAccountRepository.updateWithoutPassword(userAccount.getUserId(), userAccount.getIsAdmin(), userAccount.getEmail(),
                userAccount.getUsername(), TEST_TIME, TEST_TIME)).thenReturn(1);

        try {
            userAccountManager.updateUserWithoutPassword(userAccount, TEST_TIME);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(userAccountRepository, times(1)).updateWithoutPassword(expected.getUserId(), expected.getIsAdmin(),
                expected.getEmail(), expected.getUsername(), EXPECTED_TEST_TIME, EXPECTED_TEST_TIME);
    }

    @Test
    @DisplayName("パスワードを除いたユーザ情報を更新する際、一意制約のあるデータが重複しているとき、JpaSystemExceptionが発生する")
    void shouldThrowJpaSystemExceptionWhenUpdateUserWithoutPasswordWithDuplicatedUniqueField() {
        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);
        expected.setUpdatedAt(TEST_TIME);

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setUpdatedAt(TEST_TIME);

        when(userAccountRepository.updateWithoutPassword(userAccount.getUserId(), userAccount.getIsAdmin(), userAccount.getEmail(),
                userAccount.getUsername(), TEST_TIME, OPTIMISTIC_LOCK_UPDATED_AT))
                .thenThrow(JpaSystemException.class);

        assertThatThrownBy(() -> userAccountManager.updateUserWithoutPassword(userAccount, OPTIMISTIC_LOCK_UPDATED_AT))
                .isInstanceOf(JpaSystemException.class);
        verify(userAccountRepository, times(1)).updateWithoutPassword(expected.getUserId(), expected.getIsAdmin(),
                expected.getEmail(), expected.getUsername(), EXPECTED_TEST_TIME, OPTIMISTIC_LOCK_UPDATED_AT);
    }

    @Test
    @DisplayName("パスワードを除いたユーザ情報を更新する際、DBへの接続ができないとき、DataAccessExceptionのサブクラスであるDataAccessResourceFailureExceptionが発生する")
    void shouldThrowDataAccessExceptionWhenUpdateUserWithoutPasswordWithFailedDbConnection() {
        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);
        expected.setUpdatedAt(TEST_TIME);

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setUpdatedAt(TEST_TIME);

        when(userAccountRepository.updateWithoutPassword(userAccount.getUserId(), userAccount.getIsAdmin(), userAccount.getEmail(),
                userAccount.getUsername(), TEST_TIME, OPTIMISTIC_LOCK_UPDATED_AT))
                .thenThrow(DataAccessResourceFailureException.class);

        assertThatThrownBy(() -> userAccountManager.updateUserWithoutPassword(userAccount, OPTIMISTIC_LOCK_UPDATED_AT))
                .isInstanceOf(DataAccessException.class);
        verify(userAccountRepository, times(1)).updateWithoutPassword(expected.getUserId(), expected.getIsAdmin(),
                expected.getEmail(), expected.getUsername(), EXPECTED_TEST_TIME, OPTIMISTIC_LOCK_UPDATED_AT);
    }

    @Test
    @DisplayName("パスワードを除いたユーザ情報を更新する際、古いデータをもとに更新しようとしたとき、OptimisticLockingFailureExceptionが発生する")
    void shouldThrowOptimisticLockingFailureExceptionWhenUpdateUserWithoutPasswordWithOldData() {
        UserAccount expected = new UserAccount();
        expected.setUserId("testUserId");
        expected.setUsername("testUsername");
        expected.setEmail("test@solxyz.co.jp");
        expected.setIsAdmin(true);
        expected.setUpdatedAt(TEST_TIME);

        UserAccount userAccount = new UserAccount();
        userAccount.setUserId("testUserId");
        userAccount.setUsername("testUsername");
        userAccount.setEmail("test@solxyz.co.jp");
        userAccount.setIsAdmin(true);
        userAccount.setUpdatedAt(TEST_TIME);

        when(userAccountRepository.updateWithoutPassword(userAccount.getUserId(), userAccount.getIsAdmin(), userAccount.getEmail(),
                userAccount.getUsername(), TEST_TIME, OPTIMISTIC_LOCK_UPDATED_AT))
                .thenReturn(0);

        assertThatThrownBy(() -> userAccountManager.updateUserWithoutPassword(userAccount, OPTIMISTIC_LOCK_UPDATED_AT))
                .isInstanceOf(OptimisticLockingFailureException.class);
        verify(userAccountRepository, times(1)).updateWithoutPassword(expected.getUserId(), expected.getIsAdmin(),
                expected.getEmail(), expected.getUsername(), EXPECTED_TEST_TIME, OPTIMISTIC_LOCK_UPDATED_AT);
    }

    @Test
    @DisplayName("ユーザ情報の削除が正常に終了する")
    void shouldDeleteUser() {
        String userId = "testUserId";
        String expectedUserId = "testUserId";

        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(new UserAccount()));
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(List.of());

        try {
            userAccountManager.deleteUser(userId);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(userAccountRepository, times(1)).findById(userId);
        verify(bookCheckoutHistoryRepository, times(1)).findUnreturnedBooksByUserId(userId);
        verify(userAccountRepository, times(1)).deleteById(expectedUserId);
    }

    @Test
    @DisplayName("削除対象のユーザーIDが存在しないとき、NoSuchElementExceptionが発生する")
    void shouldThrowNoSuchElementExceptionWhenDeleteUserWithNonexistentUserId() {
        String userId = "testUserId";

        UserAccount userAccountDto = new UserAccount();
        userAccountDto.setUserId("testUserId");

        when(userAccountRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAccountManager.deleteUser(userId))
                .isInstanceOf(NoSuchElementException.class);
        verify(userAccountRepository, times(1)).findById(userId);
        verify(bookCheckoutHistoryRepository, times(0)).findUnreturnedBooksByUserId(userId);
        verify(userAccountRepository, times(0)).deleteById(userId);
    }

    @Test
    @DisplayName("未返却の書籍が存在するとき、IllegalStateExceptionが発生する")
    void shouldNotDeleteUserWhenUnreturnedBooksExist() {
        String userId = "testUserId";

        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(new UserAccount()));
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(List.of(new BookCheckoutHistory()));

        assertThatThrownBy(() -> userAccountManager.deleteUser(userId))
                .isInstanceOf(IllegalStateException.class);
        verify(userAccountRepository, times(1)).findById(userId);
        verify(bookCheckoutHistoryRepository, times(1)).findUnreturnedBooksByUserId(userId);
        verify(userAccountRepository, times(0)).deleteById(userId);
    }

    @Test
    @DisplayName("ユーザ情報を削除する際、DBへの接続ができないとき、DataAccessExceptionのサブクラスであるDataAccessResourceFailureExceptionが発生する")
    void shouldThrowDataAccessExceptionWhenDeleteUserWithFailedDbConnection() {
        String userId = "testUserId";

        when(userAccountRepository.findById(userId)).thenReturn(Optional.of(new UserAccount()));
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(List.of());

        doThrow(DataAccessResourceFailureException.class).when(userAccountRepository).deleteById(userId);

        assertThatThrownBy(() -> userAccountManager.deleteUser(userId))
                .isInstanceOf(DataAccessException.class);
        verify(userAccountRepository, times(1)).findById(userId);
        verify(bookCheckoutHistoryRepository, times(1)).findUnreturnedBooksByUserId(userId);
        verify(userAccountRepository, times(1)).deleteById(userId);
    }
}
