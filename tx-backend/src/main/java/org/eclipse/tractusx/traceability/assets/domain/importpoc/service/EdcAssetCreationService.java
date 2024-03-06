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
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.service.EdcPolicyDefinitionService;
import org.eclipse.tractusx.traceability.common.config.TestContractDefinitionService;
import org.eclipse.tractusx.traceability.common.config.TestEdcAssetService;
import org.eclipse.tractusx.traceability.common.config.TestPolicyDefinitionService;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EdcAssetCreationService {
    private static final String REGISTRY_ASSET_NAME = "registry-asset";
    private static final String SUBMODEL_ASSET_NAME = "submodel-asset";
    private final TestEdcAssetService edcDtrAssetService;
    private final TestPolicyDefinitionService edcDtrPolicyDefinitionService;
    private final TestContractDefinitionService edcDtrContractDefinitionService;
    private final TraceabilityProperties traceabilityProperties;
    @Value("${registry.urlWithPath}")
    String registryUrlWithPath = null;

    public void createDtrAndSubmodelAssets() {
        // TODO: check if exists ( query catalog of parts provider EDC ) ?

        String createdPolicyId = null;
        try {
            createdPolicyId = edcDtrPolicyDefinitionService.createAccessPolicy(traceabilityProperties.getRightOperand());
        } catch (CreateEdcPolicyDefinitionException e) {
            throw new RuntimeException(e);
        }
        log.info("DTR Policy Id created :{}", createdPolicyId);

        String dtrAssetId = null;
        try {
            dtrAssetId = edcDtrAssetService.createDtrAsset(registryUrlWithPath, REGISTRY_ASSET_NAME);
        } catch (CreateEdcAssetException e) {
            throw new RuntimeException(e);
        }
        log.info("DTR Asset Id created :{}", dtrAssetId);

        String dtrContractId = null;
        try {
            dtrContractId = edcDtrContractDefinitionService.createContractDefinition(dtrAssetId, createdPolicyId);
        } catch (CreateEdcContractDefinitionException e) {
            throw new RuntimeException(e);
        }
        log.info("DTR Contract Id created :{}", dtrContractId);

        String submodelAssetId = null;

        try {
            submodelAssetId = edcDtrAssetService.createSubmodelAsset(traceabilityProperties.getSubmodelBase()+ "/api/submodel/data", SUBMODEL_ASSET_NAME);
        } catch (CreateEdcAssetException e) {
            throw new RuntimeException(e);
        }
        log.info("Submodel Asset Id created :{}", submodelAssetId);

        String submodelContractId = null;
        try {
            submodelContractId = edcDtrContractDefinitionService.createContractDefinition(submodelAssetId, createdPolicyId);
        } catch (CreateEdcContractDefinitionException e) {
            throw new RuntimeException(e);
        }
        log.info("Submodel Contract Id created :{}", submodelContractId);
    }
}
