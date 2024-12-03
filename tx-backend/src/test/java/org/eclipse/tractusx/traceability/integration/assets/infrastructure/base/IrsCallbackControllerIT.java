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

package org.eclipse.tractusx.traceability.integration.assets.infrastructure.base;

import assets.importpoc.ImportResponse;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.repository.AssetAsBuiltSupportRepository;
import org.eclipse.tractusx.traceability.integration.common.support.repository.BpnSupportRepository;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class IrsCallbackControllerIT extends IntegrationTestSpecification {

    @Autowired
    IrsApiSupport irsApiSupport;

    @Autowired
    BpnSupportRepository bpnSupportRepository;

    @Autowired
    BpnSupport bpnSupport;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    AssetAsBuiltSupportRepository assetAsBuiltSupportRepository;


    @Test
    void givenAssets_whenCallbackReceived_thenSaveThemAndStoreContractAgreementId() throws JoseException {
        // given

        bpnSupport.providesBpdmLookup();
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsApiReturnsJobDetails();
        String jobId = "ebb79c45-7bba-4169-bf17-3e719989ab54";
        String jobState = "COMPLETED";

        // when
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("id", jobId)
                .param("state", jobState)
                .get("/api/irs/job/callback")
                .then()
                .log().all()
                .statusCode(200);

        // then
        assertThat(bpnSupportRepository.findAll()).hasSize(1);
        assetsSupport.assertAssetAsBuiltSize(2);
        assetsSupport.assertAssetAsPlannedSize(0);

        // Make the API call and store the response
        given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .pathParam("assetId", "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")
                .get("/api/assets/as-built/{assetId}")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    void givenNoAssets_whenCallbackReceivedForAsPlanned_thenSaveThem() {
        // given
        bpnSupport.providesBpdmLookup();
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsJobDetailsAsPlanned();
        String jobId = "ebb79c45-7bba-4169-bf17-SUCCESSFUL_AS_PLANNED";
        String jobState = "COMPLETED";

        // when
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("id", jobId)
                .param("state", jobState)
                .get("/api/irs/job/callback")
                .then()
                .log().all()
                .statusCode(200);

        // then
        assertThat(bpnSupportRepository.findAll()).hasSize(1);
        assetsSupport.assertAssetAsBuiltSize(0);
        assetsSupport.assertAssetAsPlannedSize(3);
    }

    @Test
    void givenInvalidJobId_whenCallbackReceivedForAsPlanned_thenNothingSynchronized() {
        // given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsJobDetailsAsPlanned();
        String invalidJobId = "irs/admin/test/ID";
        String jobState = "COMPLETED";

        // when
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("id", invalidJobId)
                .param("state", jobState)
                .get("/api/irs/job/callback")
                .then()
                .log().all()
                .statusCode(200);

        // then
        assertThat(bpnSupportRepository.findAll()).isEmpty();
        assetsSupport.assertAssetAsBuiltSize(0);
        assetsSupport.assertAssetAsPlannedSize(0);
    }

    @Test
    void givenAssetExist_whenCallbackReceived_thenUpdateIt() {
        // given
        assetsSupport.defaultAssetsStored();
        bpnSupport.providesBpdmLookup();
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsApiReturnsJobDetails();
        String jobId = "ebb79c45-7bba-4169-bf17-3e719989ab54";
        String jobState = "COMPLETED";

        // when
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("id", jobId)
                .param("state", jobState)
                .get("/api/irs/job/callback")
                .then()
                .log().all()
                .statusCode(200);


        // then
        assertThat(bpnSupportRepository.findAll()).hasSize(1);
        assetsSupport.assertAssetAsBuiltSize(14);
        assetsSupport.assertAssetAsPlannedSize(0);
        String updatedIdShort = assetsSupport.findById("urn:uuid:6dafbcec-2fce-4cbb-a5a9-b3b32aa5cffc").getIdShort();
        assertThat(updatedIdShort).isEqualTo("ecu.asm");
        //urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb
        //vehicle_hybrid_v2.asm
    }

    @Test
    void givenSuccessImportJob_whenCallbackReceivedWithTombsones_thenUpdateAsBuiltAsset() throws JoseException {
        // given

        bpnSupport.providesBpdmLookup();
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsApiReturnsJobDetails();
        String jobId = "ebb79c45-7bba-4169-bf17-3e719989ab54";
        String jobState = "COMPLETED";

        String path = getClass().getResource("/testdata/importfiles/validImportFile.json").getFile();
        File file = new File(path);

        given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(200)
                .extract().as(ImportResponse.class);

        // when
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("id", jobId)
                .param("state", jobState)
                .get("/api/irs/job/callback")
                .then()
                .log().all()
                .statusCode(200);

        // then
        String tombstoneAsBuilt = given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .pathParam("assetId", "urn:uuid:b978ad2d-be06-47ea-a578-580d9b2eca77")
                .get("/api/assets/as-built/{assetId}")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("tombstone");

        assertThat(tombstoneAsBuilt).isNotEmpty();

    }

    @Test
    void givenSuccessImportJob_whenCallbackReceivedWithTombsones_thenUpdateAsPlannedAsset() throws JoseException {
        // given
        bpnSupport.providesBpdmLookup();
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsJobDetailsAsPlanned();
        String jobId = "ebb79c45-7bba-4169-bf17-SUCCESSFUL_AS_PLANNED";
        String jobState = "COMPLETED";

        String path = getClass().getResource("/testdata/importfiles/validImportFile-onlyAsPlannedAsset.json").getFile();
        File file = new File(path);

        given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(200)
                .extract().as(ImportResponse.class);

        // when
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("id", jobId)
                .param("state", jobState)
                .get("/api/irs/job/callback")
                .then()
                .log().all()
                .statusCode(200);

        // then
        String tombstoneAsPlanned = given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .pathParam("assetId", "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb02")
                .get("/api/assets/as-planned/{assetId}")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("tombstone");

        assertThat(tombstoneAsPlanned).isNotEmpty();
    }


    @Test
    void givenNoAssets_whenCallbackReceived_thenSaveThem_withoutManufacturerName() throws JoseException {
        // given

        bpnSupport.returnsBpdmLookup401Unauthorized();
        irsApiSupport.irsApiReturnsJobDetails();
        String jobId = "ebb79c45-7bba-4169-bf17-3e719989ab54";
        String jobState = "COMPLETED";

        // when
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("id", jobId)
                .param("state", jobState)
                .get("/api/irs/job/callback")
                .then()
                .log().all()
                .statusCode(200);

        // then
        assetsSupport.assertAssetAsBuiltSize(2);
        assetsSupport.assertAssetAsPlannedSize(0);
        final String manufacturerName = given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .pathParam("assetId", "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")
                .get("/api/assets/as-built/{assetId}")
                .then()
                .log().all()
                .statusCode(200)
                .extract().path("manufacturerName");
        assertThat(manufacturerName).isEmpty();
    }


}
