package jp.co.solxyz.jsn.springbootadvincedexam.common.validation.anotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jp.co.solxyz.jsn.springbootadvincedexam.common.validation.validator.IsbnValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsbnValidator.class)
public @interface Isbn {
    String message() default "ISBNが不正です。";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
