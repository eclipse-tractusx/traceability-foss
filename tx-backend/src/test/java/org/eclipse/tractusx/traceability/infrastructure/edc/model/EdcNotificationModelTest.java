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
package org.eclipse.tractusx.traceability.infrastructure.edc.model;

import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationContent;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationHeader;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class EdcNotificationModelTest {


    @Test
    public void testSanitizeEDCNotification() {

        //GIVEN
        EDCNotificationHeader header = new EDCNotificationHeader(
                "12345", "SenderBPN", "Sender\nAddress", "RecipientBPN",
                "QM-Investigation", "Severity", "Related\nNotificationId",
                "CREATED", "2023-09-22T14:30:00Z", "MessageId"
        );

        List<String> listOfAffectedItems = new ArrayList<>(Arrays.asList("Item1\nItem2", "Item3", "Item4\r\nItem5"));

        EDCNotificationContent content = new EDCNotificationContent(
                "Information\nwith\nline\nbreaks", listOfAffectedItems
        );

        EDCNotification edcNotification = new EDCNotification(header, content);


        //WHEN
        EDCNotification actual = sanitize(edcNotification);

        //THEN
        assertEquals("Sender Address", actual.getSenderAddress());
        assertEquals("12345", actual.getNotificationId());
        assertEquals("Related NotificationId", actual.getRelatedNotificationId());
        assertEquals("Severity", actual.getSeverity());
        assertEquals("QM-Investigation", actual.convertNotificationType().getValue());
        assertEquals("CREATED", actual.convertNotificationStatus().name());
        assertEquals(Instant.parse("2023-09-22T14:30:00Z"), actual.getTargetDate());


    }
}
