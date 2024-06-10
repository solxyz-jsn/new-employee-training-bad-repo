package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザ管理情報レスポンスモデル
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserManagementModel {

    /**
     * ユーザID
     */
    private String userId;

    /**
     * ユーザ名
     */
    private String userName;

    /**
     * メールアドレス
     */
    private String email;

    /**
     * 管理者フラグ
     */
    private boolean isAdmin;
}
