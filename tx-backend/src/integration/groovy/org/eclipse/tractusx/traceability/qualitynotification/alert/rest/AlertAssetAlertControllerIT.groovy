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

package org.eclipse.tractusx.traceability.qualitynotification.alert.rest

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.common.support.NotificationsSupport
import org.eclipse.tractusx.traceability.qualitynotification.application.alert.service.AlertService
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.spockframework.spring.SpringBean
import org.springframework.http.HttpStatus

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN

@ExtendWith(MockitoExtension.class)
class AlertAssetAlertControllerIT extends IntegrationSpecification implements InvestigationsSupport, NotificationsSupport, BpnSupport {

    @SpringBean
    protected
    AlertService alertService = Mock()


    def "alert asset should return bad request when non existent severity is provided"() {
        given:
        String requestWithNotValidSeverity = asJson(
                [
                        partIds    : [
                                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"
                        ],
                        description: "very bad things happened",
                        targetDate : "2099-03-11T22:44:06.333826952Z",
                        severity   : "nonExistentSeverity",
                ]
        )

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(requestWithNotValidSeverity)
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    def "alert asset should return  when non existent severity is provided"() {
        given:
        String requestWithNotValidSeverity = asJson(
                [
                        partIds    : [
                                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"
                        ],
                        description: "very bad things happened",
                        targetDate : "2099-03-11T22:44:06.333826952Z",
                        severity   : "$severity",
                ]
        )
        1 * alertService.start(_, _, _, _) >> new QualityNotificationId(1L)

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(requestWithNotValidSeverity)
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(HttpStatus.CREATED.value())

        where:
        severity << ["MINOR", "MAJOR", "CRITICAL", "LIFE-THREATENING"]

    }

}
