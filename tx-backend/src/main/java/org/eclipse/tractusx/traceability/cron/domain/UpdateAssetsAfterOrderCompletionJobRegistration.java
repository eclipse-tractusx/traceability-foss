/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.cron.domain;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.service.AssetAsPlannedServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.configuration.application.service.OrderService;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order;
import org.eclipse.tractusx.traceability.cron.application.CronJobRegistration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateAssetsAfterOrderCompletionJobRegistration implements CronJobRegistration {

    private final OrderService orderService;
    private final AssetAsBuiltServiceImpl assetAsBuiltService;
    private final AssetAsPlannedServiceImpl assetAsPlannedService;
    private final TaskScheduler scheduler;
    private ScheduledFuture<?> future;
    private static final String JOB_NAME = "Update assets after order completion";

    @Override
    public String getJobName() {
        return JOB_NAME;
    }

    @Override
    public String getCronExpression(Config config) {
        return config.getTriggerConfiguration().getCronExpressionMapCompletedOrders();
    }

    @Override
    public void schedule(String cronExpression, Config config) {
        cancel();
        future = scheduler.schedule(this::updateAssetsAfterJobCompletion,  new CronTrigger(cronExpression));
        log.info("Successfully completion order mapping job");
    }

    @Transactional
    public void updateAssetsAfterJobCompletion() {
        List<Order> ordersByStatus = orderService
                .findOrdersByStatus(List.of(ProcessingState.COMPLETED.toString()));

        Set<AssetBase> assetsAsBuilt = ordersByStatus.stream()
                .flatMap(order -> order.getPartsAsBuilt().stream())
                .collect(Collectors.toSet());

        Set<AssetBase> assetsAsPlanned = ordersByStatus.stream()
                .flatMap(order -> order.getPartsAsPlanned().stream())
                .collect(Collectors.toSet());

        assetAsBuiltService.updateAssetsAfterJobCompletion(assetsAsBuilt);
        assetAsPlannedService.updateAssetsAfterJobCompletion(assetsAsPlanned);
    }

    @Override
    public void cancel() {
        if (future != null && !future.isCancelled()) {
            future.cancel(false);
            log.info("Cancelled completion order mapping job");
        }
    }
}
