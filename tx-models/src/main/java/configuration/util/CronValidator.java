package configuration.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.scheduling.support.CronExpression;

public class CronValidator implements ConstraintValidator<ValidCron, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return CronExpression.isValidExpression(value);
    }
}
