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

import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMessageMapper;
import org.eclipse.tractusx.traceability.common.mapper.QualityNotificationMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service.InvestigationsReceiverService;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationFactory;
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
import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class InvestigationsReceiverServiceTest {

    @Mock
    private InvestigationRepository mockRepository;

    @Mock
    private NotificationMessageMapper mockNotificationMapper;

    @Mock
    private QualityNotificationMapper mockQualityNotificationMapper;

    @Mock
    private AssetAsBuiltServiceImpl assetService;

    @InjectMocks
    private InvestigationsReceiverService service;


    @Test
    @DisplayName("Test testHandleNotificationReceiveValidSentNotification sent is valid")
    void testHandleNotificationReceiveValidSentNotification() {

        // Given
        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .createdBy("senderBPN")
                .createdByName("senderManufacturerName")
                .sendTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description("123")
                .notificationStatus(QualityNotificationStatus.SENT)
                .affectedParts(affectedParts)
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .type(QualityNotificationType.INVESTIGATION)
                .targetDate(Instant.now())
                .messageId("messageId")
                .isInitial(true)
                .build();


        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.RECEIVED, "recipientBPN");
        QualityNotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification);

        when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
        when(mockQualityNotificationMapper.toQualityNotification(any(BPN.class), anyString(), any(QualityNotificationMessage.class))).thenReturn(investigationTestData);

        // When
        service.handleNotificationReceive(edcNotification);
        // Then
        Mockito.verify(mockRepository).saveQualityNotificationEntity(investigationTestData);
    }

    @Test
    @DisplayName("Test testHandleNotificationUpdateValidAcknowledgeNotificationTransition is valid")
    void testHandleNotificationUpdateValidAcknowledgeNotificationTransition() {

        // Given
        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));


        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .createdBy("senderBPN")
                .createdByName("senderManufacturerName")
                .sendTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description("123")
                .notificationStatus(QualityNotificationStatus.ACKNOWLEDGED)
                .affectedParts(affectedParts)
                .type(QualityNotificationType.INVESTIGATION)
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .targetDate(Instant.now())
                .messageId("messageId")
                .isInitial(false)
                .build();


        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.RECEIVED, "recipientBPN");
        QualityNotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification);

        when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
        when(mockRepository.findByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(Optional.of(investigationTestData));

        // When
        service.handleNotificationUpdate(edcNotification);
        // Then
        Mockito.verify(mockRepository).updateQualityNotificationEntity(investigationTestData);
    }

    @Test
    @DisplayName("Test testHandleNotificationUpdateValidDeclineNotificationTransition is valid")
    void testHandleNotificationUpdateValidDeclineNotificationTransition() {

        // Given
        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));

        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .createdBy("senderBPN")
                .createdByName("senderManufacturerName")
                .sendTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description("123")
                .notificationStatus(QualityNotificationStatus.DECLINED)
                .affectedParts(affectedParts)
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .type(QualityNotificationType.INVESTIGATION)
                .targetDate(Instant.now())
                .messageId("messageId")
                .isInitial(false)
                .build();

        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, "recipientBPN");
        QualityNotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification);

        when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
        when(mockRepository.findByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(Optional.of(investigationTestData));

        // When
        service.handleNotificationUpdate(edcNotification);
        // Then
        Mockito.verify(mockRepository).updateQualityNotificationEntity(investigationTestData);
    }

    @Test
    @DisplayName("Test testHandleNotificationUpdateValidAcknowledgeNotification is valid")
    void testHandleNotificationUpdateValidAcceptedNotificationTransition() {

        // Given
        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));

        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .createdBy("senderBPN")
                .createdByName("senderManufacturerName")
                .sendTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description("123")
                .notificationStatus(QualityNotificationStatus.ACCEPTED)
                .affectedParts(affectedParts)
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .type(QualityNotificationType.INVESTIGATION)
                .targetDate(Instant.now())
                .messageId("messageId")
                .isInitial(false)
                .build();

        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, "recipientBPN");
        QualityNotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification);

        when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
        when(mockRepository.findByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(Optional.of(investigationTestData));

        // When
        service.handleNotificationUpdate(edcNotification);
        // Then
        Mockito.verify(mockRepository).updateQualityNotificationEntity(investigationTestData);
    }

    @Test
    @DisplayName("Test testHandleNotificationUpdateValidAcknowledgeNotification is valid")
    void testHandleNotificationUpdateValidCloseNotificationTransition() {

        // Given
        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));

        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .createdBy("senderBPN")
                .createdByName("senderManufacturerName")
                .sendTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description("123")
                .notificationStatus(QualityNotificationStatus.CLOSED)
                .affectedParts(affectedParts)
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .type(QualityNotificationType.INVESTIGATION)
                .targetDate(Instant.now())
                .messageId("messageId")
                .isInitial(false)
                .build();

        QualityNotification investigationTestData = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACKNOWLEDGED, "senderBPN");
        QualityNotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        EDCNotification edcNotification = EDCNotificationFactory.createEdcNotification(
                "it", notification);

        when(mockNotificationMapper.toNotification(edcNotification)).thenReturn(notificationTestData);
        when(mockRepository.findByEdcNotificationId(edcNotification.getNotificationId())).thenReturn(Optional.of(investigationTestData));

        // When
        service.handleNotificationUpdate(edcNotification);
        // Then
        Mockito.verify(mockRepository).updateQualityNotificationEntity(investigationTestData);
    }

}

