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

package org.eclipse.tractusx.traceability.integration.common.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.val;
import notification.request.NotificationSeverityRequest;
import notification.request.NotificationTypeRequest;
import notification.request.StartNotificationRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.repository.JpaNotificationRepository;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Component
@RequiredArgsConstructor
public class NotificationApiSupport {

    private final JpaNotificationRepository jpaNotificationRepository;
    private final AssetsSupport assetsSupport;
    private final ObjectMapper objectMapper;

    public int createInvestigation_withDefaultAssetsStored(Header authHeader) throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";

        assetsSupport.defaultAssetsStored();

        val request = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .type(NotificationTypeRequest.INVESTIGATION)
                .severity(NotificationSeverityRequest.MINOR)
                .build();

        // when
        Response response = given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(authHeader)
                .when()
                .post("/api/notifications")
                .then()
                .statusCode(201)
                .body("id", Matchers.isA(Number.class))
                .extract()
                .response();

        return response.path("id");

    }

    public Long defaultReceivedInvestigationStored() {
        NotificationEntity entity = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .type(NotificationTypeEntity.INVESTIGATION)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .side(NotificationSideBaseEntity.RECEIVER)
                .description("some description")
                .createdDate(Instant.now())
                .build();

        return jpaNotificationRepository.save(entity).getId();
    }

    public Long storeAlertWithStatusAndAssets(NotificationStatusBaseEntity status, List<AssetAsBuiltEntity> assetsAsBuilt) {
        return storeAlertWithStatusAndAssets(status, assetsAsBuilt, NotificationSideBaseEntity.RECEIVER);
    }

    public Long storeAlertWithStatusAndAssets(NotificationStatusBaseEntity status, List<AssetAsBuiltEntity> assetsAsBuilt, NotificationSideBaseEntity side) {
        NotificationEntity entity = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(status)
                .side(side)
                .type(NotificationTypeEntity.ALERT)
                .createdDate(Instant.now())
                .build();
        Long alertId = storedAlert(entity);
        NotificationEntity savedAlert = jpaNotificationRepository.findById(alertId).get();
        savedAlert.setAssets(assetsAsBuilt);
        jpaNotificationRepository.save(savedAlert);
        return alertId;
    }

    public Long storedAlert(NotificationEntity alert) {
        return jpaNotificationRepository.save(alert).getId();
    }

    public void assertInvestigationsSize(int size) {
        List<NotificationEntity> investigations = jpaNotificationRepository.findAll();

        assertThat(investigations).hasSize(size);
    }

    public void assertInvestigationStatus(NotificationStatus investigationStatus) {
        jpaNotificationRepository.findAll().forEach(
                investigation -> assertThat(investigation.getStatus().name()).isEqualTo(investigationStatus.name())
        );
    }
}
