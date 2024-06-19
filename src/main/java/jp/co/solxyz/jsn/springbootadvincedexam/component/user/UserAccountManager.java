package jp.co.solxyz.jsn.springbootadvincedexam.component.user;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookCheckoutHistoryRepository;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.user.UserAccountRepository;
import jp.co.solxyz.jsn.springbootadvincedexam.util.PasswordUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * ユーザビジネスロジッククラス
 */
@Component
@Slf4j
public class UserAccountManager {

    /**
     * ユーザアカウントリポジトリ
     */
    private final UserAccountRepository userAccountRepository;

    /**
     * 書籍貸し出し履歴リポジトリ
     */
    private final BookCheckoutHistoryRepository bookCheckoutHistoryRepository;

    /**
     * パスワードユーティリティ
     */
    private final PasswordUtility passwordUtility;

    /**
     * コンストラクタ
     * @param userAccountRepository ユーザアカウントリポジトリ
     * @param bookCheckoutHistoryRepository 書籍貸し出し履歴リポジトリ
     * @param passwordUtility パスワードユーティリティ
     */
    public UserAccountManager(UserAccountRepository userAccountRepository, BookCheckoutHistoryRepository bookCheckoutHistoryRepository,
            PasswordUtility passwordUtility) {
        this.userAccountRepository = userAccountRepository;
        this.bookCheckoutHistoryRepository = bookCheckoutHistoryRepository;
        this.passwordUtility = passwordUtility;
    }

    /**
     * パスワードを除いたユーザ情報を全件取得する
     * @return ユーザ情報リスト
     */
    public List<UserAccount> getAllUsersWithoutPassword() {
        return userAccountRepository.findAll().stream()
                .peek(user -> user.setPassword(null))
                .collect(Collectors.toList());
    }

    /**
     * ユーザIDによりパスワードを除いたユーザ情報を1件取得する
     * @param userId ユーザID
     * @return ユーザ情報
     * @throws NoSuchElementException DBに存在しないユーザIDが指定された場合
     */
    public UserAccount findByIdWithoutPassword(String userId) throws NoSuchElementException {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("存在しないユーザIDです。");
                    return new NoSuchElementException("既に削除されたか、存在しないユーザです。");
                });
    }

    /**
     * 新規ユーザを追加する
     * @param userAccount ユーザ情報
     * @throws JpaSystemException 一意制約違反が発生した場合
     * @throws DataAccessException DBとの接続で問題が発生した場合
     */
    public void addUser(UserAccount userAccount) throws DataAccessException, JpaSystemException {
        userAccount.setPassword(passwordUtility.hashPassword(userAccount.getPassword()));
        try {
            userAccountRepository.save(userAccount);
        } catch (JpaSystemException e) {
            log.error("一意制約のある項目が重複しています。", e);
            throw e;
        } catch (DataAccessException e) {
            log.error("DBへの接続ができませんでした。", e);
            throw e;
        }
    }

    /**
     * パスワードを含めたユーザ情報を更新する
     * @param updatedUserAccount 更新するユーザ情報
     * @param optimisticLockUpdatedAt 楽観的ロック用の更新日時
     * @throws OptimisticLockingFailureException 対象のユーザが既に他の管理者によって更新されていた場合
     * @throws JpaSystemException 一意制約違反が発生した場合
     * @throws DataAccessException DBとの接続で問題が発生した場合
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserWithPassword(UserAccount updatedUserAccount, Instant optimisticLockUpdatedAt)
            throws OptimisticLockingFailureException, JpaSystemException, DataAccessException {
        int result;
        try {
            result = userAccountRepository.updateWithPassword(updatedUserAccount.getUserId(), updatedUserAccount.getIsAdmin(),
                    updatedUserAccount.getEmail(),
                    updatedUserAccount.getUsername(),
                    passwordUtility.hashPassword(updatedUserAccount.getPassword()), updatedUserAccount.getUpdatedAt(), optimisticLockUpdatedAt);
        } catch (JpaSystemException e) {
            log.info("一意制約のあるデータが重複しています。", e);
            throw e;
        } catch (DataAccessException e) {
            log.error("DBへの接続ができませんでした。", e);
            throw e;
        }

        if (result == 0) {
            log.info("古いデータをもとに更新しようとしました。");
            throw new OptimisticLockingFailureException("ユーザーは他の管理者に更新されました。最新の情報から再度更新してください。");
        }
    }

    /**
     * パスワードを除いたユーザ情報を更新する
     * @param updatedUserAccount 更新するユーザ情報
     * @param optimisticLockUpdatedAt 楽観的ロック用の更新日時
     * @throws OptimisticLockingFailureException 対象のユーザが既に他の管理者によって更新されていた場合
     * @throws JpaSystemException 一意制約違反が発生した場合
     * @throws DataAccessException DBとの接続で問題が発生した場合
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUserWithoutPassword(UserAccount updatedUserAccount, Instant optimisticLockUpdatedAt)
            throws OptimisticLockingFailureException, JpaSystemException, DataAccessException {
        int result;
        try {
            result = userAccountRepository.updateWithoutPassword(updatedUserAccount.getUserId(), updatedUserAccount.getIsAdmin(),
                    updatedUserAccount.getEmail(),
                    updatedUserAccount.getUsername(),
                    updatedUserAccount.getUpdatedAt(), optimisticLockUpdatedAt);
        } catch (JpaSystemException e) {
            log.info("一意制約のあるデータが重複しています。", e);
            throw e;
        } catch (DataAccessException e) {
            log.error("DBへの接続ができませんでした。", e);
            throw e;
        }

        if (result == 0) {
            log.info("古いデータをもとに更新しようとしました。");
            throw new OptimisticLockingFailureException("ユーザーは他の管理者に更新されました。最新の情報から再度更新してください。");
        }
    }

    /**
     * ユーザ情報を削除する
     * @param userId ユーザID
     * @throws NoSuchElementException DBに削除対象が存在しない場合
     * @throws IllegalStateException ユーザに未返却の書籍が存在する場合
     * @throws DataAccessException DBとの接続で問題が発生した場合
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String userId) throws NoSuchElementException, IllegalStateException, DataAccessException {
        userAccountRepository.findById(userId).orElseThrow(() -> {
            log.warn("削除対象が存在しません。");
            return new NoSuchElementException("存在しないユーザIDか、既にユーザが削除されています。");
        });

        if (!bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId).isEmpty()) {
            log.warn("未返却の書籍が存在します。");
            throw new IllegalStateException("未返却の書籍が存在するため、ユーザを削除できません。");
        }

        try {
            userAccountRepository.deleteById(userId);
        } catch (DataAccessException e) {
            log.error("DBへの接続ができませんでした。", e);
            throw e;
        }
    }
}
