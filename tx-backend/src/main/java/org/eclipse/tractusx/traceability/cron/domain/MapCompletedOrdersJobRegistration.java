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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.configuration.application.service.OrderService;
import org.eclipse.tractusx.traceability.cron.application.CronJobRegistration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class MapCompletedOrdersJobRegistration implements CronJobRegistration {

    private final OrderService orderService;
    private final TaskScheduler scheduler;
    private ScheduledFuture<?> future;
    private static final String JOB_NAME = "fetch-batches-completed-or-partially-completed";

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
        future = scheduler.schedule(orderService::mapCompletedOrdersJobRegistration, new CronTrigger(cronExpression));
        log.info("[{}] scheduled with cron expression '{}'", JOB_NAME, cronExpression);
    }


    @Override
    public void cancel() {
        if (future != null && !future.isCancelled()) {
            future.cancel(false);
            log.info("[{}] schedule has been removed.", JOB_NAME);
        }
    }
}
