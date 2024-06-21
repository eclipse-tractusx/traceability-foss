/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.common.mapper;

import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepository;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotificationContent;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotificationHeader;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationMapperTest {

    @InjectMocks
    private NotificationMessageMapper notificationMapper;

    @Mock
    private BpnRepository bpnRepository;

    @Test
    void testToReceiverNotification() {
        EDCNotificationHeader header = new EDCNotificationHeader("id123",
                "senderBPN", "senderAddress", "recipientBPN", "classification",
                "MINOR", "relatedNotificationId", "ACKNOWLEDGED", "2022-03-01T12:00:00Z", "id123");
        EDCNotificationContent content = new EDCNotificationContent("information", List.of("partId"));
        EDCNotification edcNotification = new EDCNotification(header, content);

        NotificationMessage expectedNotification = NotificationTestDataFactory.createNotificationTestData();

        when(bpnRepository.findManufacturerName(eq(expectedNotification.getSentBy()))).thenReturn(expectedNotification.getSentByName());
        when(bpnRepository.findManufacturerName(eq(expectedNotification.getSentTo()))).thenReturn(expectedNotification.getSendToName());


        NotificationMessage actualNotification = notificationMapper.toNotificationMessage(edcNotification, NotificationType.INVESTIGATION);
        assertNotNull(actualNotification.getId());
        assertEquals(expectedNotification.getNotificationReferenceId(), actualNotification.getNotificationReferenceId());
        assertEquals(expectedNotification.getSentBy(), actualNotification.getSentBy());
        assertEquals(expectedNotification.getSentByName(), actualNotification.getSentByName());
        assertEquals(expectedNotification.getSentTo(), actualNotification.getSentTo());
        assertEquals(expectedNotification.getSendToName(), actualNotification.getSendToName());
        assertNull(actualNotification.getContractAgreementId());
        assertEquals("information", actualNotification.getMessage());
        assertEquals(expectedNotification.getNotificationStatus(), actualNotification.getNotificationStatus());
        assertEquals(expectedNotification.getAffectedParts(), actualNotification.getAffectedParts());
        assertEquals(expectedNotification.getType(), actualNotification.getType());
    }
}
