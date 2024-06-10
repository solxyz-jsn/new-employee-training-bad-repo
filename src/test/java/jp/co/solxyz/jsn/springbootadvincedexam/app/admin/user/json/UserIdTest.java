package jp.co.solxyz.jsn.springbootadvincedexam.app.user.json;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserIdTest {

    private final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

    private final Validator VALIDATOR = FACTORY.getValidator();

    @Test
    @DisplayName("ユーザIDが正常な場合、バリデーションエラーは発生しない")
    void shouldNotHaveValidationErrorsWhenUserIdIsValid() {
        UserId userId = new UserId();
        userId.setUserId("123e4567-e89b-12d3-a456-426614174000");

        Set<ConstraintViolation<UserId>> violations = VALIDATOR.validate(userId);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("ユーザIDが空の場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorsWhenUserIdIsEmpty() {
        UserId userId = new UserId();

        Set<ConstraintViolation<UserId>> violations = VALIDATOR.validate(userId);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("ユーザIDは必須です。");
    }

    @Test
    @DisplayName("ユーザIDが不正な場合、バリデーションエラーが発生する")
    void shouldHaveValidationErrorsWhenUserIdIsInvalid() {
        UserId userId = new UserId();
        userId.setUserId("invalid");

        Set<ConstraintViolation<UserId>> violations = VALIDATOR.validate(userId);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("不正なユーザIDです。");
    }
}
