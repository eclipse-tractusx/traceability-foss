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

package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.asset.EdcAssetService;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.CreateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.EdcAssetAlreadyExistsException;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.EdcPolicyDefinitionAlreadyExists;
import org.eclipse.tractusx.irs.edc.client.policy.service.EdcPolicyDefinitionService;
import org.eclipse.tractusx.traceability.common.properties.RegistryProperties;
import org.eclipse.tractusx.traceability.common.properties.SubmodelProperties;
import org.eclipse.tractusx.traceability.policies.application.service.PolicyService;
import org.springframework.stereotype.Service;
import policies.response.PolicyResponse;

import java.util.UUID;

import static org.eclipse.tractusx.traceability.policies.application.mapper.PolicyMapper.mapToEdcPolicyRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class EdcAssetCreationService {
    private static final String REGISTRY_ASSET_ID = "registry-asset-" + UUID.randomUUID().toString();
    private final EdcAssetService edcAssetService;
    private final EdcPolicyDefinitionService edcPolicyDefinitionService;
    private final EdcContractDefinitionService edcContractDefinitionService;
    private final RegistryProperties registryProperties;
    private final SubmodelProperties submodelProperties;
    private final PolicyService policyService;


    public String createEdcContractDefinitionsForDtrAndSubmodel(String policyId) throws CreateEdcPolicyDefinitionException, CreateEdcAssetException, CreateEdcContractDefinitionException {
        PolicyResponse policy = policyService.getPolicy(policyId);
        String createdPolicyId;
        try {
            boolean exists = edcPolicyDefinitionService.policyDefinitionExists(policyId);
            if (exists) {
                log.info("EDC Policy (DTR) already exists with id: {} reusing it", policyId);
                createdPolicyId = policyId;
            } else {
                createdPolicyId = edcPolicyDefinitionService.createAccessPolicy(mapToEdcPolicyRequest(policy));
                log.info("EDC Policy (DTR) created with id :{}", createdPolicyId);
            }
        } catch (EdcPolicyDefinitionAlreadyExists e) {
            createdPolicyId = policyId;
        } catch (Exception exception) {
            log.warn("EDC Policy (DTR) could not be created: {}", exception.getMessage());
            throw new CreateEdcPolicyDefinitionException(exception);
        }

        String dtrAssetId;
        try {
            dtrAssetId = edcAssetService.createDtrAsset(registryProperties.getUrlWithPathInternal(), REGISTRY_ASSET_ID);
            log.info("EDC Asset (DTR) created with id :{}", dtrAssetId);
        } catch (EdcAssetAlreadyExistsException e) {
            log.info("EDC Asset (DTR) already exists with id: {} reusing it", REGISTRY_ASSET_ID);
            dtrAssetId = REGISTRY_ASSET_ID;
        } catch (Exception exception) {
            log.warn("EDC Asset (DTR) could not be created: {}", exception.getMessage());
            throw new CreateEdcAssetException(exception);
        }

        try {
            String dtrContractId = edcContractDefinitionService.createContractDefinition(dtrAssetId, createdPolicyId);
            log.info("EDC ContractDefinition (DTR) created with id :{}", dtrContractId);
        } catch (Exception e) {
            log.warn("EDC ContractDefinition (DTR) could not be created: {}", e.getMessage());
            throw new CreateEdcContractDefinitionException(e);
        }

        String submodelAssetId;
        String submodelAssetIdToCreate = "urn:uuid:" + UUID.randomUUID();
        try {
            submodelAssetId = edcAssetService.createSubmodelAsset(submodelProperties.getBaseInternal(), submodelAssetIdToCreate);
            log.info("EDC Asset (Submodel) created with id :{}", submodelAssetId);
        } catch (EdcAssetAlreadyExistsException e) {
            submodelAssetId = submodelAssetIdToCreate;
            log.info("EDC Asset (Submodel) already exists with id: {} reusing it", submodelAssetId);
        } catch (Exception exception) {
            log.warn("EDC Asset (Submodel) could not be created: {}", exception.getMessage());
            throw new CreateEdcAssetException(exception);
        }

        try {
            String submodelContractId = edcContractDefinitionService.createContractDefinition(submodelAssetId, createdPolicyId);
            log.info("EDC ContractDefinition (Submodel) created with id :{}", submodelContractId);
        } catch (Exception e) {
            log.warn("EDC ContractDefinition (Submodel) could not be created: {}", e.getMessage());
            throw new CreateEdcContractDefinitionException(e);
        }

        return submodelAssetId;
    }
}
