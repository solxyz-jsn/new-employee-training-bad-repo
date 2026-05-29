package jp.co.solxyz.jsn.springbootadvancedexam.common.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jp.co.solxyz.jsn.springbootadvancedexam.common.validation.annotation.Isbn;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.matches("^\\d{13}$");
    }
}
