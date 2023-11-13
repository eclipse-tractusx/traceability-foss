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

import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.service.DiscoveryService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.ContractNegotiationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.NoCatalogItemException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.NoEndpointDataReferenceException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.EdcNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.NotificationsEDCFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcNotificationServiceTest {

    @InjectMocks
    private EdcNotificationService notificationsService;

    @Mock
    private NotificationsEDCFacade edcFacade;

    @Mock
    private InvestigationRepository investigationRepository;

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private DiscoveryService discoveryService;

    @Test
    void testNotificationsServiceUpdateAsync() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        // and
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        // and
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.INVESTIGATION)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verify(investigationRepository).updateQualityNotificationMessageEntity(notification);
        verifyNoInteractions(alertRepository);
    }

    @Test
    void testNotificationsServiceAlertNotificationUpdateAsync() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        // and
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        // and
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.ALERT)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verify(alertRepository).updateQualityNotificationMessageEntity(notification);
        verifyNoInteractions(investigationRepository);
    }

    @Test
    void givenNoCatalogItemException_whenHandleSendingInvestigation_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.INVESTIGATION)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();
        doThrow(new NoCatalogItemException()).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verifyNoInteractions(alertRepository);
        verifyNoInteractions(investigationRepository);
    }

    @Test
    void givenSendNotificationException_whenHandleSendingInvestigation_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.INVESTIGATION)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();
        doThrow(new SendNotificationException("message",new RuntimeException())).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verifyNoInteractions(alertRepository);
        verifyNoInteractions(investigationRepository);
    }

    @Test
    void givenSendNoEndpointDataReferenceException_whenHandleSendingInvestigation_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.INVESTIGATION)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();
        doThrow(new NoEndpointDataReferenceException("message")).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verifyNoInteractions(alertRepository);
        verifyNoInteractions(investigationRepository);
    }

    @Test
    void givenContractNegotiationException_whenHandleSendingInvestigation_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.INVESTIGATION)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();
        doThrow(new ContractNegotiationException("message")).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verifyNoInteractions(alertRepository);
        verifyNoInteractions(investigationRepository);
    }


    @Test
    void givenNoCatalogItemException_whenHandleSendingAlert_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.ALERT)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();
        doThrow(new NoCatalogItemException()).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verifyNoInteractions(alertRepository);
        verifyNoInteractions(investigationRepository);
    }

    @Test
    void givenSendNotificationException_whenHandleSendingAlert_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.ALERT)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();
        doThrow(new SendNotificationException("message",new RuntimeException())).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verifyNoInteractions(alertRepository);
        verifyNoInteractions(investigationRepository);
    }

    @Test
    void givenSendNoEndpointDataReferenceException_whenHandleSendingAlert_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.ALERT)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();
        doThrow(new NoEndpointDataReferenceException("message")).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verifyNoInteractions(alertRepository);
        verifyNoInteractions(investigationRepository);
    }

    @Test
    void givenContractNegotiationException_whenHandleSendingAlert_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .sendTo(bpn)
                .type(QualityNotificationType.ALERT)
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .build();
        doThrow(new ContractNegotiationException("message")).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(QualityNotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        verifyNoInteractions(alertRepository);
        verifyNoInteractions(investigationRepository);
    }
}
