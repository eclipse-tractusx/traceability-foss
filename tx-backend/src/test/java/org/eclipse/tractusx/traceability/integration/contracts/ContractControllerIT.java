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

import contract.response.ContractResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class ContractControllerIT extends IntegrationTestSpecification {

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
        PageResult<ContractResponse> contractResponsePageResult = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(new PageableFilterRequest())
                .post("/api/contracts")
                .then()
                .log().all()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {
                });
        //THEN
        assertThat(contractResponsePageResult.content()).isNotEmpty();
        assertThat(contractResponsePageResult.content().get(0).getPolicy()).isNotEmpty();
    }

    @Test
    void shouldReturnNextPageOfPaginatedContracts() throws JoseException {
        //GIVEN
        edcSupport.edcWillReturnPaginatedContractAgreements();
        edcSupport.edcWillReturnContractAgreementNegotiation();
        assetsSupport.defaultAssetsStored();

        //WHEN
        PageResult<ContractResponse> contractResponsePage1Result = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(PageableFilterRequest.builder().ownPageable(OwnPageable.builder().size(5).build()).build())
                .post("/api/contracts")
                .then()
                .log().all()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {
                });


        PageResult<ContractResponse> contractResponsePage2Result = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(PageableFilterRequest.builder().ownPageable(OwnPageable.builder().size(5).page(1).build()).build())
                .post("/api/contracts")
                .then()
                .log().all()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {
                });
        //THEN
        List<String> firstContractagreementIds = List.of("abc1", "abc2", "abc3", "abc4", "abc5");
        List<String> secondContractagreementIds = List.of("abc6", "abc7", "abc8", "abc9", "abc10");

        assertThat(contractResponsePage1Result.content()).isNotEmpty();
        assertThat(contractResponsePage2Result.content()).isNotEmpty();

        assertThat(contractResponsePage1Result.content().stream().map(ContractResponse::getContractId).collect(Collectors.toList())).containsAll(firstContractagreementIds);
        assertThat(contractResponsePage2Result.content().stream().map(ContractResponse::getContractId).toList()).containsAll(secondContractagreementIds);
    }

    @Test
    void shouldReturnOnlyOneContract() throws JoseException {
        //GIVEN
        edcSupport.edcWillReturnOnlyOneContractAgreement();
        edcSupport.edcWillReturnContractAgreementNegotiation();
        assetsSupport.defaultAssetsStored();

        //WHEN
        PageResult<ContractResponse> contractResponsePageResult = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(PageableFilterRequest.builder().searchCriteriaRequestParam(SearchCriteriaRequestParam.builder().filter(List.of("id,EQUAL,urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb,AND")).build()).build())
                .post("/api/contracts")
                .then()
                .log().all()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {
                });
        //THEN
        assertThat(contractResponsePageResult.content()).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyIfAssetIdIsUnknown() throws JoseException {
        //GIVEN
        edcSupport.edcWillReturnOnlyOneContractAgreement();
        edcSupport.edcWillReturnContractAgreementNegotiation();
        assetsSupport.defaultAssetsStored();

        //WHEN//THEN
        PageResult<ContractResponse> contractResponsePageResult = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(PageableFilterRequest.builder().searchCriteriaRequestParam(SearchCriteriaRequestParam.builder().filter(List.of("id,EQUAL,IdontExist,AND")).build()).build())
                .post("/api/contracts")
                .then()
                .log().all()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {
                });
        //THEN
        assertThat(contractResponsePageResult.content()).isEmpty();
    }

}
