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

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class InvestigationsReceiverServiceTest {

	@Mock
	private InvestigationsRepository mockRepository;

	@Mock
	private NotificationMapper mockNotificationMapper;

	@Mock
	private InvestigationMapper mockInvestigationMapper;

    @Mock
    private InvestigationsReadService investigationsReadService;

	@InjectMocks
	private InvestigationsReceiverService service;


	@Test
	@DisplayName("Test testHandleNotificationReceiveValidSentNotification sent is valid")
	void testHandleNotificationReceiveValidSentNotification() {

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
                Severity.MINOR,
                "123",
                null,
                null,
                "messageId"
        );

		Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.RECEIVED, InvestigationStatus.RECEIVED, "recipientBPN");
		Notification notificationTestData = NotificationTestDataFactory.createNotificationTestData();
		EDCNotification edcNotification = EDCNotificationFactory.createQualityInvestigation(
			"it", notification);

		when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
		when(mockInvestigationMapper.toInvestigation(any(BPN.class), anyString(), any(Notification.class))).thenReturn(investigationTestData);

		// When
		service.handleNotificationReceive(edcNotification);
		// Then
		Mockito.verify(mockRepository).save(investigationTestData);
	}

    @Test
    @DisplayName("Test testHandleNotificationUpdateValidAcknowledgeNotificationTransition is valid")
    void testHandleNotificationUpdateValidAcknowledgeNotificationTransition() {

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
                InvestigationStatus.ACKNOWLEDGED,
                affectedParts,
                Instant.now(),
                Severity.MINOR,
                "123",
                null,
                null,
                "messageId"
        );



        Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.RECEIVED, InvestigationStatus.RECEIVED, "recipientBPN");
        Notification notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createQualityInvestigation(
                "it", notification);

        when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
        when(investigationsReadService.loadInvestigationByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(investigationTestData);

        // When
        service.handleNotificationUpdate(edcNotification);
        // Then
        Mockito.verify(mockRepository).update(investigationTestData);
    }

    @Test
    @DisplayName("Test testHandleNotificationUpdateValidDeclineNotificationTransition is valid")
    void testHandleNotificationUpdateValidDeclineNotificationTransition() {

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
                InvestigationStatus.DECLINED,
                affectedParts,
                Instant.now(),
                Severity.MINOR,
                "123",
                null,
                null,
                "messageId"
        );



        Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.ACKNOWLEDGED, InvestigationStatus.ACKNOWLEDGED, "recipientBPN");
        Notification notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createQualityInvestigation(
                "it", notification);

        when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
        when(investigationsReadService.loadInvestigationByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(investigationTestData);

        // When
        service.handleNotificationUpdate(edcNotification);
        // Then
        Mockito.verify(mockRepository).update(investigationTestData);
    }

    @Test
    @DisplayName("Test testHandleNotificationUpdateValidAcknowledgeNotification is valid")
    void testHandleNotificationUpdateValidAcceptedNotificationTransition() {

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
                InvestigationStatus.ACCEPTED,
                affectedParts,
                Instant.now(),
                Severity.MINOR,
                "123",
                null,
                null,
                "messageId"
        );



        Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.ACKNOWLEDGED, InvestigationStatus.ACKNOWLEDGED, "recipientBPN");
        Notification notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createQualityInvestigation(
                "it", notification);

        when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
        when(investigationsReadService.loadInvestigationByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(investigationTestData);

        // When
        service.handleNotificationUpdate(edcNotification);
        // Then
        Mockito.verify(mockRepository).update(investigationTestData);
    }

    @Test
    @DisplayName("Test testHandleNotificationUpdateValidAcknowledgeNotification is valid")
    void testHandleNotificationUpdateValidCloseNotificationTransition() {

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
                InvestigationStatus.CLOSED,
                affectedParts,
                Instant.now(),
                Severity.MINOR,
                "123",
                null,
                null,
                "messageId"
        );



        Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.ACKNOWLEDGED, InvestigationStatus.ACKNOWLEDGED, "senderBPN");
        Notification notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createQualityInvestigation(
                "it", notification);

        when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
        when(investigationsReadService.loadInvestigationByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(investigationTestData);

        // When
        service.handleNotificationUpdate(edcNotification);
        // Then
        Mockito.verify(mockRepository).update(investigationTestData);
    }

}

