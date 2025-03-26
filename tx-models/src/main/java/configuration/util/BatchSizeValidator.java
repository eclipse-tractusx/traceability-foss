package configuration.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BatchSizeValidator implements ConstraintValidator<ValidBatchSize, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && value % 10 == 0;
    }
}
