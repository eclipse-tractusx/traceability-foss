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

package org.eclipse.tractusx.traceability.integration.importdata;

import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;

class ImportControllerIT extends IntegrationTestSpecification {

    @Test
    void givenValidFile_whenImportData_thenValidationShouldPass() throws JoseException {
        // given
        String path = getClass().getResource("/testdata/importfiles/validImportFile.json").getFile();
        File file = new File(path);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(204);
    }

    @Test
    void givenInvalidFile_whenImportData_thenValidationShouldNotPass() throws JoseException {
        // given
        String path = getClass().getResource("/testdata/importfiles/invalidImportFile.json").getFile();
        File file = new File(path);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(400)
                .body("validationErrors", Matchers.contains(
                        List.of(
                                "Did not match pattern: ^urn:uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                                "Missing property aspectType",
                                "Missing property localIdentifiers",
                                "Missing property aspectType",
                                "Missing property catenaXId",
                                "Missing property childItems"
                        ).toArray()));
    }

    @Test
    void givenInvalidFileExtension_whenImportData_thenValidationShouldPass() throws JoseException {
        // given
        String path = getClass().getResource("/testdata/importfiles/invalidExtensionFile.xml").getFile();
        File file = new File(path);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(400)
                .body("validationErrors", Matchers.contains(
                        List.of(
                                "Supported file is *.json"
                        ).toArray()));
    }

    @Test
    void givenInvalidAspect_whenImportData_thenValidationShouldPass() throws JoseException {
        // given
        String path = getClass().getResource("/testdata/importfiles/invalidImportFile-notSupportedAspect.json").getFile();
        File file = new File(path);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(400)
                .body("validationErrors", Matchers.contains(
                        List.of(
                                "'urn:bamm:io.catenax.serial_part:1.1.1#NOT_SUPPORTED_NAME' is not supported"
                        ).toArray()));
    }
}
