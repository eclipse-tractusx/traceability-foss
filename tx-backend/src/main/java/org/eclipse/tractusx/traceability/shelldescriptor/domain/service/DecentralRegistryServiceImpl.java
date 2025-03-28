/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.shelldescriptor.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.aas.application.service.AASService;
import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.aas.domain.model.TwinType;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.service.AssetAsPlannedServiceImpl;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.shelldescriptor.application.DecentralRegistryService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
@Component
public class DecentralRegistryServiceImpl implements DecentralRegistryService {

    private static final int BATCH_SIZE = 500;
    private static final long PAUSE_DURATION_MILLIS = 8 * 60 * 1000; // 8 minutes

    private final AssetAsBuiltServiceImpl assetAsBuiltService;
    private final AssetAsPlannedServiceImpl assetAsPlannedService;

    private final AASService aasService;

    @Override
    @Async(value = AssetsAsyncConfig.LOAD_SHELL_DESCRIPTORS_EXECUTOR)
    public void synchronizeAssets() {
        List<String> asBuiltAASIds = aasService.findByDigitalTwinType(TwinType.PART_INSTANCE)
                .stream()
                .map(AAS::getAasId)
                .toList();
        List<String> asPlannedAASIds = aasService.findByDigitalTwinType(TwinType.PART_TYPE)
                .stream()
                .map(AAS::getAasId)
                .toList();

        log.info("Try to sync {} aasIds asBuilt", asBuiltAASIds.size());
        processInBatches(asBuiltAASIds, assetAsBuiltService::syncAssetsAsyncUsingIRSOrderAPI);

        log.info("Try to sync {} aasIds asPlanned", asPlannedAASIds.size());
        processInBatches(asPlannedAASIds, assetAsPlannedService::syncAssetsAsyncUsingIRSOrderAPI);
    }

    private void processInBatches(List<String> ids, Consumer<List<String>> syncFunction) {
        for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, ids.size());
            List<String> batch = ids.subList(i, end);
            syncFunction.accept(batch);
            log.info("Processed {} - {} IDs", i + 1, end);

            // 5 minutes break after each registered order
            try {
                Thread.sleep(PAUSE_DURATION_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Pause interrupted", e);
            }
        }
    }
}

