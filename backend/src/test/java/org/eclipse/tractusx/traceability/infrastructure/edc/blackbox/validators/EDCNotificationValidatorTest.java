package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.validators;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationReceiverBpnMismatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EDCNotificationValidatorTest {


	@Mock
	TraceabilityProperties traceabilityProperties;

	@Mock
	ConstraintValidatorContext context;

	@Mock
	EDCNotification edcNotification;

	@InjectMocks
	EDCNotificationValidator validator;

	@Test
	void testIsValidWithNullEDCNotification() {
		// Given
		EDCNotification edcNotification = null;

		// When
		boolean result = validator.isValid(edcNotification, context);

		// Then
		assertTrue(result);

	}


	@Test
	void testIsValidSenderBpnMatch() {
		// Given
		when(traceabilityProperties.getBpn()).thenReturn(BPN.of("BPN_OF_APPLICATION"));
		when(edcNotification.getSenderBPN()).thenReturn("BPN_OF_APPLICATION");
		when(edcNotification.getRecipientBPN()).thenReturn("OTHER");
		// When
		boolean result = validator.isValid(edcNotification, context);

		// Then
		assertTrue(result);
	}

	@Test
	void testReceiverIsApplicationOwner() {
		// Given
		when(traceabilityProperties.getBpn()).thenReturn(BPN.of("BPN_OF_APPLICATION"));
		when(edcNotification.getSenderBPN()).thenReturn("OTHER");
		when(edcNotification.getRecipientBPN()).thenReturn("BPN_OF_APPLICATION");

		// When
		boolean result = validator.isValid(edcNotification, context);

		// Then
		assertTrue(result);
	}

	@Test
	void testThrowsExceptionNoBpnMatch() {
		// Given
		when(traceabilityProperties.getBpn()).thenReturn(BPN.of("BPN_OF_APPLICATION"));
		when(edcNotification.getSenderBPN()).thenReturn("BPN_OF_SENDER");
		when(edcNotification.getRecipientBPN()).thenReturn("OTHER");
		// When
		// Then
		assertThrows(InvestigationReceiverBpnMismatchException.class, () -> {
			validator.isValid(edcNotification, context);
		});
	}

}


