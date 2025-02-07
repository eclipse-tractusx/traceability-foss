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
package org.eclipse.tractusx.traceability.notification.domain.base.model;

import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationMessageTest {

    @ParameterizedTest
    @CsvSource({
            "SENT, ALERT, https://w3id.org/catenax/taxonomy#ReceiveQualityAlertNotification",
            "ACKNOWLEDGED, ALERT, https://w3id.org/catenax/taxonomy#UpdateQualityAlertNotification",
            "SENT, INVESTIGATION, https://w3id.org/catenax/taxonomy#ReceiveQualityInvestigationNotification",
            "ACKNOWLEDGED, INVESTIGATION, https://w3id.org/catenax/taxonomy#UpdateQualityInvestigationNotification"
    })
    void testGetTaxoValue(String notificationStatus, String notificationType, String expectedTaxoValue) {
        // Given
        NotificationMessage notificationTestData = NotificationTestDataFactory.createNotificationTestData();
        notificationTestData.setNotificationStatus(NotificationStatus.valueOf(notificationStatus));
        notificationTestData.setType(NotificationType.valueOf(notificationType));

        // When
        String result = notificationTestData.getTaxoValue();

        // Then
        assertEquals(expectedTaxoValue, result);
    }
}
