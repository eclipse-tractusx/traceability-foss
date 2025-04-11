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
import org.eclipse.tractusx.traceability.aas.application.service.AASService;
import org.eclipse.tractusx.traceability.cron.application.CronJobRegistration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class AASCleanupJobRegistration implements CronJobRegistration {
    private final AASService aasService;
    private final TaskScheduler scheduler;
    private ScheduledFuture<?> future;
    private static final String JOB_NAME = "aas cleanup";

    @Override
    public String getJobName() {
        return JOB_NAME;
    }

    @Override
    public String getCronExpression(Config config) {
        return config.getTriggerConfiguration().getCronExpressionAASCleanup();
    }

    @Override
    public void schedule(String cronExpression, Config config) {
        cancel();
        future = scheduler.schedule(aasService::aasCleanup, new CronTrigger(cronExpression));
        log.info("Successfully scheduled aas cleanup job");
    }

    @Override
    public void cancel() {
        if (future != null && !future.isCancelled()) {
            future.cancel(false);
            log.info("Cancelled aas cleanup job");
        }
    }
}
