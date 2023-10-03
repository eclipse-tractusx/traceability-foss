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

package org.eclipse.tractusx.traceability.integration.submodel;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.submodel.infrastructure.model.SubmodelEntity;
import org.eclipse.tractusx.traceability.submodel.infrastructure.reposotory.JpaSubmodelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class SubmodelControllerIT extends IntegrationTestSpecification {

    @Autowired
    JpaSubmodelRepository jpaSubmodelRepository;

    @Test
    void givenNoSubmodels_whenDeleteAll_thenDeleteSubmodelsFromDatabase() {
        // given
        String submodelId = "UUID:Xa123123";
        jpaSubmodelRepository.save(SubmodelEntity.builder()
                .id(submodelId)
                .submodel("payload")
                .build());
        List<SubmodelEntity> savedSubmodels = jpaSubmodelRepository.findAll();
        assertThat(savedSubmodels).isNotEmpty();

        // when
        given()
                .log().all()
                .when()
                .delete("/api/submodel/data")
                .then()
                .log().all()
                .statusCode(204);

        // then
        List<SubmodelEntity> submodelsAfterDeletion = jpaSubmodelRepository.findAll();
        assertThat(submodelsAfterDeletion).isEmpty();
    }

    @Test
    void givenSubmodel_whenGetById_thenGetIt() {
        // given
        String submodelId = "UUID:Xa123123";
        String payload = "Payload string";
        jpaSubmodelRepository.save(SubmodelEntity.builder()
                .id(submodelId)
                .submodel(payload)
                .build());

        // when
        String responseBody = given()
                .log().all()
                .when()
                .get("/api/submodel/data/" + submodelId)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .asString();

        // then
        assertThat(responseBody).isEqualTo(payload);
    }

    @Test
    void givenNoSubmodels_whenGetById_thenNotFound() {
        // given
        String submodelId = "UUID:Xa123123";

        // when/then
        given()
                .log().all()
                .when()
                .get("/api/submodel/data/" + submodelId)
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    void givenSubmodel_whenSave_thenSaveIntoDatabase() {
        // given
        String submodelId = "submodelId";
        String requestContent = "test request";

        // when
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(requestContent)
                .post("/api/submodel/data/" + submodelId)
                .then()
                .statusCode(204);

        // then
        SubmodelEntity entity = jpaSubmodelRepository.findById(submodelId).get();
        assertThat(entity.getId()).isEqualTo(submodelId);
        assertThat(entity.getSubmodel()).isEqualTo(requestContent);
    }
}
