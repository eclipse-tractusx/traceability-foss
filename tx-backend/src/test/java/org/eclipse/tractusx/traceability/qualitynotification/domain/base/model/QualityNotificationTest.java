/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.qualitynotification.domain.base.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class QualityNotificationTest {

    @Test
    void testSecondLatestNotifications() {
        // Given
        List<QualityNotificationMessage> notifications = new ArrayList<>();
        QualityNotificationMessage message1 = QualityNotificationMessage.builder().notificationStatus(QualityNotificationStatus.ACKNOWLEDGED).created(LocalDateTime.now()).build();
        QualityNotificationMessage message2 = QualityNotificationMessage.builder().notificationStatus(QualityNotificationStatus.ACKNOWLEDGED).created(LocalDateTime.now()).build();
        notifications.add(message1);
        notifications.add(message2);

        QualityNotification qualityNotification = QualityNotification.builder().notifications(notifications).build();

        // When
        List<QualityNotificationMessage> result = qualityNotification.secondLatestNotifications();

        // Then
        assertNotNull(result);
        assertTrue(result.contains(message1));

    }
}
