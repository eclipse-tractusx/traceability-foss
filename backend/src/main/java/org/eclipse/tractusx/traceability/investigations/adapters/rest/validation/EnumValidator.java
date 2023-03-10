package org.eclipse.tractusx.traceability.investigations.adapters.rest.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = EnumValidatorImpl.class)
public @interface EnumValidator {
	Class<? extends Enum<?>> enumClass();

	String message() default "must be any of enum {enumClass}";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}
