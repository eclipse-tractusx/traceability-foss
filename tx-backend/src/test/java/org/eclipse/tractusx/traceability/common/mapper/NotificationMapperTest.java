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

import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationContent;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationHeader;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

        QualityNotificationMessage expectedNotification = NotificationTestDataFactory.createNotificationTestData();

        when(bpnRepository.findManufacturerName(eq(expectedNotification.getCreatedBy()))).thenReturn(Optional.of(expectedNotification.getCreatedByName()));
        when(bpnRepository.findManufacturerName(eq(expectedNotification.getSendTo()))).thenReturn(Optional.of(expectedNotification.getSendToName()));


        QualityNotificationMessage actualNotification = notificationMapper.toNotification(edcNotification);
        assertNotNull(actualNotification.getId());
        assertEquals(expectedNotification.getNotificationReferenceId(), actualNotification.getNotificationReferenceId());
        assertEquals(expectedNotification.getCreatedBy(), actualNotification.getCreatedBy());
        assertEquals(expectedNotification.getCreatedByName(), actualNotification.getCreatedByName());
        assertEquals(expectedNotification.getSendTo(), actualNotification.getSendTo());
        assertEquals(expectedNotification.getSendToName(), actualNotification.getSendToName());
        assertEquals(expectedNotification.getEdcUrl(), actualNotification.getEdcUrl());
        assertNull(actualNotification.getContractAgreementId());
        assertEquals("information", actualNotification.getDescription());
        assertEquals(expectedNotification.getNotificationStatus(), actualNotification.getNotificationStatus());
        assertEquals(expectedNotification.getAffectedParts(), actualNotification.getAffectedParts());
        assertEquals(expectedNotification.getSeverity(), actualNotification.getSeverity());
    }
}
