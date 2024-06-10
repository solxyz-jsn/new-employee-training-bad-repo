package jp.co.solxyz.jsn.springbootadvincedexam.common.validation.anotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jp.co.solxyz.jsn.springbootadvincedexam.common.validation.validator.UserIdValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserIdValidator.class)
public @interface UserId {
    String message() default "ユーザIDが不正です。";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
