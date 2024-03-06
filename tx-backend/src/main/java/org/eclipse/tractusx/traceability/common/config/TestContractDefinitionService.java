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

package org.eclipse.tractusx.traceability.common.config;

import lombok.Generated;
import org.eclipse.tractusx.irs.edc.client.EdcConfiguration;
import org.eclipse.tractusx.irs.edc.client.asset.model.EdcContext;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcContractDefinitionCriteria;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcCreateContractDefinitionRequest;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class TestContractDefinitionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(EdcContractDefinitionService.class);
    private static final String ASSET_SELECTOR_ID = "https://w3id.org/edc/v0.0.1/ns/id";
    private static final String ASSET_SELECTOR_EQUALITY_OPERATOR = "=";
    private static final String ASSET_SELECTOR_TYPE = "CriterionDto";
    private static final String CONTRACT_DEFINITION_TYPE = "ContractDefinition";
    private final EdcConfiguration config;
    private final RestTemplate restTemplate;

    public String createContractDefinition(String assetId, String policyId) throws CreateEdcContractDefinitionException {
        final String contractId = UUID.randomUUID().toString();
        EdcCreateContractDefinitionRequest createContractDefinitionRequest = this.createContractDefinitionRequest(assetId, policyId, contractId);

        try {
            ResponseEntity<String> createContractDefinitionResponse = this.restTemplate.postForEntity(this.config.getControlplane().getEndpoint().getContractDefinition(), createContractDefinitionRequest, String.class, new Object[0]);
            HttpStatusCode responseCode = createContractDefinitionResponse.getStatusCode();
             if (responseCode.value() == HttpStatus.OK.value()) {
                return contractId;
            } else {
                throw new CreateEdcContractDefinitionException("Failed to create EDC contract definition for %s notification asset id".formatted(assetId));
            }
        } catch (HttpClientErrorException var6) {
            if (var6.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                log.info("{} contract definition already exists in the EDC", policyId);
                return contractId;
            }
            log.error("Failed to create edc contract definition for {} notification asset and {} policy definition id. Reason: ", new Object[]{assetId, policyId, var6});
            throw new CreateEdcContractDefinitionException(var6);
        }
    }

    public EdcCreateContractDefinitionRequest createContractDefinitionRequest(String assetId, String accessPolicyId, String contractId) {
        EdcContractDefinitionCriteria edcContractDefinitionCriteria = EdcContractDefinitionCriteria.builder().type("CriterionDto").operandLeft("https://w3id.org/edc/v0.0.1/ns/id").operandRight(assetId).operator("=").build();
        EdcContext edcContext = EdcContext.builder().edc("https://w3id.org/edc/v0.0.1/ns/").build();
        return EdcCreateContractDefinitionRequest.builder().contractPolicyId(accessPolicyId).edcContext(edcContext).type("ContractDefinition").accessPolicyId(accessPolicyId).contractDefinitionId(contractId).assetsSelector(edcContractDefinitionCriteria).build();
    }

    @Generated
    public TestContractDefinitionService(EdcConfiguration config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }
}
