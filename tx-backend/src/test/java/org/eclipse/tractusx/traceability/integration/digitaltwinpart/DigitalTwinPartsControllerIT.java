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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability.integration.digitaltwinpart;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.FilterAttribute;
import common.FilterValue;
import digitaltwinpart.ActorResponse;
import digitaltwinpart.DigitalTwinPartDetailRequest;
import digitaltwinpart.DigitalTwinPartDetailResponse;
import digitaltwinpart.DigitalTwinPartFilter;
import digitaltwinpart.DigitalTwinPartRequest;
import digitaltwinpart.DigitalTwinPartResponse;
import digitaltwinpart.SearchableDigitalTwinPartRequest;
import io.restassured.common.mapper.TypeRef;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.TriggerConfigurationEntity;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AASDatabaseSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.ConfigurationSupport;
import org.eclipse.tractusx.traceability.integration.common.support.DigitalTwinPartApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DigitalTwinPartsControllerIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;
    @Autowired
    EdcSupport edcSupport;
    @Autowired
    IrsApiSupport irsApiSupport;
    @Autowired
    AASDatabaseSupport aasDatabaseSupport;
    @Autowired
    ConfigurationSupport configurationSupport;
    @Autowired
    DigitalTwinPartApiSupport digitalTwinPartApiSupport;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        edcSupport.performSupportActionsForBpdmAccess();
        irsApiSupport.irsApiReturnsPoliciesBpdm();
    }

    @Test
    void shouldReturnDigitalTwinPartSorted() {
        // GIVEN
        assetsSupport.defaultAssetsStored();
        String aasId = "ABC_1";
        String aasId2 = "ABC_2";
        aasDatabaseSupport.createAASEntityByAASId(aasId);
        aasDatabaseSupport.createAASEntityByAASId(aasId2);

        DigitalTwinPartRequest digitalTwinPartRequest = DigitalTwinPartRequest.builder()
                .filters(List.of())
                .page(0)
                .size(10)
                .sort(List.of("aasId,desc"))
                .build();

        // WHEN
        PageResult<DigitalTwinPartResponse> digitalTwinPartResponsePageResult =
                digitalTwinPartApiSupport.postDigitalTwinPart(digitalTwinPartRequest, 200, new TypeRef<>() {});

        // THEN
        assertThat(digitalTwinPartResponsePageResult.content()).hasSize(2);
        assertThat(digitalTwinPartResponsePageResult.content().get(0).getAasId()).isEqualTo(aasId2);
    }

    @Test
    void shouldReturnDigitalTwinPartFiltered() {
        // GIVEN
        assetsSupport.defaultAssetsStored();
        String aasId = "ABC_1";
        String aasId2 = "ABC_2";
        aasDatabaseSupport.createAASEntityByAASId(aasId);
        aasDatabaseSupport.createAASEntityByAASId(aasId2);

        FilterAttribute filterAttribute = FilterAttribute.builder()
                .value(List.of(FilterValue.builder()
                        .value(aasId)
                        .strategy(SearchCriteriaStrategy.STARTS_WITH.name())
                        .build()))
                .operator("OR")
                .build();

        DigitalTwinPartFilter digitalTwinPartFilter = DigitalTwinPartFilter.builder()
                .aasId(filterAttribute)
                .build();

        DigitalTwinPartRequest digitalTwinPartRequest = DigitalTwinPartRequest.builder()
                .filters(List.of(digitalTwinPartFilter))
                .page(0)
                .size(10)
                .sort(List.of("aasId,desc"))
                .build();

        // WHEN
        PageResult<DigitalTwinPartResponse> digitalTwinPartResponsePageResult =
                digitalTwinPartApiSupport.postDigitalTwinPart(digitalTwinPartRequest, 200, new TypeRef<>() {});

        // THEN
        assertThat(digitalTwinPartResponsePageResult.content()).isNotEmpty();
        assertThat(digitalTwinPartResponsePageResult.content().get(0).getAasId()).isEqualTo(aasId);
    }

    @Test
    void shouldReturnDigitalTwinPartDetails() {
        // GIVEN
        assetsSupport.defaultAssetsStored();
        String aasId = "ABC_1";
        String aasId2 = "ABC_2";
        aasDatabaseSupport.createAASEntityByAASId(aasId);
        aasDatabaseSupport.createAASEntityByAASId(aasId2);

        TriggerConfigurationEntity triggerConfigurationEntity = TriggerConfigurationEntity.builder()
                .cronExpressionRegisterOrderTTLReached("* * * * * *")
                .cronExpressionMapCompletedOrders("* * * * * *")
                .partTTL(1000)
                .aasTTL(1000)
                .build();
        configurationSupport.storeTriggerConfiguration(triggerConfigurationEntity);

        DigitalTwinPartDetailRequest digitalTwinPartDetailRequest = DigitalTwinPartDetailRequest.builder()
                .aasId(aasId)
                .build();

        // WHEN
        DigitalTwinPartDetailResponse response =
                digitalTwinPartApiSupport.postDigitalTwinPartDetail(digitalTwinPartDetailRequest, 200, DigitalTwinPartDetailResponse.class);

        // THEN
        assertThat(response.getAasId()).isEqualTo(aasId);
        assertThat(response.getAasTTL()).isEqualTo(1000);
        assertThat(response.getActor()).isEqualTo(ActorResponse.SYSTEM);
        assertThat(response.getBpn()).isEqualTo("BPNL00000001TEST");
        assertThat(response.getDigitalTwinType()).isEqualTo("PART_TYPE");
    }

    @Test
    void shouldReturnSearchableValuesForFieldName() {
        // GIVEN
        assetsSupport.defaultAssetsStored();
        String aasId = "ABC_FILTER_TEST";
        aasDatabaseSupport.createAASEntityByAASId(aasId);

        SearchableDigitalTwinPartRequest request = new SearchableDigitalTwinPartRequest();
        request.setFieldName("aasId");
        request.setStartWith("ABC");
        request.setSize(10);

        // WHEN
        List<String> results =
                digitalTwinPartApiSupport.postSearchableValues(request, 200, new TypeRef<>() {});

        // THEN
        assertThat(results)
                .isNotEmpty()
                .anyMatch(val -> val.startsWith("ABC"));
    }
}
