package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;

import org.eclipse.tractusx.traceability.testdata.AssetTestDataFactory;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void testStartInvestigationSuccessful() {
        // Given
        Investigation investigation = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.ACKNOWLEDGED, InvestigationStatus.CLOSED, "bpn123");
        when(assetRepository.getAssetsById(Arrays.asList("asset-1", "asset-2"))).thenReturn(List.of(AssetTestDataFactory.createAssetTestData()));
        when(repository.save(any(Investigation.class))).thenReturn(investigation.getId());

        // When
        investigationsPublisherService.startInvestigation(BPN.of("bpn-123"), Arrays.asList("asset-1", "asset-2"), "Test investigation");

        // Then
        verify(assetRepository).getAssetsById(Arrays.asList("asset-1", "asset-2"));
        verify(repository).save(any(Investigation.class));

    }

    @Test
    void testCancelInvestigationSuccessful() {
        // Given
        BPN bpn = new BPN("bpn123");
        Long id = 1L;
        Investigation investigation = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.CREATED, InvestigationStatus.CREATED, "bpn123");
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
    void testCloseInvestigationSuccessful() {

        // Given
        final long id = 1L;
        final String reason = "TEST_REASON";
        final BPN bpn = new BPN("bpn123");
        InvestigationId investigationId = new InvestigationId(id);
        Investigation investigation = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.ACKNOWLEDGED, InvestigationStatus.RECEIVED, "bpn123");
        when(investigationsReadService.loadInvestigation(investigationId)).thenReturn(investigation);
        when(repository.update(investigation)).thenReturn(investigationId);

        // When
        investigationsPublisherService.closeInvestigation(bpn, id, reason);

        // Then
        verify(investigationsReadService).loadInvestigation(investigationId);
        verify(repository).update(investigation);
        verify(notificationsService).updateAsync(any());
    }

    @Test
    void testSendInvestigationSuccessful() {
        // Given
        final long id = 1L;
        final BPN bpn = new BPN("bpn123");
        InvestigationId investigationId = new InvestigationId(1L);
        Investigation investigation = InvestigationTestDataFactory.createInvestigationTestData(InvestigationStatus.CREATED, InvestigationStatus.CREATED, "bpn123");
        when(investigationsReadService.loadInvestigation(investigationId)).thenReturn(investigation);
        when(repository.update(investigation)).thenReturn(investigationId);

        // When
        investigationsPublisherService.sendInvestigation(bpn, id);

        // Then
        verify(investigationsReadService).loadInvestigation(investigationId);
        verify(repository).update(investigation);
        verify(notificationsService).updateAsync(any());
    }
}
