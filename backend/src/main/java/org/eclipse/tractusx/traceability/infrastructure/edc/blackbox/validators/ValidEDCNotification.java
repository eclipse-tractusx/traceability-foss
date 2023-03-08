package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(PARAMETER)
@Constraint(validatedBy = EDCNotificationValidator.class)
public @interface ValidEDCNotification {
	String message() default "Invalid EDCNotification";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
