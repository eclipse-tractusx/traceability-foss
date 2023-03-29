package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.common.mapper.InvestigationMapper;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationFactory;
import org.eclipse.tractusx.traceability.investigations.domain.model.*;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class InvestigationsReceiverServiceTest {

	@Mock
	private InvestigationsRepository mockRepository;

	@Mock
	private NotificationMapper mockNotificationMapper;

	@Mock
	private InvestigationMapper mockInvestigationMapper;

	@InjectMocks
	private InvestigationsReceiverService service;


	@Test
	@DisplayName("Test handleNotificationReceiverCallback sent is valid")
	void testHandleNotificationReceiverCallbackValidSentNotification() {

		// Given
		List<AffectedPart> affectedParts = List.of(new AffectedPart("partId"));
        Notification notification = new Notification(
                "123",
                "id123",
                "senderBPN",
                "senderManufacturerName",
                "recipientBPN",
                "receiverManufacturerName",
                "senderAddress",
                "agreement",
                "information",
                InvestigationStatus.SENT,
                affectedParts,
                Instant.now(),
                Severity.MINOR
        );

		Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.RECEIVED, InvestigationStatus.RECEIVED, "recipientBPN");
		Notification notificationTestData = NotificationTestDataFactory.createNotificationTestData();
		EDCNotification edcNotification = EDCNotificationFactory.createQualityInvestigation(
			"it", notification);

		when(mockNotificationMapper.toReceiverNotification(edcNotification, InvestigationStatus.SENT)).thenReturn(notificationTestData);
		when(mockInvestigationMapper.toReceiverInvestigation(any(BPN.class), anyString(), any(Notification.class))).thenReturn(investigationTestData);

		// When
		service.handleNotificationReceiverCallback(edcNotification);
		// Then
		Mockito.verify(mockRepository).save(investigationTestData);
	}

}

