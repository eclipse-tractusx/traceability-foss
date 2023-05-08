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
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.InvestigationsEDCFacade;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Notification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Severity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationsRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service.NotificationsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationsServiceTest {

    @InjectMocks
    private NotificationsService notificationsService;

    @Mock
    private InvestigationsEDCFacade edcFacade;

    @Mock
    private InvestigationsRepository repository;

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
        Notification notification = new Notification(
                null,
                null,
                null,
                null,
                bpn,
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Severity.MINOR,
                null,
                null,
                null,
                null,
                false
        );

        // when
        notificationsService.asyncNotificationExecutor(notification);

        // then
        verify(edcFacade).startEDCTransfer(any(Notification.class), eq(edcReceiverUrl), eq(edcSenderUrl));
        //	verify(repository).update(any(Notification.class));
    }
}
