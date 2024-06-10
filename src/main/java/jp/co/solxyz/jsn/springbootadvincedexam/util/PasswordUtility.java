package jp.co.solxyz.jsn.springbootadvincedexam.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 文字列ユーティリティ
 */
@Component
public class PasswordUtility {

    /**
     * パスワードエンコーダ
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * コンストラクタ
     * @param passwordEncoder パスワードエンコーダ
     */
    public PasswordUtility(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * パスワードをハッシュ化する
     * @param password パスワード
     * @return ハッシュ化されたパスワード
     */
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
