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

import common.FilterAttribute;
import common.FilterValue;
import contract.request.ContractFilter;
import contract.request.ContractRequest;
import contract.response.ContractResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.ContractsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class ContractsControllerIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    EdcSupport edcSupport;
    @Autowired
    ContractsSupport contractsSupport;

    @Test
    void shouldReturnOnlyOneContract() throws JoseException {
        //GIVEN
        edcSupport.edcWillReturnOnlyOneContractAgreement();
        edcSupport.edcWillReturnContractAgreementNegotiation();
        assetsSupport.defaultAssetsStored();
        contractsSupport.defaultContractAgreementAsBuiltStored();

        //WHEN
        PageResult<ContractResponse> contractResponsePageResult = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(ContractRequest.builder().page(0).size(10)
                        .contractFilter(ContractFilter.builder()
                                .contractId(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value("abc1").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build()).build())
                .post("/api/contracts")
                .then()
                .log().all()
                .statusCode(200)
                .extract().body().as(new TypeRef<>() {
                });
        //THEN
        assertThat(contractResponsePageResult.content()).isNotEmpty();
        assertThat(contractResponsePageResult.content().get(0).getCounterpartyAddress()).isNotEmpty();
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
                .body(ContractRequest.builder().page(0).size(10).sort(Collections.emptyList())
                        .contractFilter(ContractFilter.builder()
                                .id(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder()
                                                .value("IdontExist")
                                                .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                .build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build())
                        .build())
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
