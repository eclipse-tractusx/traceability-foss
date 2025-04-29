/********************************************************************************
 * Copyright (c) 2023,2024 Contributors to the Eclipse Foundation
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
import org.eclipse.tractusx.traceability.assets.application.importpoc.PublishService;
import org.eclipse.tractusx.traceability.assets.application.importpoc.PublishServiceAsync;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PublishAssetException;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState.ERROR;
import static org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState.TRANSIENT;


@Slf4j
@RequiredArgsConstructor
@Service
public class PublishServiceImpl implements PublishService {

    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final AssetAsBuiltRepository assetAsBuiltRepository;

    // Needed to make sure async method is async executed
    private final PublishServiceAsync publishServiceAsync;

    @Override
    public void publishAssets(final OrderConfiguration orderConfiguration, final TriggerConfiguration triggerConfiguration) {
        log.info("Start publish assets cron job");
        List<AssetBase> assetsAsBuiltInSync = assetAsBuiltRepository.findByImportStateIn(ImportState.IN_SYNCHRONIZATION);
        List<AssetBase> assetsAsPlannedInSync = assetAsPlannedRepository.findByImportStateIn(ImportState.IN_SYNCHRONIZATION);
        List<AssetBase> allInSyncAssets = Stream.concat(assetsAsPlannedInSync.stream(), assetsAsBuiltInSync.stream()).toList();
        log.info("Found following assets in state IN_SYNCHRONIZATION to publish {}", allInSyncAssets.stream().map(AssetBase::getId).toList());
        boolean triggerSynchronizeAssets = true;
        publishServiceAsync.publishAssetsToCoreServices(allInSyncAssets, triggerSynchronizeAssets, orderConfiguration, triggerConfiguration);
    }

    @Override
    @Transactional
    public void publishAssets(
            String policyId,
            List<String> assetIds,
            boolean triggerSynchronizeAssets,
            OrderConfiguration orderConfiguration,
            TriggerConfiguration triggerConfiguration) {
        assetIds.forEach(this::throwIfNotExists);

        //Update assets with policy id
        log.info("Updating status of asPlannedAssets.");
        List<AssetBase> updatedAsPlannedAssets = updateAssetWithStatusAndPolicy(policyId, assetIds, assetAsPlannedRepository);
        log.info("Updating status of asBuiltAssets.");
        List<AssetBase> updatedAsBuiltAssets = updateAssetWithStatusAndPolicy(policyId, assetIds, assetAsBuiltRepository);

        publishServiceAsync.publishAssetsToCoreServices(
                Stream.concat(updatedAsPlannedAssets.stream(), updatedAsBuiltAssets.stream()).toList(),
                triggerSynchronizeAssets, orderConfiguration, triggerConfiguration
        );
    }

    private void throwIfNotExists(String assetId) {
        if (!(assetAsBuiltRepository.existsById(assetId) || assetAsPlannedRepository.existsById(assetId))) {
            throw new PublishAssetException("No asset found with the provided ID: " + assetId);
        }
    }

    private List<AssetBase> updateAssetWithStatusAndPolicy(String policyId, List<String> assetIds, AssetRepository repository) {
        List<AssetBase> assetList = repository.getAssetsById(assetIds);
        List<AssetBase> saveList = assetList.stream()
                .filter(this::validTransientOrErrorState)
                .map(asset -> {
                    asset.setImportState(ImportState.IN_SYNCHRONIZATION);
                    asset.setImportNote(ImportNote.IN_SYNCHRONIZATION);
                    asset.setPolicyId(policyId);
                    return asset;
                }).toList();

        List<AssetBase> assetBases = repository.saveAll(saveList);

        log.info("Successfully set {} in status IN_SYNCHRONIZATION", assetBases.stream().map(AssetBase::getId).collect(Collectors.joining(", ")));
        return assetBases;
    }

    private boolean validTransientOrErrorState(AssetBase assetBase) {
        if (TRANSIENT.equals(assetBase.getImportState()) || ERROR.equals(assetBase.getImportState())) {
            return true;
        }
        throw new PublishAssetException("Asset with ID " + assetBase.getId() + " is not in TRANSIENT state.");
    }
}
