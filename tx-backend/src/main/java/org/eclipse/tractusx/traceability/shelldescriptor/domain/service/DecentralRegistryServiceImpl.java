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

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.service.AssetAsPlannedServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.configuration.application.service.ConfigurationService;
import org.eclipse.tractusx.traceability.configuration.application.service.OrderService;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.shelldescriptor.application.DecentralRegistryService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Component
public class DecentralRegistryServiceImpl implements DecentralRegistryService {

    private final ConfigurationService configurationService;
    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final TaskScheduler taskScheduler;
    private final AssetAsBuiltServiceImpl assetAsBuiltService;
    private final AssetAsPlannedServiceImpl assetAsPlannedService;
    private final OrderService orderService;

    private ScheduledFuture<?> currentSchedule;

    @Override
    public void synchronizeAssets() {
        configurationService.getLatestTriggerConfiguration().ifPresentOrElse(trigger -> {
            String cronExpressionRegisterOrderTTLReached = trigger.getCronExpressionRegisterOrderTTLReached();
            log.info("Synchronizing assets with cron expression: {}", cronExpressionRegisterOrderTTLReached);
            try {
                scheduleJob(cronExpressionRegisterOrderTTLReached);
            } catch (Exception e) {
                throw new RuntimeException("Failed to schedule assets synchronization job, reason: %s".formatted(e.getMessage()));
            }
        }, () -> log.info("No trigger configuration found, skipping assets synchronization"));
    }

    private void scheduleJob(String cronExpressionRegisterOrderTTLReached) {
        if (currentSchedule != null && !currentSchedule.isCancelled()) {
            log.info("Cancelling the current schedule");
            currentSchedule.cancel(false);
        }

        currentSchedule = taskScheduler.schedule(this::executeJob,
                new CronTrigger(cronExpressionRegisterOrderTTLReached));
        log.info("Successfully scheduled assets synchronization job");
    }

    @Override
    @Transactional
    public void executeJob() {
        List<AssetBase> assetsAsBuiltToSynchronize = assetAsBuiltRepository.findAllExpired();
        log.info("Found {} assets as built to synchronize", assetsAsBuiltToSynchronize.size());
        List<String> assetsAsBuiltIds = assetsAsBuiltToSynchronize.stream().map(AssetBase::getId).toList();

        List<AssetBase> assetsAsPlannedToSynchronize = assetAsPlannedRepository.findAllExpired();
        log.info("Found {} assets as planned to synchronize", assetsAsPlannedToSynchronize.size());
        List<String> assetsAsPlannedIds = assetsAsPlannedToSynchronize.stream().map(AssetBase::getId).toList();

        Optional<OrderConfiguration> maybeLatestOrderConfiguration = configurationService.getLatestOrderConfiguration();

        String registerOrderResponseForAssetsAsBuilt =
                assetAsBuiltService.syncAssetsUsingIRSOrderAPI(assetsAsBuiltIds, maybeLatestOrderConfiguration);

        String registerOrderResponseForAssetsAsPlanned =
                assetAsPlannedService.syncAssetsUsingIRSOrderAPI(assetsAsPlannedIds, maybeLatestOrderConfiguration);

        if (!StringUtils.isEmpty(registerOrderResponseForAssetsAsBuilt)) {
            log.info("Order created: {}", registerOrderResponseForAssetsAsBuilt);

            orderService.persistOrder(Order.builder()
                    .id(registerOrderResponseForAssetsAsBuilt)
                    .orderConfiguration(maybeLatestOrderConfiguration.orElse(null))
                    .status(ProcessingState.INITIALIZED)
                    .build());

            log.info("Changing as built assets import state to IN_SYNCHRONIZATION after creating an order");
            updateAssetsStatus(assetsAsBuiltToSynchronize, assetAsBuiltRepository::saveAll);
        } else {
            log.warn("No order created for assets as planned, skipping assets synchronization");
        }

        if (!StringUtils.isEmpty(registerOrderResponseForAssetsAsPlanned)) {
            log.info("Order created: {}", registerOrderResponseForAssetsAsPlanned);
            orderService.persistOrder(Order.builder()
                    .id(registerOrderResponseForAssetsAsPlanned)
                    .orderConfiguration(maybeLatestOrderConfiguration.orElse(null))
                    .status(ProcessingState.INITIALIZED)
                    .build());

            log.info("Changing as planned assets import state to IN_SYNCHRONIZATION after creating an order");
            updateAssetsStatus(assetsAsPlannedToSynchronize, assetAsPlannedRepository::saveAll);
        } else {
            log.warn("No order created for assets as planned, skipping assets synchronization");
        }
    }

    private void updateAssetsStatus(List<AssetBase> assets, Consumer<List<AssetBase>> repository) {
        assets.forEach(assetBase -> assetBase.setImportState(ImportState.IN_SYNCHRONIZATION));
        repository.accept(assets);
    }
}
