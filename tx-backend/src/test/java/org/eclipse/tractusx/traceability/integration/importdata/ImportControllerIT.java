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

import assets.importpoc.ImportResponse;
import assets.importpoc.ImportStateMessage;
import assets.importpoc.ImportStateResponse;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class ImportControllerIT extends IntegrationTestSpecification {

    @Autowired
    AssetAsBuiltRepository assetAsBuiltRepository;

    @Test
    void givenValidFile_whenImportData_thenValidationShouldPass() throws JoseException {
        // given
        String path = getClass().getResource("/testdata/importfiles/validImportFile.json").getFile();
        File file = new File(path);

        // when/then
        ImportResponse result = given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(200)
                .extract().as(ImportResponse.class);

        assertThat(result.validationResult().validationErrors()).isEmpty();
        assertThat(result.importStateMessage()).containsExactlyInAnyOrder(
                new ImportStateMessage("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb01", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4c79e", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:c7a2b803-f8fe-4b79-b6fc-967ce847c9a1", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:aad27ddb-43aa-4e42-98c2-01e529ef128c", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:2c57b0e9-a653-411d-bdcd-64787e9fd3a7", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:07cb071f-8716-45fe-89f1-f2f77a1ce93b", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:e8c48a8e-d2d7-43d9-a867-65c70c85f5b8", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:f11ddc62-3bd5-468f-b7b0-110fe13ed0cd", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:c47b9f8b-48d0-4ef4-8f0b-e965a225cb8d", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb01", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb02", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb03", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:580d3adf-1981-44a0-a214-13d6ceed6841", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:b0acf3e1-3fbe-46c0-aa0b-0724caae7772", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:da978a30-4dde-4d76-808a-b7946763ff0d", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:254604ab-2153-45fb-8cad-54ef09f4080f", ImportStateResponse.TRANSIENT, true)
        );
    }

    @Test
    void givenValidFile_whenImportDataButAssetExistInPersistentImportState_thenValidationShouldPassAndExpectedResponse() throws JoseException {
        // given
        String path = getClass().getResource("/testdata/importfiles/validImportFile.json").getFile();
        File file = new File(path);
        AssetBase asset = AssetBase.builder()
                .id("urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd")
                .semanticModelId("NO-313869652971440618042264")
                .manufacturerId("BPNL00000003AXS3")
                .nameAtManufacturer("Door f-l")
                .manufacturerPartId("")
                .detailAspectModels(List.of())
                .owner(Owner.OWN)
                .activeAlert(false)
                .inInvestigation(false)
                .qualityType(QualityType.OK)
                .classification("component")
                .importState(ImportState.PERSISTENT)
                .build();
        assetAsBuiltRepository.save(asset);
        // when/then
        ImportResponse result = given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(200)
                .extract().as(ImportResponse.class);

        assertThat(result.validationResult().validationErrors()).isEmpty();
        assertThat(result.importStateMessage()).containsExactlyInAnyOrder(
                new ImportStateMessage("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb01", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4c79e", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:c7a2b803-f8fe-4b79-b6fc-967ce847c9a1", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:aad27ddb-43aa-4e42-98c2-01e529ef128c", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:2c57b0e9-a653-411d-bdcd-64787e9fd3a7", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:07cb071f-8716-45fe-89f1-f2f77a1ce93b", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:e8c48a8e-d2d7-43d9-a867-65c70c85f5b8", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd", ImportStateResponse.TRANSIENT, false),
                new ImportStateMessage("urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:f11ddc62-3bd5-468f-b7b0-110fe13ed0cd", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:c47b9f8b-48d0-4ef4-8f0b-e965a225cb8d", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb01", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb02", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb03", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:580d3adf-1981-44a0-a214-13d6ceed6841", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:b0acf3e1-3fbe-46c0-aa0b-0724caae7772", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:da978a30-4dde-4d76-808a-b7946763ff0d", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:254604ab-2153-45fb-8cad-54ef09f4080f", ImportStateResponse.TRANSIENT, true)
        );
    }

    @Test
    void givenValidFile_whenImportDataButAssetExistInTransientImportState_thenValidationShouldPassAndExpectedResponse() throws JoseException {
        // given
        String path = getClass().getResource("/testdata/importfiles/validImportFile.json").getFile();
        File file = new File(path);
        AssetBase asset = AssetBase.builder()
                .id("urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd")
                .semanticModelId("NO-313869652971440618042264")
                .manufacturerId("BPNL00000003AXS3")
                .nameAtManufacturer("Door to be updated")
                .manufacturerPartId("")
                .detailAspectModels(List.of())
                .owner(Owner.OWN)
                .activeAlert(false)
                .inInvestigation(false)
                .qualityType(QualityType.OK)
                .classification("component")
                .importState(ImportState.TRANSIENT)
                .build();
        assetAsBuiltRepository.save(asset);
        // when/then
        ImportResponse result = given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(200)
                .extract().as(ImportResponse.class);

        assertThat(result.validationResult().validationErrors()).isEmpty();
        assertThat(result.importStateMessage()).containsExactlyInAnyOrder(
                new ImportStateMessage("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb01", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4c79e", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:c7a2b803-f8fe-4b79-b6fc-967ce847c9a1", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:aad27ddb-43aa-4e42-98c2-01e529ef128c", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:2c57b0e9-a653-411d-bdcd-64787e9fd3a7", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:07cb071f-8716-45fe-89f1-f2f77a1ce93b", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:e8c48a8e-d2d7-43d9-a867-65c70c85f5b8", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:f11ddc62-3bd5-468f-b7b0-110fe13ed0cd", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:c47b9f8b-48d0-4ef4-8f0b-e965a225cb8d", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb01", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb02", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fb03", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:580d3adf-1981-44a0-a214-13d6ceed6841", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:b0acf3e1-3fbe-46c0-aa0b-0724caae7772", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:da978a30-4dde-4d76-808a-b7946763ff0d", ImportStateResponse.TRANSIENT, true),
                new ImportStateMessage("urn:uuid:254604ab-2153-45fb-8cad-54ef09f4080f", ImportStateResponse.TRANSIENT, true)
        );
        AssetBase updatedAsset = assetAsBuiltRepository.getAssetById("urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd");
        assertThat(updatedAsset.getImportNote()).isEqualTo("Asset updated successfully in transient state.");
        assertThat(updatedAsset.getNameAtManufacturer()).isEqualTo("Door f-l");
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
                .body("validationResult.validationErrors", Matchers.contains(
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
                .body("validationResult.validationErrors", Matchers.contains(
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
                .body("validationResult.validationErrors", Matchers.contains(
                        List.of(
                                "'urn:bamm:io.catenax.serial_part:1.1.1#NOT_SUPPORTED_NAME' is not supported"
                        ).toArray()));
    }
}
