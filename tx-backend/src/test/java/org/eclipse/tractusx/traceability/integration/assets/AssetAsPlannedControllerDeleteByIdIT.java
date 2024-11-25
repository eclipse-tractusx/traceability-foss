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
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability.integration.assets;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;

class AssetAsPlannedControllerDeleteByIdIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    AssetsApiSupport assetsApiSupport;

    @Test
    void shouldDeleteAssetById() throws JoseException {

        assetsSupport.defaultAssetsAsPlannedStored();
        final Header authHeader = oAuth2Support.jwtAuthorization(ADMIN);
        final BomLifecycle bomLifecycle = BomLifecycle.AS_PLANNED;
        final String assetId = "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01";

        assetsApiSupport.getAssetById(authHeader, assetId, bomLifecycle, 200);
        assetsApiSupport.deleteAssetById(authHeader, assetId, bomLifecycle, 200);
        assetsApiSupport.getAssetById(authHeader, assetId, bomLifecycle, 404);

    }

    @Test
    void shouldNotDeleteAssetsWrongRole() throws JoseException {
        assetsSupport.defaultAssetsAsPlannedStored();
        final Header authHeader = oAuth2Support.jwtAuthorization(SUPERVISOR);
        final String assetId = "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01";
        final BomLifecycle bomLifecycle = BomLifecycle.AS_PLANNED;
        assetsApiSupport.getAssetById(authHeader, assetId, bomLifecycle, 200);
        assetsApiSupport.deleteAssetById(authHeader, assetId, bomLifecycle, 403);
    }

}
