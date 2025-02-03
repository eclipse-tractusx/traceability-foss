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

package org.eclipse.tractusx.traceability.integration.common.support;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
@RequiredArgsConstructor
public class AssetsApiSupport {

    public void deleteAssetById(Header authHeader, String assetId, BomLifecycle bomLifecycle, int expectedStatusCode) {
        final String path = bomLifecycle == BomLifecycle.AS_BUILT ? "as-built" : "as-planned";
        given()
                .contentType(ContentType.JSON)
                .header(authHeader)
                .when()
                .delete("/api/assets/" + path + "/" + assetId)
                .then()
                .statusCode(expectedStatusCode);
    }

    public void getAssetById(Header authHeader, final String assetId, BomLifecycle bomLifecycle,int expectedStatusCode){
        final String path = bomLifecycle == BomLifecycle.AS_BUILT ? "as-built" : "as-planned";
        given()
                .header(authHeader)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/" + path + "/" + assetId)
                .then()
                .statusCode(expectedStatusCode);
    }

}
