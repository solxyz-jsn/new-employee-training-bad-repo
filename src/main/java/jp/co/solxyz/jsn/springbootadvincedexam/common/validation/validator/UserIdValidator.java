package jp.co.solxyz.jsn.springbootadvincedexam.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jp.co.solxyz.jsn.springbootadvincedexam.common.validation.anotation.UserId;

public class UserIdValidator implements ConstraintValidator<UserId, String> {
    @Override
    public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");
    }
}
