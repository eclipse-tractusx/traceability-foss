/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.IrsApiSupport
import org.hamcrest.Matchers

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.hamcrest.Matchers.*

class AssetsControllerAllIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, BpnSupport {

    def "should return assets with manufacturer name"() {
        given:
        cachedBpnsForDefaultAssets()

        and:
        defaultAssetsStored()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets")
                .then()
                .statusCode(200)
                .body("content.manufacturerName", everyItem(not(equalTo(emptyText()))))
    }

    // Deprecated please remove once controller has been removed
    def "should return supplier assets"() {
        given:
        defaultAssetsStored()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .queryParam("owner", "SUPPLIER")
                .when()
                .get("/api/assets")
                .then()
                .statusCode(200)
                .body("totalItems", equalTo(14))
    }

    // Deprecated please remove once controller has been removed
    def "should return own assets"() {
        given:
        defaultAssetsStored()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .queryParam("owner", "OWN")
                .when()
                .get("/api/assets")
                .then()
                .statusCode(200)
                .body("totalItems", equalTo(1))
    }

    def "should return all assets"() {
        given:
        defaultAssetsStored()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets",)
                .then()
                .statusCode(200)
                .body("totalItems", equalTo(15))
                .body("content[0]", hasEntry("id", "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"))
                .body("content[0]", hasEntry("idShort", "vehicle_hybrid.asm"))
                .body("content[0]", hasEntry("semanticModelId", "OMA-TGFAYUHXFLHHUQQMPLTE"))
                .body("content[0]", hasEntry("manufacturerId", "BPNL00000003AYRE"))
                .body("content[0]", hasEntry("manufacturerName", "Manufacturer Name 1"))

                .body("content[0]", hasEntry("underInvestigation", false))
                .body("content[0]", hasEntry("qualityType", "Ok"))
                .body("content[0]", hasEntry("van", "OMA-TGFAYUHXFLHHUQQMPLTE"))
                .body("content[0].semanticModel", hasEntry("manufacturingCountry", "DEU"))
                .body("content[0].semanticModel", hasEntry("manufacturingDate", "2014-11-18T08:23:55Z"))
                .body("content[0].semanticModel", hasEntry("nameAtManufacturer", "Vehicle Hybrid"))
                .body("content[0].semanticModel", hasEntry("manufacturerPartId", emptyText()))
                .body("content[0].semanticModel", hasEntry("nameAtCustomer", emptyText()))
                .body("content[0].semanticModel", hasEntry("customerPartId", emptyText()))

    }

    def "should return assets by owner filtering"() {
        given:
        defaultAssetsStored()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .queryParam("owner", ownerValue)
                .when()
                .get("/api/assets",)
                .then()
                .statusCode(200)
                .body("totalItems", equalTo(totalItemsValue))

        where:
        ownerValue || totalItemsValue
        "OWN"      || 1
        "CUSTOMER" || 0
        "SUPPLIER" || 14
        "UNKNOWN"  || 0
    }

    def "should get a page of assets"() {
        given:
        defaultAssetsStored()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", "2")
                .param("size", "2")
                .when()
                .get("/api/assets")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(1))
                .body("pageSize", Matchers.is(2))
    }

}
