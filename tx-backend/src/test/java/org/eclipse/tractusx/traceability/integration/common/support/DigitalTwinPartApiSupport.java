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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability.integration.common.support;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DigitalTwinPartApiSupport {

    private final OAuth2Support oAuth2Support;
    private static final JwtRole ROLE = JwtRole.ADMIN;

    /**
     * Executes POST request to /api/administration/digitalTwinPart endpoint
     */
    public <T> T postDigitalTwinPart(Object requestBody, int expectedStatusCode, TypeRef<T> typeRef) {
        try {
            return RestAssured
                    .given()
                    .header(oAuth2Support.jwtAuthorization(ROLE))
                    .contentType(ContentType.JSON)
                    .log().all()
                    .body(requestBody)
                    .when()
                    .post("/api/administration/digitalTwinPart")
                    .then()
                    .log().all()
                    .statusCode(expectedStatusCode)
                    .extract()
                    .body()
                    .as(typeRef);
        } catch (JoseException e) {
            throw new RuntimeException("Failed to authorize JWT for postDigitalTwinPart", e);
        }
    }

    /**
     * Executes POST request to /api/administration/digitalTwinPart/detail endpoint
     */
    public <T> T postDigitalTwinPartDetail(Object requestBody, int expectedStatusCode, Class<T> clazz) {
        try {
            return RestAssured
                    .given()
                    .header(oAuth2Support.jwtAuthorization(ROLE))
                    .contentType(ContentType.JSON)
                    .log().all()
                    .body(requestBody)
                    .when()
                    .post("/api/administration/digitalTwinPart/detail")
                    .then()
                    .log().all()
                    .statusCode(expectedStatusCode)
                    .extract()
                    .as(clazz);
        } catch (JoseException e) {
            throw new RuntimeException("Failed to authorize JWT for postDigitalTwinPartDetail", e);
        }
    }

    /**
     * Executes POST request to /api/administration/digitalTwinPart/searchable-values endpoint
     */
    public <T> T postSearchableValues(Object requestBody, int expectedStatusCode, TypeRef<T> typeRef) {
        try {
            return RestAssured
                    .given()
                    .header(oAuth2Support.jwtAuthorization(ROLE))
                    .contentType(ContentType.JSON)
                    .log().all()
                    .body(requestBody)
                    .when()
                    .post("/api/administration/digitalTwinPart/searchable-values")
                    .then()
                    .log().all()
                    .statusCode(expectedStatusCode)
                    .extract()
                    .body()
                    .as(typeRef);
        } catch (JoseException e) {
            throw new RuntimeException("Failed to authorize JWT for postSearchableValues", e);
        }
    }
}
