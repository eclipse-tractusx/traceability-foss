package org.eclipse.tractusx.traceability.investigations.adapters.rest.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, CharSequence> {
	private List<String> acceptedValues;

	@Override
	public void initialize(final EnumValidator annotation) {
		acceptedValues =
			Stream.of(annotation.enumClass().getEnumConstants()).map(Enum::name).toList();
	}

	@Override
	public boolean isValid(final CharSequence value, final ConstraintValidatorContext context) {
		// This should be validated by NotNull
		if (value == null) {
			return true;
		}
		return acceptedValues.contains(value.toString());
	}
}
