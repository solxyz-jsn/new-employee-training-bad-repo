package jp.co.solxyz.jsn.springbootadvincedexam.infra.repository;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.dto.UserAccountDto;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.user.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAccountRepositoryTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("メールアドレスが存在する場合、ユーザーアカウントが返される")
    void shouldReturnUserAccountWhenEmailExists() {
        UserAccount expectedUserAccount = new UserAccount();
        expectedUserAccount.setEmail("test@example.com");

        UserAccount userAccount = new UserAccount();
        userAccount.setEmail("test@example.com");

        when(userAccountRepository.findByEmail(userAccount.getEmail())).thenReturn(userAccount);

        Optional<UserAccount> returnedUserAccount = Optional.ofNullable(userAccountRepository.findByEmail(userAccount.getEmail()));

        verify(userAccountRepository, times(1)).findByEmail(userAccount.getEmail());
        assertThat(returnedUserAccount.orElse(new UserAccount()).getEmail()).isEqualTo(expectedUserAccount.getEmail());
    }

    @Test
    @DisplayName("メールアドレスが存在しない場合、nullではなく空が返される")
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        String email = "test@example.com";

        when(userAccountRepository.findByEmail(email)).thenReturn(null);

        Optional<UserAccount> returnedUserAccount = Optional.ofNullable(userAccountRepository.findByEmail(email));

        verify(userAccountRepository, times(1)).findByEmail(email);
        assertThat(returnedUserAccount).isEmpty();
    }

    @Test
    @DisplayName("パスワードを除いた全ユーザ情報を取得する")
    void shouldReturnAllUserAccounts() {
        Instant testInstant = Instant.parse("2021-01-01T00:00:00Z");

        UserAccountDto userAccountDto1 = new UserAccountDto("1", true, "test1@example.com", "username1", testInstant);
        UserAccountDto userAccountDto2 = new UserAccountDto("2", false, "test2@example.com", "username2", testInstant);

        List<UserAccountDto> userAccountDtos = List.of(userAccountDto1, userAccountDto2);
        List<UserAccountDto> expectedUserAccounts = List.of(userAccountDto1, userAccountDto2);

        when(userAccountRepository.findAllWithoutPassword()).thenReturn(userAccountDtos);

        List<UserAccountDto> actualUserAccounts = userAccountRepository.findAllWithoutPassword();

        verify(userAccountRepository, times(1)).findAllWithoutPassword();
        assertThat(actualUserAccounts).isEqualTo(expectedUserAccounts);
    }

    @Test
    @DisplayName("ユーザIDによりユーザ情報を1件取得する")
    void shouldReturnUserAccountById() {
        Instant testInstant = Instant.parse("2021-01-01T00:00:00Z");

        UserAccountDto userAccountDto = new UserAccountDto("1", true, "test1@example.com", "username1", testInstant);
        UserAccountDto expectedUserAccount = new UserAccountDto("1", true, "test1@example.com", "username1", testInstant);

        when(userAccountRepository.findByIdWithoutPassword(userAccountDto.getUserId())).thenReturn(userAccountDto);

        UserAccountDto actualUserAccount = userAccountRepository.findByIdWithoutPassword(userAccountDto.getUserId());

        verify(userAccountRepository, times(1)).findByIdWithoutPassword(userAccountDto.getUserId());
        assertThat(actualUserAccount).isEqualTo(expectedUserAccount);
    }

    @Test
    @DisplayName("パスワードを含めたユーザ情報を更新する")
    void shouldUpdateUserAccountWithPassword() {
        String userId = "1";
        boolean isAdmin = true;
        String email = "test1@example.com";
        String username = "username1";
        String password = "password1";
        Instant updatedAt = Instant.parse("2021-01-01T00:00:00Z");
        Instant optimisticLockUpdatedAt = Instant.parse("2021-01-01T00:00:00Z");

        when(userAccountRepository.updateWithPassword(userId, isAdmin, email, username, password, updatedAt, optimisticLockUpdatedAt)).thenReturn(1);

        int result = userAccountRepository.updateWithPassword(userId, isAdmin, email, username, password, updatedAt, optimisticLockUpdatedAt);

        verify(userAccountRepository, times(1)).updateWithPassword(userId, isAdmin, email, username, password, updatedAt, optimisticLockUpdatedAt);
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("パスワードを除いたユーザ情報を更新する")
    void shouldUpdateUserAccountWithoutPassword() {
        String userId = "1";
        boolean isAdmin = true;
        String email = "test1@example.com";
        String username = "username1";
        Instant updatedAt = Instant.parse("2021-01-01T00:00:00Z");
        Instant optimisticLockUpdatedAt = Instant.parse("2021-01-01T00:00:00Z");

        when(userAccountRepository.updateWithoutPassword(userId, isAdmin, email, username, updatedAt, optimisticLockUpdatedAt)).thenReturn(1);

        int result = userAccountRepository.updateWithoutPassword(userId, isAdmin, email, username, updatedAt, optimisticLockUpdatedAt);

        verify(userAccountRepository, times(1)).updateWithoutPassword(userId, isAdmin, email, username, updatedAt, optimisticLockUpdatedAt);
        assertThat(result).isEqualTo(1);
    }
}
