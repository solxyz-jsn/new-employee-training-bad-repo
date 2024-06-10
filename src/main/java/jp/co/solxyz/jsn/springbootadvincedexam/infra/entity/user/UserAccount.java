package jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.converter.BooleanToIntegerConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * ユーザアカウントエンティティ
 */
@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
public class UserAccount implements Serializable {

    /**
     * シリアルバージョンUID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ユーザID
     */
    @Id
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    /**
     * 管理者権限
     */
    @Column(name = "is_admin", nullable = false)
    @Convert(converter = BooleanToIntegerConverter.class)
    @NotNull
    private Boolean isAdmin;

    /**
     * メールアドレス
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * ユーザ名
     */
    @Column(name = "username", length = 100, nullable = false)
    private String username;

    /**
     * パスワード
     */
    @ToString.Exclude
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    /**
     * 更新日時
     */
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private Instant updatedAt;
}
