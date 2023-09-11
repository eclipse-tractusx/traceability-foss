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

package org.eclipse.tractusx.traceability.integration.edc.blackbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationContent;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationHeader;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.NotificationType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EdcNotificationModelIT extends IntegrationTestSpecification {

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void shouldBeAbleToDeserializeEdcNotificationObjectWithUnknownFields() throws IOException {
        // given
        String notificationJson = readFile("/testdata/edc_notification_with_unknown_fields.json");

        // when
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // then
        assertThat(edcNotification).isNotNull();
        assertThat(edcNotification.header().notificationId()).isEqualTo("cda2d956-fa91-4a75-bb4a-8e5ba39b268a");
        assertThat(edcNotification.header().senderBPN()).isEqualTo("BPNL00000003AXS3");
        assertThat(edcNotification.header().senderAddress()).isEqualTo("https://some-url.com");
        assertThat(edcNotification.header().recipientBPN()).isEqualTo("BPNL00000003AXS3");
        assertThat(edcNotification.header().classification()).isEqualTo("QM-Investigation");
        assertThat(edcNotification.header().severity()).isEqualTo("CRITICAL");
        assertThat(edcNotification.header().relatedNotificationId()).isEmpty();
        assertThat(edcNotification.header().status()).isEqualTo("SENT");
        assertThat(edcNotification.header().targetDate()).isEmpty();
        assertThat(edcNotification.content().information()).isEqualTo("Some long description");
        assertThat(edcNotification.content().listOfAffectedItems()).hasSize(1);
        assertThat(edcNotification.content().listOfAffectedItems().stream().findFirst().get()).contains("urn:uuid:171fed54-26aa-4848-a025-81aaca557f37");

        assertThat(edcNotification.convertNotificationStatus()).isEqualTo(QualityNotificationStatus.SENT);
        assertThat(edcNotification.convertNotificationType()).isEqualTo(NotificationType.QMINVESTIGATION);
    }

    @Test
    void shouldBeAbleToSerializeEdcNotificationObject() throws JsonProcessingException {
        // given
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
                "cda2d956-fa91-4a75-bb4a-8e5ba39b268a"
        );
        EDCNotificationContent content = new EDCNotificationContent(
                "Some long description",
                List.of("urn:uuid:171fed54-26aa-4848-a025-81aaca557f37")
        );

        EDCNotification edcNotification = new EDCNotification(header, content);

        // when
        String edcNotificationJson = objectMapper.writeValueAsString(edcNotification);
        assertThat(jsonPathRead(edcNotificationJson, "$..header.notificationId"))
                .isEqualTo(header.notificationId());
        assertThat(jsonPathRead(edcNotificationJson, "$..header.senderBPN"))
                .isEqualTo(header.senderBPN());
        assertThat(jsonPathRead(edcNotificationJson, "$..header.senderAddress"))
                .isEqualTo(header.senderAddress());
        assertThat(jsonPathRead(edcNotificationJson, "$..header.recipientBPN"))
                .isEqualTo(header.recipientBPN());
        assertThat(jsonPathRead(edcNotificationJson, "$..header.classification"))
                .isEqualTo(header.classification());
        assertThat(jsonPathRead(edcNotificationJson, "$..header.severity"))
                .isEqualTo(header.severity());
        assertThat(jsonPathRead(edcNotificationJson, "$..header.status"))
                .isEqualTo(header.status());
        assertThat(jsonPathRead(edcNotificationJson, "$..header.targetDate"))
                .isEqualTo(header.targetDate());
        assertThat(jsonPathRead(edcNotificationJson, "$..content.information"))
                .isEqualTo(content.information());
        assertThat(jsonPathReadList(edcNotificationJson, "$..content.listOfAffectedItems"))
                .containsAll(content.listOfAffectedItems());
    }

    private String jsonPathRead(final String content, final String jsonPath) {
        List<String> list = JsonPath.read(content, jsonPath);
        return list.stream().findFirst().get();
    }

    private List<String> jsonPathReadList(final String content, final String jsonPath) {
        List<List<String>> list = JsonPath.read(content, jsonPath);
        return list.stream().findFirst().get();
    }

    private String readFile(final String filePath) throws IOException {
        InputStream file = EdcNotificationModelIT.class.getResourceAsStream(filePath);
        return new String(file.readAllBytes(), StandardCharsets.UTF_8);
    }
}
