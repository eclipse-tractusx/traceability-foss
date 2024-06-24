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

package org.eclipse.tractusx.traceability.integration.openapi;

import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.commons.io.FileUtils.writeStringToFile;

@EnabledIf(expression = "${openapi-doc.generate}", loadContext = true)
class OpenApiDocumentationIT extends IntegrationTestSpecification {

    private static final List<String> API_DOCUMENTATION_LOCATIONS = List.of("./openapi/traceability-foss-backend.json",
            "../docs/api/traceability-foss-backend.json");

    @Test
    void shouldGenerateOpenApiDoc() {
        // when
        var response = given()
                .when()
                .get("/api/v3/api-docs");

        // then
        response.then()
                .statusCode(200);

        API_DOCUMENTATION_LOCATIONS.forEach(location -> {
            try {
                writeStringToFile(new File(location), response.body().asPrettyString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
