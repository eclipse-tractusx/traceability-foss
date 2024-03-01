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

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.repository.AssetAsBuiltSupportRepository;
import org.eclipse.tractusx.traceability.integration.common.support.repository.BpnSupportRepository;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class IrsCallbackControllerIT extends IntegrationTestSpecification {

    @Autowired
    IrsApiSupport irsApiSupport;

    @Autowired
    BpnSupportRepository bpnSupportRepository;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    AssetAsBuiltSupportRepository assetAsBuiltSupportRepository;


    @Test
    void givenNoAssets_whenCallbackReceived_thenSaveThem() {
        // Given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsApiReturnsJobDetails();
        String jobId = "ebb79c45-7bba-4169-bf17-3e719989ab54";
        String jobState = "COMPLETED";

        // When
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

        // Then
        assertThat(bpnSupportRepository.findAll()).hasSize(6);
        assetsSupport.assertAssetAsBuiltSize(15);
        assetsSupport.assertAssetAsPlannedSize(0);
    }

    @Test
    void givenNoAssets_whenCallbackReceivedForAsPlanned_thenSaveThem() {
        // Given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsJobDetailsAsPlanned();
        String jobId = "ebb79c45-7bba-4169-bf17-SUCCESSFUL_AS_PLANNED";
        String jobState = "COMPLETED";

        // When
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

        // Then
        assertThat(bpnSupportRepository.findAll()).hasSize(2);
        assetsSupport.assertAssetAsBuiltSize(0);
        assetsSupport.assertAssetAsPlannedSize(2);
    }

    @Test
    void givenAssetExist_whenCallbackReceived_thenUpdateIt() {
        // Given
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        irsApiSupport.irsApiReturnsJobDetails();
        String jobId = "ebb79c45-7bba-4169-bf17-3e719989ab54";
        String jobState = "COMPLETED";

        // When
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

        // Then
        assertThat(bpnSupportRepository.findAll()).hasSize(6);
        assetsSupport.assertAssetAsBuiltSize(15);
        assetsSupport.assertAssetAsPlannedSize(0);
    }
}
