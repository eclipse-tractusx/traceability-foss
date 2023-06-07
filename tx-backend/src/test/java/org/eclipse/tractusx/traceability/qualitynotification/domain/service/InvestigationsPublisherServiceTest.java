/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.qualitynotification.domain.service;

import org.eclipse.tractusx.traceability.assets.domain.service.repository.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.service.InvestigationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service.InvestigationsPublisherService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvestigationsPublisherServiceTest {
    @InjectMocks
    private InvestigationsPublisherService investigationsPublisherService;

    @Mock
    private InvestigationRepository repository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private AssetService assetsService;
    @Mock
    private Clock clock;
    @Mock
    private InvestigationService investigationService;
    @Mock
    private EdcNotificationService notificationsService;
    @Mock
    private BpnRepository bpnRepository;
    @Mock
    private TraceabilityProperties traceabilityProperties;

    @Test
    void testStartInvestigationSuccessful() {
        // Given
        QualityNotification investigation = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, "bpn123");
        when(assetRepository.getAssetsById(Arrays.asList("asset-1", "asset-2"))).thenReturn(List.of(AssetTestDataFactory.createAssetTestData()));
        when(repository.saveQualityNotificationEntity(any(QualityNotification.class))).thenReturn(investigation.getNotificationId());
        when(bpnRepository.findManufacturerName(anyString())).thenReturn(Optional.empty());
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("bpn-123"));
        // When
        investigationsPublisherService.startInvestigation(Arrays.asList("asset-1", "asset-2"), "Test investigation", Instant.parse("2022-03-01T12:00:00Z"), QualityNotificationSeverity.MINOR);

        // Then
        verify(assetRepository).getAssetsById(Arrays.asList("asset-1", "asset-2"));
        verify(repository).saveQualityNotificationEntity(any(QualityNotification.class));

    }

    @Test
    void testCancelInvestigationSuccessful() {
        // Given
        BPN bpn = new BPN("bpn123");
        Long id = 1L;
        QualityNotification investigation = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.CREATED, QualityNotificationStatus.CREATED);
        when(repository.updateQualityNotificationEntity(investigation)).thenReturn(new QualityNotificationId(id));
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        // When
        investigationsPublisherService.cancelInvestigation(investigation);

        // Then
        verify(repository).updateQualityNotificationEntity(investigation);
        assertEquals(QualityNotificationStatus.CANCELED, investigation.getNotificationStatus());
    }

    @Test
    void testSendInvestigationSuccessful() {
        // Given
        final BPN bpn = new BPN("bpn123");
        QualityNotificationId investigationId = new QualityNotificationId(1L);
        QualityNotification investigation = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.CREATED, QualityNotificationStatus.CREATED);
        when(repository.updateQualityNotificationEntity(investigation)).thenReturn(investigationId);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);

        // When
        investigationsPublisherService.approveInvestigation(investigation);

        // Then
        verify(repository).updateQualityNotificationEntity(investigation);
        verify(notificationsService).asyncNotificationExecutor(any());
    }

    @Test
    @DisplayName("Test updateInvestigation is valid")
    void testUpdateInvestigation() {

        // Given
        BPN bpn = BPN.of("senderBPN");
        QualityNotificationStatus status = QualityNotificationStatus.ACKNOWLEDGED;
        String reason = "the update reason";

        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now())
                .targetDate(Instant.now())
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        QualityNotificationMessage notification2 = QualityNotificationMessage.builder()
                .id("456")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now().plusSeconds(10))
                .targetDate(Instant.now())
                .isInitial(false)
                .build();

        List<QualityNotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);
        notifications.add(notification2);

        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(QualityNotificationStatus.RECEIVED, "recipientBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        // When
        investigationsPublisherService.updateInvestigationPublisher(investigationTestData, status, reason);

        // Then
        Mockito.verify(repository).updateQualityNotificationEntity(investigationTestData);
        Mockito.verify(notificationsService, times(1)).asyncNotificationExecutor(any(QualityNotificationMessage.class));
    }

    @Test
    @DisplayName("Test updateInvestigation accepted is valid")
    void testUpdateInvestigationAccepted() {

        // Given
        BPN bpn = BPN.of("senderBPN");
        QualityNotificationStatus status = QualityNotificationStatus.ACCEPTED;
        String reason = "the update reason";

        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now())
                .targetDate(Instant.now())
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        QualityNotificationMessage notification2 = QualityNotificationMessage.builder()
                .id("456")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now().plusSeconds(10))
                .targetDate(Instant.now())
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        List<QualityNotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);
        notifications.add(notification2);

        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(QualityNotificationStatus.ACKNOWLEDGED, "recipientBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        // When
        investigationsPublisherService.updateInvestigationPublisher(investigationTestData, status, reason);

        // Then
        Mockito.verify(repository).updateQualityNotificationEntity(investigationTestData);
        Mockito.verify(notificationsService, times(1)).asyncNotificationExecutor(any(QualityNotificationMessage.class));
    }

    @Test
    @DisplayName("Test updateInvestigation declined is valid")
    void testUpdateInvestigationDeclined() {

        // Given
        BPN bpn = BPN.of("senderBPN");
        QualityNotificationStatus status = QualityNotificationStatus.DECLINED;
        String reason = "the update reason";

        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now())
                .targetDate(Instant.now())
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        QualityNotificationMessage notification2 = QualityNotificationMessage.builder()
                .id("456")
                .notificationReferenceId("id123")
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .created(LocalDateTime.now().plusSeconds(10))
                .targetDate(Instant.now())
                .build();

        List<QualityNotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);
        notifications.add(notification2);

        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(QualityNotificationStatus.ACKNOWLEDGED, "recipientBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);

        // When
        investigationsPublisherService.updateInvestigationPublisher(investigationTestData, status, reason);

        // Then
        Mockito.verify(repository).updateQualityNotificationEntity(investigationTestData);
        Mockito.verify(notificationsService, times(1)).asyncNotificationExecutor(any(QualityNotificationMessage.class));
    }

    @Test
    @DisplayName("Test updateInvestigation close is valid")
    void testUpdateInvestigationClose() {

        // Given
        BPN bpn = BPN.of("senderBPN");
        Long investigationIdRaw = 1L;
        QualityNotificationStatus status = QualityNotificationStatus.CLOSED;
        String reason = "the update reason";

        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now())
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        QualityNotificationMessage notification2 = QualityNotificationMessage.builder()
                .id("456")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now().plusSeconds(10))
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        List<QualityNotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);
        notifications.add(notification2);

        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(QualityNotificationStatus.ACCEPTED, "senderBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        // When
        investigationsPublisherService.updateInvestigationPublisher(investigationTestData, status, reason);

        // Then
        Mockito.verify(repository).updateQualityNotificationEntity(investigationTestData);
        Mockito.verify(notificationsService, times(1)).asyncNotificationExecutor(any(QualityNotificationMessage.class));
    }

    @Test
    @DisplayName("Test updateInvestigation is invalid because investigation status transition not allowed")
    void testUpdateInvestigationInvalid() {

        // Given
        BPN bpn = BPN.of("recipientBPN");
        Long investigationIdRaw = 1L;
        QualityNotificationStatus status = QualityNotificationStatus.CREATED;
        String reason = "the update reason";

        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        List<QualityNotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);

        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(QualityNotificationStatus.SENT, "recipientBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        // When
        assertThrows(InvestigationIllegalUpdate.class, () -> investigationsPublisherService.updateInvestigationPublisher(investigationTestData, status, reason));

        // Then
        Mockito.verify(repository, never()).updateQualityNotificationEntity(investigationTestData);
        Mockito.verify(notificationsService, never()).asyncNotificationExecutor(any(QualityNotificationMessage.class));
    }

}
