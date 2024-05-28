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

import assets.importpoc.PolicyResponse;
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
import org.eclipse.tractusx.traceability.assets.application.importpoc.PolicyService;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.eclipse.tractusx.traceability.assets.application.importpoc.mapper.PolicyMapper.mapToEdcPolicyRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class EdcAssetCreationService {
    private static final String REGISTRY_ASSET_ID = "registry-asset";
    private final EdcAssetService edcAssetService;
    private final EdcPolicyDefinitionService edcPolicyDefinitionService;
    private final EdcContractDefinitionService edcContractDefinitionService;
    private final TraceabilityProperties traceabilityProperties;
    private final PolicyService policyService;
    @Value("${registry.urlWithPath}")
    String registryUrlWithPath = null;

    public String createEdcContractDefinitionsForDtrAndSubmodel(String policyId) throws CreateEdcPolicyDefinitionException, CreateEdcAssetException, CreateEdcContractDefinitionException {
        PolicyResponse policy = policyService.getPolicyById(policyId);
        String createdPolicyId;
        try {
            boolean exists = edcPolicyDefinitionService.policyDefinitionExists(policyId);
            if (exists) {
                log.info("Policy with id " + policyId + " already exists and contains necessary application constraints. Reusing for edc asset contract definition.");
                createdPolicyId =policyId;
            } else{
                createdPolicyId = edcPolicyDefinitionService.createAccessPolicy(mapToEdcPolicyRequest(policy));
                log.info("DTR Policy Id created :{}", createdPolicyId);
            }
        } catch (EdcPolicyDefinitionAlreadyExists e) {
            createdPolicyId = policyId;
        } catch (Exception exception) {
            throw new CreateEdcPolicyDefinitionException(exception);
        }

        String dtrAssetId;
        try {
            dtrAssetId = edcAssetService.createDtrAsset(registryUrlWithPath, REGISTRY_ASSET_ID);
            log.info("DTR Asset Id created :{}", dtrAssetId);
        } catch (EdcAssetAlreadyExistsException e) {
            dtrAssetId = REGISTRY_ASSET_ID;
        } catch (Exception exception) {
            throw new CreateEdcAssetException(exception);
        }

        try {
            String dtrContractId = edcContractDefinitionService.createContractDefinition(dtrAssetId, createdPolicyId);
            log.info("DTR Contract Id created :{}", dtrContractId);
        } catch (Exception e) {
            throw new CreateEdcContractDefinitionException(e);
        }

        String submodelAssetId;
        String submodelAssetIdToCreate = "urn:uuid:" + UUID.randomUUID();
        try {
            submodelAssetId = edcAssetService.createSubmodelAsset(traceabilityProperties.getSubmodelBase() + traceabilityProperties.getSubmodelPath(), submodelAssetIdToCreate);
            log.info("Submodel Asset Id created :{}", submodelAssetId);
        } catch (EdcAssetAlreadyExistsException e) {
            submodelAssetId = submodelAssetIdToCreate;
        } catch (Exception exception) {
            throw new CreateEdcAssetException(exception);
        }

        try {
            String submodelContractId = edcContractDefinitionService.createContractDefinition(submodelAssetId, createdPolicyId);
            log.info("Submodel Contract Id created :{}", submodelContractId);
        } catch (Exception e) {
            throw new CreateEdcContractDefinitionException(e);
        }

        return submodelAssetId;
    }
}
