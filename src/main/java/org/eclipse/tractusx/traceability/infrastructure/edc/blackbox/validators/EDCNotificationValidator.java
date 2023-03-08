package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.validators;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationReceiverBpnMismatchException;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * A validator for {@link EDCNotification} objects annotated with {@link ValidEDCNotification}.
 * This validator ensures that the sender BPN of the notification is eligible to handle the application BPN.
 * If the sender BPN is null or does not match the application BPN, an {@link InvestigationReceiverBpnMismatchException}
 * will be thrown.
 */
@Component
public class EDCNotificationValidator implements ConstraintValidator<ValidEDCNotification, EDCNotification> {
	private final TraceabilityProperties traceabilityProperties;


	public EDCNotificationValidator(TraceabilityProperties traceabilityProperties) {
		this.traceabilityProperties = traceabilityProperties;
	}

	/**
	 * Initializes the validator.
	 *
	 * @param constraintAnnotation the annotation that this validator is validating
	 */
	@Override
	public void initialize(ValidEDCNotification constraintAnnotation) {
		// No initialization needed
	}

    /**
     * Validates the specified {@link EDCNotification} object.
     *
     * @param edcNotification the {@link EDCNotification} object to validate
     * @param context         the {@link ConstraintValidatorContext} for creating validation messages
     * @return true if the validation succeeds
     * @throws InvestigationReceiverBpnMismatchException if the sender BPN is null or does not match the application BPN
     */
    @Override
    public boolean isValid(EDCNotification edcNotification, ConstraintValidatorContext context) {
        // Will not be handled in this validation
        if (edcNotification == null) {
            return true;
        }
        BPN applicationBPN = traceabilityProperties.getBpn();
        String senderBPN = edcNotification.getSenderBPN();
        String recipientBPN = edcNotification.getRecipientBPN();
        if (senderBPN == null) {
            throw new InvestigationReceiverBpnMismatchException("BPN of sender cannot be null.");
        }
        if (!senderBPN.equals(applicationBPN.value()) && !recipientBPN.equals(applicationBPN.value())) {
            final String senderBPNIsNotSameAsReceiverError = String.format("BPN {%s} is not eligible to handle BPN: {%s}", applicationBPN.value(), senderBPN);
            throw new InvestigationReceiverBpnMismatchException(senderBPNIsNotSameAsReceiverError);
        }

        return true;
    }


}
