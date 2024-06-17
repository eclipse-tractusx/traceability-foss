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

import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.service.DiscoveryService;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.ContractNegotiationException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.NoCatalogItemException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.NoEndpointDataReferenceException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.base.service.EdcNotificationServiceImpl;
import org.eclipse.tractusx.traceability.notification.domain.base.service.NotificationsEDCFacade;
import org.eclipse.tractusx.traceability.notification.domain.notification.repository.NotificationRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EdcNotificationServiceImplTest {

    @InjectMocks
    private EdcNotificationServiceImpl notificationsService;

    @Mock
    private NotificationsEDCFacade edcFacade;

    @Mock
    private DiscoveryService discoveryService;

    @Mock
    private NotificationRepository notificationRepository;


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
        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.INVESTIGATION)
                .build();

        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
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
        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.ALERT)
                .build();

        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
    }

    @Test
    void givenNoCatalogItemException_whenHandleSendingInvestigation_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.INVESTIGATION)
                .build();
        doThrow(new NoCatalogItemException()).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);
        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
    }

    @Test
    void givenSendNotificationException_whenHandleSendingInvestigation_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.INVESTIGATION)
                .build();
        doThrow(new SendNotificationException("message", new RuntimeException())).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);
        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
    }

    @Test
    void givenSendNoEndpointDataReferenceException_whenHandleSendingInvestigation_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.INVESTIGATION)
                .build();
        doThrow(new NoEndpointDataReferenceException("message")).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
    }

    @Test
    void givenContractNegotiationException_whenHandleSendingInvestigation_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.INVESTIGATION)
                .build();
        doThrow(new ContractNegotiationException("message")).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
    }


    @Test
    void givenNoCatalogItemException_whenHandleSendingAlert_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.ALERT)
                .build();
        doThrow(new NoCatalogItemException()).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
    }

    @Test
    void givenSendNotificationException_whenHandleSendingAlert_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);

        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.ALERT)
                .build();
        doThrow(new SendNotificationException("message", new RuntimeException())).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);


        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
    }

    @Test
    void givenSendNoEndpointDataReferenceException_whenHandleSendingAlert_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.ALERT)
                .build();
        doThrow(new NoEndpointDataReferenceException("message")).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
    }

    @Test
    void givenContractNegotiationException_whenHandleSendingAlert_thenHandleIt() {
        // given
        String bpn = "BPN1234";
        String edcReceiverUrl = "https://not-real-edc-receiver-url.com";
        String edcSenderUrl = "https://not-real-edc-sender-url.com";

        Discovery discovery = Discovery.builder().senderUrl(edcSenderUrl).receiverUrls(List.of(edcReceiverUrl)).build();
        when(discoveryService.getDiscoveryByBPN(bpn)).thenReturn(discovery);
        NotificationMessage notification = NotificationMessage.builder()
                .sentTo(bpn)
                .type(NotificationType.ALERT)
                .build();
        doThrow(new ContractNegotiationException("message")).when(edcFacade).startEdcTransfer(notification, edcReceiverUrl, edcSenderUrl);

        // when
        notificationsService.asyncNotificationMessageExecutor(notification);

        // then
        verify(edcFacade).startEdcTransfer(any(NotificationMessage.class), eq(edcReceiverUrl), eq(edcSenderUrl));
    }
}
