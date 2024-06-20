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

package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;

import java.util.List;

public class NotificationTestDataFactory {

    public static NotificationMessage createNotificationTestData() {
        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));

        return NotificationMessage.builder()
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
                .type(NotificationType.INVESTIGATION)
                .edcNotificationId("123")
                .messageId("messageId")
                .build();
    }

    public static NotificationMessage createNotificationTestData(NotificationType notificationType) {
        List<NotificationAffectedPart> affectedParts = List.of(new NotificationAffectedPart("partId"));

        return NotificationMessage.builder()
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
                .edcNotificationId("123")
                .messageId("messageId")
                .type(notificationType)
                .build();
    }
}
