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

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.asset.model.exception.CreateEdcAssetException;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.irs.registryclient.decentral.exception.CreateDtrShellException;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.service.DecentralRegistryServiceImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncPublishService {

    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final EdcAssetCreationService edcAssetCreationService;
    private final DtrService dtrService;
    private final DecentralRegistryServiceImpl decentralRegistryService;

    @Async(value = AssetsAsyncConfig.PUBLISH_ASSETS_EXECUTOR)
    public void publishAssetsToCoreServices(List<AssetBase> assets, boolean triggerSynchronizeAssets) {
        Map<String, List<AssetBase>> assetsByPolicyId = assets.stream().collect(Collectors.groupingBy(AssetBase::getPolicyId));

        List<String> createdShellsAssetIds = new ArrayList<>();
        assetsByPolicyId.forEach((policyId, assetsForPolicy) -> {
            String submodelServerAssetId = null;

            try {
                submodelServerAssetId = edcAssetCreationService.createEdcContractDefinitionsForDtrAndSubmodel(policyId);
            } catch (CreateEdcPolicyDefinitionException e) {
                log.error("Failed to create EDC Policy.", e);
                updateAssetStates(ImportState.ERROR, ImportNote.ERROR_EDC_POLICY_CREATION_FAILED, assetsForPolicy.stream().map(AssetBase::getId).toList());
            } catch (CreateEdcAssetException e) {
                log.error("Failed to create EDC Asset.", e);
                updateAssetStates(ImportState.ERROR, ImportNote.ERROR_EDC_ASSET_CREATION_FAILED, assetsForPolicy.stream().map(AssetBase::getId).toList());
            } catch (CreateEdcContractDefinitionException e) {
                log.error("Failed to create EDC Contract.", e);
                updateAssetStates(ImportState.ERROR, ImportNote.ERROR_EDC_CONTRACT_CREATION_FAILED, assetsForPolicy.stream().map(AssetBase::getId).toList());
            }

            if (Objects.nonNull(submodelServerAssetId)) {
                String tempSubmodelServerAssetId = submodelServerAssetId;
                assetsForPolicy.forEach(assetBase -> {
                    try {
                        String assetId = dtrService.createShellInDtr(assetBase, tempSubmodelServerAssetId);
                        createdShellsAssetIds.add(assetId);
                    } catch (CreateDtrShellException e) {
                        log.error("Failed to create shell in dtr for asset with id %s".formatted(assetBase.getId()), e);
                        updateAssetStates(ImportState.ERROR, ImportNote.ERROR_DTR_SHELL_CREATION_FAILED, List.of(assetBase.getId()));
                    }
                });

                updateAssetStates(ImportState.PUBLISHED_TO_CORE_SERVICES, ImportNote.PUBLISHED_TO_CORE_SERVICES, createdShellsAssetIds);
                if (triggerSynchronizeAssets) {
                    decentralRegistryService.synchronizeAssets();
                }
            }
        });
    }

    private void updateAssetStates(ImportState importState, String importNote, List<String> assetIds) {
        assetAsBuiltRepository.updateImportStateAndNoteForAssets(importState, importNote, assetIds);
        assetAsPlannedRepository.updateImportStateAndNoteForAssets(importState, importNote, assetIds);
    }
}
