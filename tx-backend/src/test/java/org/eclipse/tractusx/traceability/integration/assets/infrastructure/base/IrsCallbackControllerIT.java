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
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.ContentType;
import org.apache.http.protocol.HTTP;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.repository.BpnSupportRepository;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport.sendCallback;

class IrsCallbackControllerIT extends IntegrationTestSpecification {

    @Autowired
    IrsApiSupport irsApiSupport;

    @Autowired
    BpnSupportRepository bpnSupportRepository;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    EdcSupport edcSupport;

    @Autowired
    BpnSupport bpnSupport;

    private final static int HTTP_STATUS_OKAY = 200;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        edcSupport.performSupportActionsForBpdmAccess();
        irsApiSupport.irsApiReturnsPoliciesBpdm();
        bpnSupport.providesBpdmLookup();
    }

    @Test
    void givenAssets_whenCallbackReceived_thenSaveThemAndStoreContractAgreementId()
            throws JoseException {
        // given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsApiReturnsOrderAndBatchDetails();
        irsApiSupport.irsApiReturnsJobDetails();

        String orderId = "ebb79c45-7bba-4169-bf17-3e719989ab54";
        String batchId = "ebb79c45-7bba-4169-bf17-3e719989ab55";
        String orderState = "PROCESSING";
        String batchState = "COMPLETED";

        // when
        sendCallback(orderId, batchId, orderState, batchState, HTTP_STATUS_OKAY);

        // then
        assertThat(bpnSupportRepository.findAll()).hasSize(1);
        assetsSupport.assertAssetAsBuiltSize(2);
        assetsSupport.assertAssetAsPlannedSize(0);
    }

    @Test
    void givenNoAssets_whenCallbackReceivedForAsPlanned_thenSaveThem() {
        // given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsJobDetailsAsPlanned();
        irsApiSupport.irsApiReturnsOrderAndBatchDetailsAsPlanned();
        String orderId = "ebb79c45-7bba-4169-bf17-3e719989ab57";
        String batchId = "ebb79c45-7bba-4169-bf17-3e719989ab58";
        String orderState = "COMPLETED";
        String batchState = "COMPLETED";
        // when
        sendCallback(orderId, batchId, orderState, batchState, HTTP_STATUS_OKAY);

        // then
        assetsSupport.assertAssetAsPlannedSize(3);
    }

    @Test
    void givenNoBatchIdNoBatchState_whenCallbackReceivedOrderCompleted_thenSimplyDoNothing() {
        // given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsJobDetailsAsPlanned();
        irsApiSupport.irsApiReturnsOrderAndBatchDetailsAsPlanned();
        String orderId = "ebb79c45-7bba-4169-bf17-3e719989ab57";
        String batchId = "ebb79c45-7bba-4169-bf17-3e719989ab58";
        String orderState = "COMPLETED";

        // when then
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("orderId", orderId)
                .param("orderState", orderState)
                .get("/api/irs/order/callback")
                .then()
                .log().all()
                .statusCode(HTTP_STATUS_OKAY);

    }


    @Test
    void givenNoJobsForOrder_whenCallbackReceivedForAsPlanned_thenNothingSynchronized() {
        // given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsApiReturnsOrderAndBatchDetailsAsPlannedNoJobs();
        String orderId = "ebb79c45-7bba-4169-bf17-3e719989ab51";
        String batchId = "ebb79c45-7bba-4169-bf17-3e719989ab52";
        String orderState = "COMPLETED";
        String batchState = "COMPLETED";

        // when
        sendCallback(orderId, batchId, orderState, batchState, HTTP_STATUS_OKAY);

        // then
        assertThat(bpnSupportRepository.findAll()).isEmpty();
        assetsSupport.assertAssetAsPlannedSize(0);
    }

    @Test
    void givenSuccessImportJob_whenCallbackReceivedWithTombsones_thenUpdateAsBuiltAsset() throws JoseException {
        // given

        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsApiReturnsJobDetails();
        irsApiSupport.irsApiReturnsOrderAndBatchDetails();
        String orderId = "ebb79c45-7bba-4169-bf17-3e719989ab54";
        String batchId = "ebb79c45-7bba-4169-bf17-3e719989ab55";
        String orderState = "COMPLETED";
        String batchState = "COMPLETED";

        String path = getClass().getResource("/testdata/importfiles/validImportFile.json").getFile();
        File file = new File(path);

        given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(HTTP_STATUS_OKAY)
                .extract().as(ImportResponse.class);

        // when
        sendCallback(orderId, batchId, orderState, batchState, HTTP_STATUS_OKAY);

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
                .statusCode(HTTP_STATUS_OKAY)
                .extract().path("tombstone");

        assertThat(tombstoneAsBuilt).isNotEmpty();

    }

    @Test
    void givenSuccessImportJob_whenCallbackReceivedWithTombsones_thenUpdateAsPlannedAsset() throws JoseException {
        // given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsJobDetailsAsPlanned();
        irsApiSupport.irsApiReturnsOrderAndBatchDetailsAsPlanned();
        String orderId = "ebb79c45-7bba-4169-bf17-3e719989ab57";
        String batchId = "ebb79c45-7bba-4169-bf17-3e719989ab58";
        String orderState = "COMPLETED";
        String batchState = "COMPLETED";

        String path = getClass().getResource("/testdata/importfiles/validImportFile-onlyAsPlannedAsset.json").getFile();
        File file = new File(path);

        given()
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .multiPart(file)
                .post("/api/assets/import")
                .then()
                .statusCode(HTTP_STATUS_OKAY)
                .extract().as(ImportResponse.class);

        // when
        sendCallback(orderId, batchId, orderState, batchState, HTTP_STATUS_OKAY);

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
                .statusCode(HTTP_STATUS_OKAY)
                .extract().path("tombstone");

        assertThat(tombstoneAsPlanned).isNotEmpty();
    }

}
