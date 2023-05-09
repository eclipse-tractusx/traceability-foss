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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.support.TestDataSupport
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationContent
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationHeader
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.NotificationType
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus
import org.springframework.beans.factory.annotation.Autowired

class EdcNotificationModelIT extends IntegrationSpecification implements TestDataSupport {

    @Autowired
    private ObjectMapper objectMapper

    def "should be able to deserialize edc notification object with unknown fields"() {
        given:
        String notificationJson = readFile("edc_notification_with_unknown_fields.json")

        when:
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class)

        then:
        edcNotification != null

        and:
        with(edcNotification) { it ->
            it.header().notificationId() == "cda2d956-fa91-4a75-bb4a-8e5ba39b268a"
            it.header().senderBPN() == "BPNL00000003AXS3"
            it.header().senderAddress() == "https://some-url.com"
            it.header().recipientBPN() == "BPNL00000003AXS3"
            it.header().classification() == "QM-Investigation"
            it.header().severity() == "CRITICAL"
            it.header().relatedNotificationId() == ""
            it.header().status() == "SENT"
            it.header().targetDate() == ""

            it.content().information() == "Some long description"

            List<String> listOfAffectedItems = it.content().listOfAffectedItems()
            listOfAffectedItems.size() == 1
            listOfAffectedItems.first() == "urn:uuid:171fed54-26aa-4848-a025-81aaca557f37"
        }

        and:
        edcNotification.convertInvestigationStatus() == InvestigationStatus.SENT
        edcNotification.convertNotificationType() == NotificationType.QMINVESTIGATION
    }

    def "should be able to serialize edc notification object"() {
        given:
        EDCNotificationHeader header = new EDCNotificationHeader(
                "cda2d956-fa91-4a75-bb4a-8e5ba39b268a",
                "BPNL00000003AXS3",
                "https://some-url.com",
                "BPNL00000003AXS3",
                "QM-Investigation",
                "CRITICAL",
                null,
                "SENT",
                "2018-11-30T18:35:24.00Z",
                "cda2d956-fa91-4a75-bb4a-8e5ba39b268a",
        )

        and:
        EDCNotificationContent content = new EDCNotificationContent(
                "Some long description",
                ["urn:uuid:171fed54-26aa-4848-a025-81aaca557f37"]
        )

        and:
        EDCNotification edcNotification = new EDCNotification(header, content)

        when:
        String edcNotificationJson = objectMapper.writeValueAsString(edcNotification)

        then:
        def json = new JsonSlurper().parseText(edcNotificationJson)
        json instanceof Map

        and:
        json["header"]["notificationId"] == header.notificationId()
        json["header"]["senderBPN"] == header.senderBPN()
        json["header"]["senderAddress"] == header.senderAddress()
        json["header"]["recipientBPN"] == header.recipientBPN()
        json["header"]["classification"] == header.classification()
        json["header"]["severity"] == header.severity()
        json["header"]["status"] == header.status()
        json["header"]["targetDate"] == header.targetDate()

        and:
        def headerObject = json["header"] as Map
        !headerObject.containsKey("relatedNotificationId")

        and:
        json["content"]["information"] == content.information()
        json["content"]["listOfAffectedItems"] == content.listOfAffectedItems()
    }
}
