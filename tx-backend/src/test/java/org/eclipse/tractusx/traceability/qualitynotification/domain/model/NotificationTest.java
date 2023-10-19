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
package org.eclipse.tractusx.traceability.qualitynotification.domain.model;

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void testCopyAndSwitchSenderAndReceiverIsAppBpn() {
        // Given
        QualityNotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();

        BPN applicationBPN = new BPN("recipientBPN");
        String senderBPN = "senderBPN";
        String receiverBPN = applicationBPN.value();
        // When
        QualityNotificationMessage switchedNotification = notificationTestData.copyAndSwitchSenderAndReceiver(applicationBPN);

        // Then
        assertThat(switchedNotification.getSendTo()).isEqualTo(senderBPN);
        assertThat(switchedNotification.getCreatedBy()).isEqualTo(receiverBPN);
    }

    @Test
    void testCopyAndSwitchSenderAndReceiverIsNotAppBpn() {
        // Given
        QualityNotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();

        BPN applicationBPN = new BPN("senderBPN");
        String senderBPN = applicationBPN.value();
        String receiverBPN = "recipientBPN";

        // When
        QualityNotificationMessage switchedNotification = notificationTestData.copyAndSwitchSenderAndReceiver(applicationBPN);

        // Then
        assertThat(switchedNotification.getSendTo()).isEqualTo(receiverBPN);
        assertThat(switchedNotification.getCreatedBy()).isEqualTo(senderBPN);
    }
}
