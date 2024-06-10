package jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.user;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * ユーザアカウントリポジトリ
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    /**
     * メールアドレスによりユーザ情報を取得する
     * @param email メールアドレス
     * @return ユーザ情報
     */
    @Query("from UserAccount e where e.email = :email")
    UserAccount findByEmail(String email);

    /**
     * パスワードを含めたユーザ情報を更新する
     * @param userId ユーザID
     * @param isAdmin 管理者権限
     * @param email メールアドレス
     * @param username ユーザ名
     * @param password パスワード
     * @param updatedAt 更新日時
     * @param optimisticLockUpdatedAt 楽観ロック用更新日時
     */
    @Transactional
    @Modifying
    @Query("UPDATE UserAccount e SET e.isAdmin = :isAdmin, e.email = :email, e.username = :username, e.password = :password, e.updatedAt = :updatedAt WHERE e.userId = :userId AND e.updatedAt = :optimisticLockUpdatedAt")
    int updateWithPassword(String userId, boolean isAdmin, String email, String username,
            String password, Instant updatedAt, Instant optimisticLockUpdatedAt);

    /**
     * パスワードを除いたユーザ情報を更新する
     * @param userId ユーザID
     * @param isAdmin 管理者権限
     * @param email メールアドレス
     * @param username ユーザ名
     * @param updatedAt 更新日時
     * @param optimisticLockUpdatedAt 楽観ロック用更新日時
     */
    @Transactional
    @Modifying
    @Query("UPDATE UserAccount e SET e.isAdmin = :isAdmin, e.email = :email, e.username = :username, e.updatedAt = :updatedAt WHERE e.userId = :userId AND e.updatedAt = :optimisticLockUpdatedAt")
    int updateWithoutPassword(String userId, boolean isAdmin, String email,
            String username, Instant updatedAt, Instant optimisticLockUpdatedAt);
}
