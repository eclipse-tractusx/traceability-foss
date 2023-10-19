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
import qualitynotification.base.request.CloseQualityNotificationRequest;
import qualitynotification.base.request.QualityNotificationSeverityRequest;
import qualitynotification.base.request.StartQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationStatusRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;
import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitizeHtml;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

    @Test
    public void testSanitizeStartQualityNotificationRequest() {
        //GIVEN
        List<String> partIds = new ArrayList<>();
        partIds.add("urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978");
        partIds.add("urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca979\n");
        Instant targetDate = Instant.parse("2023-09-22T14:30:00Z".trim());
        QualityNotificationSeverityRequest severity = QualityNotificationSeverityRequest.MINOR;
        StartQualityNotificationRequest request = new StartQualityNotificationRequest(partIds, "The description\n", targetDate, severity, true, "BPN00001123123AS\n");


        //WHEN
        StartQualityNotificationRequest cleanRequest = sanitize(request);

        //THEN
        assertEquals("urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca979 ", cleanRequest.getPartIds().get(1));
        assertEquals("The description ", cleanRequest.getDescription());
        assertTrue(cleanRequest.isAsBuilt());
        assertEquals("BPN00001123123AS ", cleanRequest.getReceiverBpn());

    }

    @Test
    public void testSanitizeCloseInvestigationRequest() {
        //GIVEN
        CloseQualityNotificationRequest closeQualityNotificationRequest = new CloseQualityNotificationRequest();
        closeQualityNotificationRequest.setReason("Reason\n");

        //WHEN
        CloseQualityNotificationRequest cleanCloseQualityNotificationRequest = sanitize(closeQualityNotificationRequest);

        //THEN
        assertEquals("Reason ", cleanCloseQualityNotificationRequest.getReason());

    }


    @Test
    public void testSanitizeUpdateQualityNotificationRequest() {
        //GIVEN
        UpdateQualityNotificationRequest updateQualityNotificationRequest = new UpdateQualityNotificationRequest();
        updateQualityNotificationRequest.setReason("Reason\n");
        updateQualityNotificationRequest.setStatus(UpdateQualityNotificationStatusRequest.ACCEPTED);

        //WHEN
        UpdateQualityNotificationRequest cleanUpdateQualityNotificationRequest = sanitize(updateQualityNotificationRequest);

        //THEN
        assertEquals("Reason ", cleanUpdateQualityNotificationRequest.getReason());
    }

    @Test
    public void testSanitizeHtml() {
        //GIVEN
        String html = "\n<oohlook&atme>";

        //WHEN
        String stringWithoutLineBreaks = sanitize(html);
        String cleanString = sanitizeHtml(stringWithoutLineBreaks);

        //THEN
        assertEquals(" &lt;oohlook&amp;atme&gt;", cleanString);
    }
}
