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

import org.eclipse.tractusx.traceability.common.mapper.NotificationMapper;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMessageMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.notification.repository.NotificationRepository;
import org.eclipse.tractusx.traceability.notification.domain.notification.service.NotificationReceiverService;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotificationFactory;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class InvestigationsReceiverServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMessageMapper mockNotificationMessageMapper;

    @Mock
    private NotificationMapper mockNotificationMapper;


    @InjectMocks
    private NotificationReceiverService service;


    @Test
    @DisplayName("Test testhandleReceiveValidSentNotification sent is valid")
    void testhandleReceiveValidSentNotification() {

        // Given
        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));
        NotificationType notificationType = NotificationType.INVESTIGATION;
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .sentBy("senderBPN")
                .sentByName("senderManufacturerName")
                .sentTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .contractAgreementId("agreement")
                .message("123")
                .notificationStatus(NotificationStatus.SENT)
                .affectedParts(affectedParts)
                .edcNotificationId("123")
                .type(notificationType)
                .messageId("messageId")
                .build();


        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.RECEIVED, "recipientBPN");
        NotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification, investigationTestData);

        when(mockNotificationMessageMapper.toNotificationMessage(edcNotification, notificationType)).thenReturn(notificationTestData);
        when(mockNotificationMapper.toNotification(any(BPN.class), any(), any(NotificationMessage.class), any(NotificationType.class))).thenReturn(investigationTestData);

        // When
        service.handleReceive(edcNotification, notificationType);
        // Then
        Mockito.verify(notificationRepository).saveNotification(investigationTestData);
    }

    @Test
    @DisplayName("Test testHandleNotificationUpdateValidAcknowledgeNotificationTransition is valid")
    void testHandleNotificationUpdateValidAcknowledgeNotificationTransition() {

        // Given
        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));

        NotificationType notificationType = NotificationType.INVESTIGATION;
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .sentBy("senderBPN")
                .sentByName("senderManufacturerName")
                .sentTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .contractAgreementId("agreement")
                .message("123")
                .notificationStatus(NotificationStatus.ACKNOWLEDGED)
                .affectedParts(affectedParts)
                .type(notificationType)
                .edcNotificationId("123")
                .messageId("messageId")
                .build();


        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.RECEIVED, "recipientBPN");
        NotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification, investigationTestData);

        when(mockNotificationMessageMapper.toNotificationMessage(edcNotification, notificationType)).thenReturn(notificationTestData);
        when(notificationRepository.findByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(Optional.of(investigationTestData));

        // When
        service.handleUpdate(edcNotification, notificationType);
        // Then
        Mockito.verify(notificationRepository).updateNotification(investigationTestData);
    }

    @Test
    @DisplayName("Test testhandleUpdateValidDeclineNotificationTransition is valid")
    void testhandleUpdateValidDeclineNotificationTransition() {

        // Given
        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));
        NotificationType notificationType = NotificationType.INVESTIGATION;
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .sentBy("senderBPN")
                .sentByName("senderManufacturerName")
                .sentTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .contractAgreementId("agreement")
                .message("123")
                .notificationStatus(NotificationStatus.DECLINED)
                .affectedParts(affectedParts)
                .edcNotificationId("123")
                .type(notificationType)
                .messageId("messageId")
                .build();

        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.ACKNOWLEDGED, "recipientBPN");
        NotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification, investigationTestData);

        when(mockNotificationMessageMapper.toNotificationMessage(edcNotification, notificationType)).thenReturn(notificationTestData);
        when(notificationRepository.findByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(Optional.of(investigationTestData));

        // When
        service.handleUpdate(edcNotification, notificationType);
        // Then
        Mockito.verify(notificationRepository).updateNotification(investigationTestData);
    }

    @Test
    @DisplayName("Test testhandleUpdateValidAcknowledgeNotification is valid")
    void testhandleUpdateValidAcceptedNotificationTransition() {

        // Given
        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));
        NotificationType notificationType = NotificationType.INVESTIGATION;
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .sentBy("senderBPN")
                .sentByName("senderManufacturerName")
                .sentTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .contractAgreementId("agreement")
                .message("123")
                .notificationStatus(NotificationStatus.ACCEPTED)
                .affectedParts(affectedParts)
                .edcNotificationId("123")
                .type(notificationType)
                .messageId("messageId")
                .build();

        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.ACKNOWLEDGED, "recipientBPN");
        NotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification, investigationTestData);

        when(mockNotificationMessageMapper.toNotificationMessage(edcNotification, notificationType)).thenReturn(notificationTestData);
        when(notificationRepository.findByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(Optional.of(investigationTestData));

        // When
        service.handleUpdate(edcNotification, notificationType);
        // Then
        Mockito.verify(notificationRepository).updateNotification(investigationTestData);
    }

    @Test
    @DisplayName("Test testhandleUpdateValidAcknowledgeNotification is valid")
    void testhandleUpdateValidCloseNotificationTransition() {

        // Given
        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));
        NotificationType notificationType = NotificationType.INVESTIGATION;
        NotificationMessage notification = NotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .sentBy("senderBPN")
                .sentByName("senderManufacturerName")
                .sentTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .contractAgreementId("agreement")
                .message("123")
                .notificationStatus(NotificationStatus.CLOSED)
                .affectedParts(affectedParts)
                .edcNotificationId("123")
                .type(notificationType)
                .messageId("messageId")
                .build();

        Notification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.ACKNOWLEDGED, "senderBPN");
        NotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification, investigationTestData);

        when(mockNotificationMessageMapper.toNotificationMessage(edcNotification, notificationType)).thenReturn(notificationTestData);
        when(notificationRepository.findByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(Optional.of(investigationTestData));

        // When
        service.handleUpdate(edcNotification, notificationType);
        // Then
        Mockito.verify(notificationRepository).updateNotification(investigationTestData);
    }

}

