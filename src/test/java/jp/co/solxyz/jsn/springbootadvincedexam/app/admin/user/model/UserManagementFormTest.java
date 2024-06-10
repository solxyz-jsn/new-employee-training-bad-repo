package jp.co.solxyz.jsn.springbootadvincedexam.app.user.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.json.UserManagementForm;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.model.validation.OnUserCreation;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.model.validation.OnUserUpdate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserManagementFormTest {

    private final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

    private final Validator VALIDATOR = FACTORY.getValidator();

    private final String OVER_101_CHARACTERS = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    @Test
    @DisplayName("全てのフィールドが正しい場合、バリデーションエラーは発生しない")
    void shouldNotHaveValidationErrorsWhenAllFieldsAreCorrect() {
        UserManagementForm form = new UserManagementForm();
        form.setUserId("123e4567-e89b-12d3-a456-426614174000");
        form.setUserName("testuser");
        form.setEmail("test@solxyz.co.jp");
        form.setIsAdmin(true);
        form.setPassword("password");

        Set<ConstraintViolation<UserManagementForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("ユーザIDが空の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorsWhenUserIdIsEmpty() {
        UserManagementForm form = new UserManagementForm();
        form.setUserId("");
        form.setUserName("testuser");
        form.setEmail("test@solxyz.co.jp");
        form.setIsAdmin(true);
        form.setPassword("password");

        Set<ConstraintViolation<UserManagementForm>> violations = VALIDATOR.validate(form, OnUserUpdate.class);

        assertThat(violations).hasSize(2);
        assertThat(violations).anyMatch(violation -> violation.getMessage().equals("ユーザIDは必須です。"));
        assertThat(violations).anyMatch(violation -> violation.getMessage().equals("不正なユーザIDです。"));
    }

    @Test
    @DisplayName("ユーザIDが不正な場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorsWhenUserIdIsInvalid() {
        UserManagementForm form = new UserManagementForm();
        form.setUserId("invalid");
        form.setUserName("testuser");
        form.setEmail("test@solxyz.co.jp");
        form.setIsAdmin(true);
        form.setPassword("password");

        Set<ConstraintViolation<UserManagementForm>> violations = VALIDATOR.validate(form, OnUserUpdate.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("不正なユーザIDです。");
    }

    @Test
    @DisplayName("ユーザ名が空の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorsWhenUserNameIsEmpty() {
        UserManagementForm form = new UserManagementForm();
        form.setUserId("123e4567-e89b-12d3-a456-426614174000");
        form.setUserName("");
        form.setEmail("test@solxyz.co.jp");
        form.setIsAdmin(true);
        form.setPassword("password");

        Set<ConstraintViolation<UserManagementForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("ユーザ名は必須です。");
    }

    @Test
    @DisplayName("メールアドレスが空の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorsWhenEmailIsEmpty() {
        UserManagementForm form = new UserManagementForm();
        form.setUserId("123e4567-e89b-12d3-a456-426614174000");
        form.setUserName("testuser");
        form.setEmail("");
        form.setIsAdmin(true);
        form.setPassword("password");

        Set<ConstraintViolation<UserManagementForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("メールアドレスは必須です。");
    }

    @Test
    @DisplayName("メールアドレスが不正な場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorsWhenEmailIsInvalid() {
        UserManagementForm form = new UserManagementForm();
        form.setUserId("123e4567-e89b-12d3-a456-426614174000");
        form.setUserName("testuser");
        form.setEmail("invalid");
        form.setIsAdmin(true);
        form.setPassword("password");

        Set<ConstraintViolation<UserManagementForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("有効なメールアドレスを入力してください。");

    }

    @Test
    @DisplayName("パスワードが空の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorsWhenPasswordIsEmpty() {
        UserManagementForm form = new UserManagementForm();
        form.setUserId("123e4567-e89b-12d3-a456-426614174000");
        form.setUserName("testuser");
        form.setEmail("test@solxyz.co.jp");
        form.setIsAdmin(true);
        form.setPassword("");

        Set<ConstraintViolation<UserManagementForm>> violations = VALIDATOR.validate(form, OnUserCreation.class);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("パスワードは必須です。");
    }

    @Test
    @DisplayName("パスワードが101文字以上の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorsWhenPasswordIsOver100Characters() {
        UserManagementForm form = new UserManagementForm();
        form.setUserId("123e4567-e89b-12d3-a456-426614174000");
        form.setUserName("testuser");
        form.setEmail("test@solxyz.co.jp");
        form.setIsAdmin(true);
        form.setPassword(OVER_101_CHARACTERS);

        Set<ConstraintViolation<UserManagementForm>> violations = VALIDATOR.validate(form);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("パスワードは100文字以下である必要があります。");
    }
}
