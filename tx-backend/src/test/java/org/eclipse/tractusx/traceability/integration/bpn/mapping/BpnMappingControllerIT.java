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

package org.eclipse.tractusx.traceability.integration.bpn.mapping;

import bpn.request.BpnMappingRequest;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.BpnEdcMappingSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.USER;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;

class BpnMappingControllerIT extends IntegrationTestSpecification {

    @Autowired
    BpnEdcMappingSupport bpnEdcMappingSupport;

    @Test
    void givenBpnMappingRequest_whenCreateBpnMapping_thenCreateIt() throws JoseException {
        // given
        BpnMappingRequest mappings = new BpnMappingRequest("BPNL00000003CSGV", "http://localhost:12345/abc");

        // when
        given()
                .contentType(ContentType.JSON)
                .body(List.of(mappings))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/bpn-config")
                .then()
                .statusCode(200);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/bpn-config")
                .then()
                .statusCode(200)
                .body("", Matchers.hasSize(1))
                .body("[0].bpn", Matchers.equalTo("BPNL00000003CSGV"))
                .body("[0].url", Matchers.equalTo("http://localhost:12345/abc"));
    }

    @Test
    void givenBpnMappingRequest_whenUpdate_thenUpdateIt() throws JoseException {
        {
            // given
            BpnMappingRequest mappings = new BpnMappingRequest("BPNL00000003TEST", "https://newurl.com");
            bpnEdcMappingSupport.defaultBpnEdcMappingStored();

            // when
            given()
                    .contentType(ContentType.JSON)
                    .body(List.of(mappings))
                    .header(oAuth2Support.jwtAuthorization(ADMIN))
                    .when()
                    .put("/api/bpn-config")
                    .then()
                    .statusCode(200);

            // then
            given()
                    .header(oAuth2Support.jwtAuthorization(ADMIN))
                    .contentType(ContentType.JSON)
                    .when()
                    .get("/api/bpn-config")
                    .then()
                    .statusCode(200)
                    .log().all()
                    .body("", Matchers.hasSize(2))
                    .body("", hasItem(allOf(
                            hasEntry("bpn", "BPNL00000003TEST"),
                            hasEntry("url", "https://newurl.com"))));
        }
    }

    @Test
    void givenBpnMappingsStored_whenDeleteOneBpn_thenDeleteOnlyOne() throws JoseException {
        // given
        bpnEdcMappingSupport.defaultBpnEdcMappingStored();

        // when
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .delete("/api/bpn-config/BPNL00000003TEST")
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/bpn-config")
                .then()
                .statusCode(200)
                .body("", Matchers.hasSize(1))
                .body("[0].bpn", Matchers.equalTo("BPNL00000002TEST"))
                .body("[0].url", Matchers.equalTo("https://test456.de"));
    }

    @Test
    void givenBpnMappingWrongBPNFormat_whenSave_thenBadRequest() throws JoseException {
        // given
        BpnMappingRequest mappings = new BpnMappingRequest("ABC", "http://localhost:12345/abc");

        // when
        given()
                .contentType(ContentType.JSON)
                .body(List.of(mappings))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/bpn-config")
                .then()
                .statusCode(400);
    }

    @Test
    void givenBadRequest_whenCreateBpnConfig_thenReturn400() throws JoseException {
        // given
        var request = """
                    [
                        "url" : "https://test.de"
                    ]
                """;
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/bpn-config")
                .then()
                .statusCode(400);
    }

    @Test
    void givenMalformedRequest_whenCreateBpnMapping_thenReturn400() throws JoseException {
        // given
        var request = """
                    [
                        "url" : "https://test.de",
                        "abc" : "de"
                    ]
                """;
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/bpn-config")
                .then()
                .statusCode(400);
    }

    @Test
    void givenUserRole_whenCreateBpnMapping_thenReturn403() throws JoseException {
        // given
        BpnMappingRequest request = new BpnMappingRequest("BPNL00000003CSGF", "https://newurl.com");

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(List.of(request))
                .header(oAuth2Support.jwtAuthorization(USER))
                .when()
                .post("/api/bpn-config")
                .then()
                .statusCode(403);
    }
}
