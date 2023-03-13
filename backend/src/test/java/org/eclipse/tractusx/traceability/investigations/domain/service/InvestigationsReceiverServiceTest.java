package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.common.mapper.InvestigationMapper;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationFactory;
import org.eclipse.tractusx.traceability.investigations.domain.model.*;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationReceiverBpnMismatchException;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationStatusTransitionNotAllowed;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.NotificationStatusTransitionNotAllowed;
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
import java.util.ArrayList;
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
	private InvestigationsReadService mockReadService;

	@Mock
	private NotificationMapper mockNotificationMapper;

	@Mock
	private InvestigationMapper mockInvestigationMapper;

	@Mock
	private TraceabilityProperties mockTraceabilityProperties;

	@Mock
	private NotificationsService mockNotificationsService;

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
			"recipientBPN",
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

	@Test
	@DisplayName("Test updateInvestigation is valid")
	void testUpdateInvestigation() {

		// Given
		BPN bpn = BPN.of("recipientBPN");
		Long investigationIdRaw = 1L;
		InvestigationStatus status = InvestigationStatus.ACKNOWLEDGED;
		String reason = "the update reason";

		List<AffectedPart> affectedParts = List.of(new AffectedPart("partId"));
		Notification notification = new Notification(
			"123",
			"id123",
			"senderBPN",
			"recipientBPN",
			"senderAddress",
			"agreement",
			"information",
			InvestigationStatus.RECEIVED,
			affectedParts,
			Instant.now(),
			Severity.MINOR
		);

		Notification notification2 = new Notification(
			"456",
			"id123",
			"senderBPN",
			"recipientBPN",
			"senderAddress",
			"agreement",
			"information",
			InvestigationStatus.RECEIVED,
			affectedParts,
			Instant.now(),
			Severity.MINOR
		);
		List<Notification> notifications = new ArrayList<>();
		notifications.add(notification);
		notifications.add(notification2);

		Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(InvestigationStatus.RECEIVED, "recipientBPN", notifications);

		when(mockReadService.loadInvestigation(any(InvestigationId.class))).thenReturn(investigationTestData);

		// When
		service.updateInvestigationPublisher(bpn, investigationIdRaw, status, reason);

		// Then
		Mockito.verify(mockRepository).update(investigationTestData);
		Mockito.verify(mockNotificationsService, times(2)).updateAsync(any(Notification.class), anyBoolean());
	}

	@Test
	@DisplayName("Test updateInvestigation is invalid because investigation status transition not allowed")
	void testUpdateInvestigationInvalid() {

		// Given
		BPN bpn = BPN.of("recipientBPN");
		Long investigationIdRaw = 1L;
		InvestigationStatus status = InvestigationStatus.ACCEPTED;
		String reason = "the update reason";

		List<AffectedPart> affectedParts = List.of(new AffectedPart("partId"));
		Notification notification = new Notification(
			"123",
			"id123",
			"senderBPN",
			"recipientBPN",
			"senderAddress",
			"agreement",
			"information",
			InvestigationStatus.CREATED,
			affectedParts,
			Instant.now(),
			Severity.MINOR
		);

		List<Notification> notifications = new ArrayList<>();
		notifications.add(notification);

		Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(InvestigationStatus.SENT, "recipientBPN", notifications);

		when(mockReadService.loadInvestigation(any(InvestigationId.class))).thenReturn(investigationTestData);

		// When
		assertThrows(InvestigationStatusTransitionNotAllowed.class, () -> {
			service.updateInvestigationPublisher(bpn, investigationIdRaw, status, reason);
		});

		// Then
		Mockito.verify(mockRepository, never()).update(investigationTestData);
		Mockito.verify(mockNotificationsService, never()).updateAsync(any(Notification.class));
	}

	@Test
	@DisplayName("Test updateInvestigation is invalid because notification receiverbpn is not same as application bpn")
	void testUpdateInvestigationInvalidBpnMismatch() {

		// Given
		BPN bpn = BPN.of("applicationBPN");
		Long investigationIdRaw = 1L;
		InvestigationStatus status = InvestigationStatus.ACKNOWLEDGED;
		String reason = "the update reason";

		List<AffectedPart> affectedParts = List.of(new AffectedPart("partId"));
		Notification notification = new Notification(
			"123",
			"id123",
			"senderBPN",
			"recipientBPN",
			"senderAddress",
			"agreement",
			"information",
			InvestigationStatus.CREATED,
			affectedParts,
			Instant.now(),
			Severity.MINOR
		);

		List<Notification> notifications = new ArrayList<>();
		notifications.add(notification);

		Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(InvestigationStatus.RECEIVED, "recipientBPN", notifications);

		when(mockReadService.loadInvestigation(any(InvestigationId.class))).thenReturn(investigationTestData);

		// When
		assertThrows(InvestigationReceiverBpnMismatchException.class, () -> {
			service.updateInvestigationPublisher(bpn, investigationIdRaw, status, reason);
		});

		// Then
		Mockito.verify(mockRepository, never()).update(investigationTestData);
		Mockito.verify(mockNotificationsService, never()).updateAsync(any(Notification.class));
	}

}

