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
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.CreateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.EdcAssetAlreadyExistsException;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.EdcPolicyDefinitionAlreadyExists;
import org.eclipse.tractusx.irs.edc.client.policy.service.EdcPolicyDefinitionService;
import org.eclipse.tractusx.traceability.assets.application.importpoc.PolicyService;
import org.eclipse.tractusx.traceability.common.config.TestEdcAssetService;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EdcAssetCreationService {
    private static final String REGISTRY_ASSET_ID = "registry-asset";
    private final TestEdcAssetService edcDtrAssetService;
    private final EdcPolicyDefinitionService edcDtrPolicyDefinitionService;
    private final EdcContractDefinitionService edcDtrContractDefinitionService;
    private final TraceabilityProperties traceabilityProperties;
    @Value("${registry.urlWithPath}")
    String registryUrlWithPath = null;

    public String createDtrAndSubmodelAssets(String policyId) {
        String createdPolicyId;
        try {
            // TODO: get policy from policyService ( IRS policy store ) and map it to request
            createdPolicyId = edcDtrPolicyDefinitionService.createAccessPolicy(traceabilityProperties.getRightOperand(), policyId);
            log.info("DTR Policy Id created :{}", createdPolicyId);
        } catch (CreateEdcPolicyDefinitionException e) {
            throw new RuntimeException(e);
        } catch (EdcPolicyDefinitionAlreadyExists e) {
            createdPolicyId = policyId;
        }


        String dtrAssetId = null;
        try {
            dtrAssetId = edcDtrAssetService.createDtrAsset(registryUrlWithPath, REGISTRY_ASSET_ID);
            log.info("DTR Asset Id created :{}", dtrAssetId);
        } catch (CreateEdcAssetException e) {
            throw new RuntimeException(e);
        } catch (EdcAssetAlreadyExistsException e) {
            dtrAssetId = REGISTRY_ASSET_ID;
        }


        String dtrContractId = null;
        try {
            dtrContractId = edcDtrContractDefinitionService.createContractDefinition(dtrAssetId, createdPolicyId);
            log.info("DTR Contract Id created :{}", dtrContractId);
        } catch (CreateEdcContractDefinitionException e) {
            throw new RuntimeException(e);
        }


        String submodelAssetId = null;
        String submodelAssetIdToCreate = "urn:uuid:" + UUID.randomUUID();
        try {
            submodelAssetId = edcDtrAssetService.createSubmodelAsset(traceabilityProperties.getSubmodelBase() + "/api/submodel", submodelAssetIdToCreate);
            log.info("Submodel Asset Id created :{}", submodelAssetId);
        } catch (CreateEdcAssetException e) {
            throw new RuntimeException(e);
        } catch (EdcAssetAlreadyExistsException e) {
            submodelAssetId = submodelAssetIdToCreate;
        }


        String submodelContractId = null;
        try {
            submodelContractId = edcDtrContractDefinitionService.createContractDefinition(submodelAssetId, createdPolicyId);
        } catch (CreateEdcContractDefinitionException e) {
            throw new RuntimeException(e);
        }
        log.info("Submodel Contract Id created :{}", submodelContractId);
        return submodelAssetId;
    }
}
