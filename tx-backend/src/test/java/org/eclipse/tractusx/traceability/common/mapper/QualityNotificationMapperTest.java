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

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class QualityNotificationMapperTest {

    @InjectMocks
    private QualityNotificationMapper mapper;

    @Test
    void testToReceiverInvestigation() {
        // Given
        String sender = "BPNL000000000001";
        String receiver = "BPNL000000000002";
        String description = "Test investigation";
        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("1")
                .notificationReferenceId("Test notification")
                .notificationStatus(QualityNotificationStatus.RECEIVED)
                .affectedParts(List.of(new QualityNotificationAffectedPart("123")))
                .createdByName("senderManufacturerName")
                .createdBy(sender)
                .sendTo(receiver)
                .sendToName("receiverManufacturerName")
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .messageId("1")
                .build();


        // When
        QualityNotification result = mapper.toQualityNotification(new BPN(receiver), description, notification);

        // Then
        assertEquals(QualityNotificationStatus.RECEIVED, result.getNotificationStatus());
        assertEquals(QualityNotificationSide.RECEIVER, result.getNotificationSide());
        assertEquals(description, result.getDescription());
        assertEquals(List.of("123"), result.getAssetIds());
        assertEquals(List.of(notification), result.getNotifications());
    }
}


