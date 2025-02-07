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

package org.eclipse.tractusx.traceability.assets.domain.importpoc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.application.importpoc.PublishService;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.common.config.ApplicationProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@EnableScheduling
@Profile(ApplicationProfiles.NOT_TESTS)
@RequiredArgsConstructor
public class PublishAssetsJob {

    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final PublishService publishService;

    @Scheduled(cron = "${traceability.publishAssetCronExpression}", zone = "${traceability.publishAssetCronZone}")
    public void publishAssets() {
        log.info("Start publish assets cron job");
        List<AssetBase> assetsAsBuiltInSync = assetAsBuiltRepository.findByImportStateIn(ImportState.IN_SYNCHRONIZATION);
        List<AssetBase> assetsAsPlannedInSync = assetAsPlannedRepository.findByImportStateIn(ImportState.IN_SYNCHRONIZATION);
        List<AssetBase> allInSyncAssets = Stream.concat(assetsAsPlannedInSync.stream(), assetsAsBuiltInSync.stream()).toList();
        log.info("Found following assets in state IN_SYNCHRONIZATION to publish {}", allInSyncAssets.stream().map(AssetBase::getId).toList());
        boolean triggerSynchronizeAssets = true;
        publishService.publishAssetsToCoreServices(allInSyncAssets, triggerSynchronizeAssets);
    }
}
