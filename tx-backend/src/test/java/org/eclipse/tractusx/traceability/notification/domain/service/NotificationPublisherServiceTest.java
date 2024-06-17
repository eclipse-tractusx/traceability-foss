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

package org.eclipse.tractusx.traceability.notification.domain.service;

import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.base.model.exception.NotificationIllegalUpdate;
import org.eclipse.tractusx.traceability.notification.domain.base.service.EdcNotificationService;
import org.eclipse.tractusx.traceability.notification.domain.base.service.NotificationPublisherService;
import org.eclipse.tractusx.traceability.notification.domain.notification.model.StartNotification;
import org.eclipse.tractusx.traceability.notification.domain.notification.repository.NotificationRepository;
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
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherServiceTest {
    @InjectMocks
    private NotificationPublisherService notificationPublisherService;

    @Mock
    private NotificationRepository repository;
    @Mock
    private AssetAsBuiltRepository assetRepository;
    @Mock
    private AssetAsBuiltServiceImpl assetsService;
    @Mock
    private Clock clock;
    @Mock
    private EdcNotificationService notificationsService;
    @Mock
    private BpnRepository bpnRepository;
    @Mock
    private TraceabilityProperties traceabilityProperties;

    @Test
    void testStartInvestigationSuccessful() {
        // Given
        String title = "title";
        String description = "Test investigation";
        List<String> assets = Arrays.asList("asset-1", "asset-2");
        Instant targetDate = Instant.parse("2022-03-01T12:00:00Z");
        when(assetRepository.getAssetsById(assets)).thenReturn(List.of(AssetTestDataFactory.createAssetAsBuiltTestdata()));
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("bpn-123"));
        String receiverBpn = "someReceiverBpn";
        StartNotification startNotification = StartNotification.builder()
                .title(title)
                .affectedPartIds(assets)
                .description(description)
                .targetDate(targetDate)
                .severity(NotificationSeverity.MINOR)
                .type(NotificationType.INVESTIGATION)
                .receiverBpn(receiverBpn)
                .build();

        // When
        Notification result = notificationPublisherService.startNotification(startNotification);

        // Then
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getNotificationStatus()).isEqualTo(NotificationStatus.CREATED);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getNotificationSide()).isEqualTo(NotificationSide.SENDER);
        assertThat(result.getNotifications()).extracting("severity")
                .containsExactly(NotificationSeverity.MINOR);
        verify(assetRepository).getAssetsById(Arrays.asList("asset-1", "asset-2"));
    }

    @Test
    void testStartAlertSuccessful() {
        // Given
        String title = "Title";
        String description = "Test investigation";
        String receiverBpn = "BPN00001";
        Instant targetDate = Instant.parse("2022-03-01T12:00:00Z");
        List<String> assets = Arrays.asList("asset-1", "asset-2");
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("bpn-123"));
        when(assetRepository.getAssetsById(assets)).thenReturn(List.of(AssetTestDataFactory.createAssetAsBuiltTestdata()));
        StartNotification startNotification = StartNotification.builder()
                .title(title)
                .affectedPartIds(assets)
                .description(description)
                .targetDate(targetDate)
                .severity(NotificationSeverity.MINOR)
                .type(NotificationType.INVESTIGATION)
                .receiverBpn(receiverBpn)
                .build();
        // When
        Notification result = notificationPublisherService.startNotification(startNotification);

        // Then
        assertThat(result.getNotificationStatus()).isEqualTo(NotificationStatus.CREATED);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getNotificationSide()).isEqualTo(NotificationSide.SENDER);
        assertThat(result.getNotifications()).extracting("severity")
                .containsExactly(NotificationSeverity.MINOR);
        assertThat(result.getNotifications()).hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("sendTo", receiverBpn);
        verify(assetRepository).getAssetsById(Arrays.asList("asset-1", "asset-2"));

    }

    @Test
    void testCancelInvestigationSuccessful() {
        // Given
        BPN bpn = new BPN("bpn123");
        Notification investigation = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.CREATED, NotificationStatus.CREATED);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        // When
        Notification result = notificationPublisherService.cancelNotification(investigation);

        // Then
        assertThat(result.getNotificationStatus()).isEqualTo(NotificationStatus.CANCELED);
    }

    @Test
    void testSendInvestigationSuccessful() {
        // Given
        final BPN bpn = new BPN("bpn123");
        Notification investigation = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.CREATED, NotificationStatus.CREATED);
        NotificationMessage notificationMessage = investigation.getNotifications().stream().findFirst().get();
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        when(notificationsService.asyncNotificationMessageExecutor(any())).thenReturn(CompletableFuture.completedFuture(notificationMessage));

        // When
        Notification result = notificationPublisherService.approveNotification(investigation);

        // Then
        assertThat(result.getNotificationStatus()).isEqualTo(NotificationStatus.SENT);
        verify(notificationsService).asyncNotificationMessageExecutor(any());
    }

    @Test
    void testSendInvestigationFailed() {
        // Given
        final BPN bpn = new BPN("bpn123");
        Notification investigation = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.CREATED, NotificationStatus.CREATED);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        when(notificationsService.asyncNotificationMessageExecutor(any())).thenReturn(CompletableFuture.completedFuture(null));

        // When/Then
        assertThrows(SendNotificationException.class, () -> notificationPublisherService.approveNotification(investigation));
        verify(notificationsService).asyncNotificationMessageExecutor(any());
    }

    @Test
    @DisplayName("Test updateInvestigation is valid")
    void testUpdateInvestigation() {

        // Given
        BPN bpn = BPN.of("senderBPN");
        NotificationStatus status = NotificationStatus.ACKNOWLEDGED;
        String reason = "the update reason";

        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now())
                .notificationStatus(NotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        NotificationMessage notification2 = NotificationMessage.builder()
                .id("456")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now().plusSeconds(10))
                .notificationStatus(NotificationStatus.ACKNOWLEDGED)
                .build();

        List<NotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);
        notifications.add(notification2);

        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(NotificationStatus.RECEIVED, "recipientBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        when(notificationsService.asyncNotificationMessageExecutor(any())).thenReturn(CompletableFuture.completedFuture(notification2));

        // When
        Notification result = notificationPublisherService.updateNotificationPublisher(investigationTestData, status, reason);

        // Then
        assertThat(result.getNotificationStatus()).isEqualTo(NotificationStatus.ACKNOWLEDGED);

        Mockito.verify(notificationsService, times(1)).asyncNotificationMessageExecutor(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("Test updateInvestigation accepted is valid")
    void testUpdateInvestigationAccepted() {

        // Given
        BPN bpn = BPN.of("senderBPN");
        NotificationStatus status = NotificationStatus.ACCEPTED;
        String reason = "the update reason";

        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now())
                .notificationStatus(NotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        NotificationMessage notification2 = NotificationMessage.builder()
                .id("456")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now().plusSeconds(10))
                .notificationStatus(NotificationStatus.ACCEPTED)
                .affectedParts(affectedParts)
                .build();

        List<NotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);
        notifications.add(notification2);

        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(NotificationStatus.ACKNOWLEDGED, "recipientBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        when(notificationsService.asyncNotificationMessageExecutor(any())).thenReturn(CompletableFuture.completedFuture(notification2));

        // When
        Notification result = notificationPublisherService.updateNotificationPublisher(investigationTestData, status, reason);

        // Then
        assertThat(result.getNotificationStatus()).isEqualTo(NotificationStatus.ACCEPTED);
        Mockito.verify(notificationsService, times(1)).asyncNotificationMessageExecutor(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("Test updateInvestigation declined is valid")
    void testUpdateInvestigationDeclined() {

        // Given
        BPN bpn = BPN.of("senderBPN");
        NotificationStatus status = NotificationStatus.DECLINED;
        String reason = "the update reason";

        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now())
                .notificationStatus(NotificationStatus.ACKNOWLEDGED)
                .affectedParts(affectedParts)
                .build();

        NotificationMessage notification2 = NotificationMessage.builder()
                .id("456")
                .notificationReferenceId("id123")
                .notificationStatus(NotificationStatus.DECLINED)
                .affectedParts(affectedParts)
                .created(LocalDateTime.now().plusSeconds(10))
                .build();

        List<NotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);
        notifications.add(notification2);

        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(NotificationStatus.ACKNOWLEDGED, "recipientBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        when(notificationsService.asyncNotificationMessageExecutor(any())).thenReturn(CompletableFuture.completedFuture(notification2));

        // When
        Notification result = notificationPublisherService.updateNotificationPublisher(investigationTestData, status, reason);

        // Then
        assertThat(result.getNotificationStatus()).isEqualTo(NotificationStatus.DECLINED);
        Mockito.verify(notificationsService, times(1)).asyncNotificationMessageExecutor(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("Test updateInvestigation close is valid")
    void testUpdateInvestigationClose() {

        // Given
        BPN bpn = BPN.of("senderBPN");
        NotificationStatus status = NotificationStatus.CLOSED;
        String reason = "the update reason";

        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now())
                .notificationStatus(NotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        NotificationMessage notification2 = NotificationMessage.builder()
                .id("456")
                .notificationReferenceId("id123")
                .created(LocalDateTime.now().plusSeconds(10))
                .notificationStatus(NotificationStatus.CLOSED)
                .affectedParts(affectedParts)
                .build();

        List<NotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);
        notifications.add(notification2);

        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(NotificationStatus.ACCEPTED, "senderBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        when(notificationsService.asyncNotificationMessageExecutor(any())).thenReturn(CompletableFuture.completedFuture(notification2));

        // When
        Notification result = notificationPublisherService.updateNotificationPublisher(investigationTestData, status, reason);

        // Then
        assertThat(result.getNotificationStatus()).isEqualTo(NotificationStatus.CLOSED);
        Mockito.verify(notificationsService, times(1)).asyncNotificationMessageExecutor(any(NotificationMessage.class));
    }

    @Test
    @DisplayName("Test updateInvestigation is invalid because investigation status transition not allowed")
    void testUpdateInvestigationInvalid() {

        // Given
        BPN bpn = BPN.of("recipientBPN");
        NotificationStatus status = NotificationStatus.CREATED;
        String reason = "the update reason";

        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .notificationStatus(NotificationStatus.CREATED)
                .affectedParts(affectedParts)
                .build();

        List<NotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);

        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestDataWithNotificationList(NotificationStatus.SENT, "recipientBPN", notifications);
        when(traceabilityProperties.getBpn()).thenReturn(bpn);
        // When
        assertThrows(NotificationIllegalUpdate.class, () -> notificationPublisherService.updateNotificationPublisher(investigationTestData, status, reason));

        // Then
        Mockito.verify(repository, never()).updateNotification(investigationTestData);
        Mockito.verify(notificationsService, never()).asyncNotificationMessageExecutor(any(NotificationMessage.class));
    }

}
