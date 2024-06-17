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
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class NotificationMessageMapperTest {

    @InjectMocks
    private NotificationMapper mapper;

    @Test
    void testToReceiverInvestigation() {
        // Given
        String sender = "BPNL000000000001";
        String receiver = "BPNL000000000002";
        String description = "Test investigation";
        NotificationMessage notification = NotificationMessage.builder()
                .id("1")
                .notificationReferenceId("Test notification")
                .notificationStatus(NotificationStatus.RECEIVED)
                .affectedParts(List.of(new NotificationAffectedPart("123")))
                .sentByName("senderManufacturerName")
                .sentBy(sender)
                .sentTo(receiver)
                .sendToName("receiverManufacturerName")
                .severity(NotificationSeverity.MINOR)
                .messageId("1")
                .build();
        NotificationType type = NotificationType.INVESTIGATION;


        // When
        Notification result = mapper.toNotification(new BPN(receiver), description, notification, type);

        // Then
        assertEquals(NotificationStatus.RECEIVED, result.getNotificationStatus());
        assertEquals(NotificationSide.RECEIVER, result.getNotificationSide());
        assertEquals(description, result.getDescription());
        assertEquals(List.of("123"), result.getAffectedPartIds());
        assertEquals(List.of(notification), result.getNotifications());
        assertEquals(NotificationType.INVESTIGATION, result.getNotificationType());
    }
}


