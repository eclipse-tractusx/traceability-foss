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

package org.eclipse.tractusx.traceability.assets

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.IrsApiSupport

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.*

class AssetsControllerCountriesIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, BpnSupport {

    def "should return assets country map"() {
        expect:
        given()
                .header(jwtAuthorization(role))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/countries")
                .then()
                .statusCode(200)

        where:
        role << [USER, ADMIN, SUPERVISOR]
    }

    def "should not return assets country map when user is not authenticated"() {
        expect:
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/countries")
                .then()
                .statusCode(401)
    }
}
