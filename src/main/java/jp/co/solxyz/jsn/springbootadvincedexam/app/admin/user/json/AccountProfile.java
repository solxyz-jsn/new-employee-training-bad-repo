package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.json;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model.validation.OnUserCreation;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.user.model.validation.OnUserUpdate;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザ管理情報リクエストモデル
 */
@Data
@NoArgsConstructor
public class AccountProfile {

    /**
     * ユーザID
     */
    @NotBlank(message = "ユーザIDは必須です。", groups = OnUserUpdate.class)
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$", message = "不正なユーザIDです。",
            groups = OnUserUpdate.class)
    private String userId;

    /**
     * ユーザ名
     */
    @NotBlank(message = "ユーザ名は必須です。")
    private String userName;

    /**
     * メールアドレス
     */
    @NotBlank(message = "メールアドレスは必須です。")
    @Email(message = "有効なメールアドレスを入力してください。")
    private String email;

    /**
     * 管理者フラグ
     */
    @NotNull(message = "管理者フラグは必須です。")
    private boolean isAdmin;

    /**
     * パスワード
     */
    @NotBlank(message = "パスワードは必須です。", groups = OnUserCreation.class)
    @Size(max = 100, message = "パスワードは100文字以下である必要があります。")
    private String password;

}
