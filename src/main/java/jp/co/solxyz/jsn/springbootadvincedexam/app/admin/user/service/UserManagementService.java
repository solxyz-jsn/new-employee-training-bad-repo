package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.service;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.json.AccountProfile;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model.UserManagementModel;
import jp.co.solxyz.jsn.springbootadvincedexam.util.UUIDGenerator;
import jp.co.solxyz.jsn.springbootadvincedexam.component.user.UserAccountManager;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user.UserAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * ユーザ管理サービス
 */
@Service
@Slf4j
public class UserManagementService {

    /**
     * ユーザアカウントリポジトリ
     */
    private final UserAccountManager userAccountManager;

    /**
     * コンストラクタ
     * @param userAccountManager ユーザアカウントリポジトリ
     */
    public UserManagementService(UserAccountManager userAccountManager) {
        this.userAccountManager = userAccountManager;
    }

    /**
     * パスワードを除いたユーザ情報を全件取得する
     * @return ユーザモデルリスト
     */
    public List<UserManagementModel> getAllUsers() {
        List<UserAccount> users = userAccountManager.getAllUsersWithoutPassword();
        List<UserManagementModel> userModel = new ArrayList<>();
        for (UserAccount userDto : users) {
            UserManagementModel user = new UserManagementModel();
            user.setUserId(userDto.getUserId());
            user.setUserName(userDto.getUsername());
            user.setEmail(userDto.getEmail());
            user.setAdmin(userDto.getIsAdmin());
            userModel.add(user);
        }
        return userModel;
    }

    /**
     * 新規ユーザを追加する
     * @param newUserModel 追加するユーザ情報
     */
    public void addUser(AccountProfile newUserModel) {
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(UUIDGenerator.generateUserId());
        userAccount.setIsAdmin(newUserModel.isAdmin());
        userAccount.setEmail(newUserModel.getEmail());
        userAccount.setUsername(newUserModel.getUserName());
        userAccount.setPassword(newUserModel.getPassword());
        userAccount.setUpdatedAt(Instant.now());

        userAccountManager.addUser(userAccount);
    }

    /**
     * ユーザ情報を更新する
     * @param updatedUserModel 更新するユーザ情報
     */
    public void updateUser(AccountProfile updatedUserModel) {
        UserAccount currentUserAccountDto = userAccountManager.findByIdWithoutPassword(updatedUserModel.getUserId());
        UserAccount updatedUserAccount = new UserAccount();
        updatedUserAccount.setUserId(currentUserAccountDto.getUserId());
        updatedUserAccount.setIsAdmin(updatedUserModel.isAdmin());
        updatedUserAccount.setEmail(updatedUserModel.getEmail());
        updatedUserAccount.setUsername(updatedUserModel.getUserName());
        updatedUserAccount.setPassword(updatedUserModel.getPassword());
        updatedUserAccount.setUpdatedAt(Instant.now());

        if (updatedUserAccount.getPassword().isEmpty()) {
            // パスワードが空の場合はパスワードを含めない
            userAccountManager.updateUserWithoutPassword(updatedUserAccount, currentUserAccountDto.getUpdatedAt());
        } else {
            userAccountManager.updateUserWithPassword(updatedUserAccount, currentUserAccountDto.getUpdatedAt());
        }
    }

    /**
     * ユーザ情報を削除する
     * @param userId 削除するユーザID
     */
    public void deleteUser(String userId) {
        userAccountManager.deleteUser(userId);
    }
}
