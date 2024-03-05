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
package org.eclipse.tractusx.traceability.integration.contracts;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

public class ContractsControllerIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    EdcSupport edcSupport;

    @Test
    void shouldReturnContracts() throws JoseException {
        //GIVEN
        edcSupport.edcWillReturnContractAgreements();
        edcSupport.edcWillReturnContractAgreementNegotiation();
        assetsSupport.defaultAssetsStored();

        //WHEN
        PageResult contractResponsePageResult = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/contracts")
                .then()
                .log().all()
                .statusCode(200)
                .extract().body().as(PageResult.class);
        //THEN
        assertThat(contractResponsePageResult.content()).isNotEmpty();
    }

    @Test
    void shouldReturnNextPageOfPaginatedContracts() throws JoseException {
        //GIVEN
        edcSupport.edcWillReturnPaginatedContractAgreements();
        edcSupport.edcWillReturnContractAgreementNegotiation();
        assetsSupport.defaultAssetsStored();

        //WHEN
        PageResult contractResponsePage1Result = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/contracts?size=5&page=0")
                .then()
                .log().all()
                .statusCode(200)
                .extract().body().as(PageResult.class);


        PageResult contractResponsePage2Result = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/contracts?size=5&page=1")
                .then()
                .log().all()
                .statusCode(200)
                .extract().body().as(PageResult.class);
        //THEN
        assertThat(contractResponsePage1Result.content()).isNotEmpty();
        assertThat(contractResponsePage2Result.content()).isNotEmpty();
    }

}
