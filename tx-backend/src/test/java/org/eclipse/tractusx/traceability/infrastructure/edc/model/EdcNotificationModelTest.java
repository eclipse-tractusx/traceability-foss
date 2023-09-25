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

import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationContent;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationHeader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EdcNotificationModelTest {

    @Test
    public void testToStringEDCNotificationHeader() {

        //GIVEN
        EDCNotificationHeader header = new EDCNotificationHeader(
                "12345", "SenderBPN", "Sender\nAddress", "RecipientBPN",
                "Classification", "Severity", "Related\nNotificationId",
                "Status", "2023-09-22", "MessageId"
        );

        String expected = "EDCNotificationHeader{" +
                "notificationId='12345', " +
                "senderBPN='SenderBPN', " +
                "senderAddress='Sender Address', " +
                "recipientBPN='RecipientBPN', " +
                "classification='Classification', " +
                "severity='Severity', " +
                "relatedNotificationId='Related NotificationId', " +
                "status='Status', " +
                "targetDate='2023-09-22', " +
                "messageId='MessageId'}";

        //WHEN
        String actual = header.toString();

        //THEN
        assertEquals(expected, actual);
    }

    @Test
    public void testToStringEDCNotificationContent() {

        //GIVEN
        List<String> listOfAffectedItems = new ArrayList<>(Arrays.asList("Item1\nItem2", "Item3", "Item4\r\nItem5"));

        EDCNotificationContent content = new EDCNotificationContent(
                "Information\nwith\nline\nbreaks", listOfAffectedItems
        );

        String expected = "EDCNotificationContent{" +
                "information='Information with line breaks', " +
                "listOfAffectedItems=[Item1 Item2, Item3, Item4 Item5]" +
                "}";

        //WHEN
        String actual = content.toString();

        //THEN
        assertEquals(expected, actual);
    }
}
