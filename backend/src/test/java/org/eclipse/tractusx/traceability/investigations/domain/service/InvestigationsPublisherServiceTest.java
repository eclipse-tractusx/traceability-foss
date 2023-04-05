package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.investigations.domain.model.*;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.eclipse.tractusx.traceability.testdata.AssetTestDataFactory;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestigationsPublisherServiceTest {
    @InjectMocks
    private InvestigationsPublisherService investigationsPublisherService;

    @Mock
    private InvestigationsRepository repository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private Clock clock;
    @Mock
    private InvestigationsReadService investigationsReadService;
    @Mock
    private NotificationsService notificationsService;

    @Mock
    private BpnRepository bpnRepository;

    @Test
    void testStartInvestigationSuccessful() {
        // Given
        Investigation investigation = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.ACKNOWLEDGED, InvestigationStatus.CLOSED, "bpn123");
        when(assetRepository.getAssetsById(Arrays.asList("asset-1", "asset-2"))).thenReturn(List.of(AssetTestDataFactory.createAssetTestData()));
        when(repository.save(any(Investigation.class))).thenReturn(investigation.getId());
        when(bpnRepository.findManufacturerName(anyString())).thenReturn(Optional.empty());

        // When
        investigationsPublisherService.startInvestigation(
			BPN.of("bpn-123"),
			Arrays.asList("asset-1", "asset-2"), "Test investigation", Instant.parse("2022-03-01T12:00:00Z"), Severity.MINOR);

        // Then
        verify(assetRepository).getAssetsById(Arrays.asList("asset-1", "asset-2"));
        verify(repository).save(any(Investigation.class));

    }

	@Test
	void testCancelInvestigationSuccessful() {
		// Given
		BPN bpn = new BPN("bpn123");
		Long id = 1L;
		Investigation investigation = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.CREATED, InvestigationStatus.CREATED);
		when(investigationsReadService.loadInvestigation(any())).thenReturn(investigation);
		when(repository.update(investigation)).thenReturn(new InvestigationId(id));

		// When
		investigationsPublisherService.cancelInvestigation(bpn, id);

		// Then
		verify(investigationsReadService).loadInvestigation(new InvestigationId(id));
		verify(repository).update(investigation);
		assertEquals(InvestigationStatus.CANCELED, investigation.getInvestigationStatus());
	}

	@Test
	void testSendInvestigationSuccessful() {
		// Given
		final long id = 1L;
		final BPN bpn = new BPN("bpn123");
		InvestigationId investigationId = new InvestigationId(1L);
		Investigation investigation = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.CREATED, InvestigationStatus.CREATED);
		when(investigationsReadService.loadInvestigation(investigationId)).thenReturn(investigation);
		when(repository.update(investigation)).thenReturn(investigationId);

		// When
		investigationsPublisherService.approveInvestigation(bpn, id);

		// Then
		verify(investigationsReadService).loadInvestigation(investigationId);
		verify(repository).update(investigation);
		verify(notificationsService).updateAsync(any());
	}

	@Test
	@DisplayName("Test updateInvestigation is valid")
	void testUpdateInvestigation() {

		// Given
		BPN bpn = BPN.of("senderBPN");
		Long investigationIdRaw = 1L;
		InvestigationStatus status = InvestigationStatus.ACKNOWLEDGED;
		String reason = "the update reason";

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
                InvestigationStatus.CREATED,
                affectedParts,
                Instant.now(),
                Severity.MINOR,
                "123",
                LocalDateTime.now(),
                null
        );

		Notification notification2 = new Notification(
                "456",
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
                LocalDateTime.now().plusSeconds(10),
                null
		);
		List<Notification> notifications = new ArrayList<>();
		notifications.add(notification);
		notifications.add(notification2);

		Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(InvestigationStatus.RECEIVED, "recipientBPN", notifications);

		when(investigationsReadService.loadInvestigation(any(InvestigationId.class))).thenReturn(investigationTestData);

		// When
		investigationsPublisherService.updateInvestigationPublisher(bpn, investigationIdRaw, status, reason);

		// Then
		Mockito.verify(repository).update(investigationTestData);
		Mockito.verify(notificationsService, times(1)).updateAsync(any(Notification.class));
	}

	@Test
	@DisplayName("Test updateInvestigation is invalid because investigation status transition not allowed")
	void testUpdateInvestigationInvalid() {

		// Given
		BPN bpn = BPN.of("recipientBPN");
		Long investigationIdRaw = 1L;
		InvestigationStatus status = InvestigationStatus.CREATED;
		String reason = "the update reason";

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
                InvestigationStatus.CREATED,
                affectedParts,
                Instant.now(),
                Severity.MINOR,
                "123",
                null,
                null
		);

		List<Notification> notifications = new ArrayList<>();
		notifications.add(notification);

		Investigation investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(InvestigationStatus.SENT, "recipientBPN", notifications);

		when(investigationsReadService.loadInvestigation(any(InvestigationId.class))).thenReturn(investigationTestData);

		// When
		assertThrows(InvestigationIllegalUpdate.class, () -> investigationsPublisherService.updateInvestigationPublisher(bpn, investigationIdRaw, status, reason));

		// Then
		Mockito.verify(repository, never()).update(investigationTestData);
		Mockito.verify(notificationsService, never()).updateAsync(any(Notification.class));
	}

}
