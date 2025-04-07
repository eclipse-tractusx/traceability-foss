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
package org.eclipse.tractusx.traceability.configuration.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.aas.application.service.AASService;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.eclipse.tractusx.traceability.shelldescriptor.application.DecentralRegistryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;

@Service
@RequiredArgsConstructor
@Slf4j
public class CronRegistrationService {

    private ScheduledFuture<?> currentScheduleCleanupFuture;
    private ScheduledFuture<?> currentScheduleLookupFuture;
    private ScheduledFuture<?> currentScheduleRegisterOrderFuture;
    @Qualifier("registerOrderScheduler")
    private final TaskScheduler registerOrderScheduler;
    @Qualifier("aasCleanupScheduler")
    private final TaskScheduler aasCleanupScheduler;
    @Qualifier("aasLookupScheduler")
    private final TaskScheduler aasLookupScheduler;

    private final AASService aasService;

    private final DecentralRegistryService decentralRegistryService;


    public void updateCronjobs(TriggerConfiguration triggerConfiguration, OrderConfiguration orderConfiguration) {
        updateAASLookupCron(triggerConfiguration);
        updateAASCleanupCron(triggerConfiguration);
        updateRegisterOrderCron(triggerConfiguration, orderConfiguration);
    }

    private void scheduleAASCleanupJob(String cronExpression) {
        if (currentScheduleCleanupFuture != null && !currentScheduleCleanupFuture.isCancelled()) {
            log.info("Cancelling the current schedule for aas cleanup");
            currentScheduleCleanupFuture.cancel(false);
        }

        currentScheduleCleanupFuture = aasCleanupScheduler.schedule(aasService::aasCleanup,
                new CronTrigger(cronExpression));
        log.info("Successfully scheduled aas cleanup job");
    }


    private void scheduleAASLookup(final String cronExpression, TriggerConfiguration triggerConfiguration) {
        if (currentScheduleLookupFuture != null && !currentScheduleLookupFuture.isCancelled()) {
            log.info("Cancelling the current schedule for aas lookup");
            currentScheduleLookupFuture.cancel(false);
        }

        currentScheduleLookupFuture = aasLookupScheduler.schedule(() -> aasService.aasLookup(triggerConfiguration),
                new CronTrigger(cronExpression));
        log.info("Successfully scheduled aas lookup job");
    }

    private void updateAASLookupCron(TriggerConfiguration triggerConfiguration) {
        String cronExpressionAASLookup = triggerConfiguration.getCronExpressionAASLookup();
        log.info("Scheduled aas lookup: {}", sanitize(cronExpressionAASLookup));
        try {
            scheduleAASLookup(cronExpressionAASLookup, triggerConfiguration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to schedule aas lookup job, reason: %s".formatted(e.getMessage()));
        }
    }

    private void updateAASCleanupCron(TriggerConfiguration triggerConfiguration) {
        String cronExpressionAASCleanup = triggerConfiguration.getCronExpressionAASCleanup();
        log.info("Scheduled aas cleanup: {}", sanitize(cronExpressionAASCleanup));
        try {
            scheduleAASCleanupJob(cronExpressionAASCleanup);
        } catch (Exception e) {
            throw new RuntimeException("Failed to schedule aas cleanup job, reason: %s".formatted(e.getMessage()));
        }
    }

    private void updateRegisterOrderCron(TriggerConfiguration triggerConfiguration, OrderConfiguration orderConfiguration) {
        String cronExpressionRegisterOrderTTLReached = triggerConfiguration.getCronExpressionRegisterOrderTTLReached();
        log.info("Register order for expired assets with cron expression: {}", sanitize(cronExpressionRegisterOrderTTLReached));
        try {
            scheduleRegisterOrderJob(cronExpressionRegisterOrderTTLReached, orderConfiguration);
        } catch (Exception e) {
            throw new RuntimeException("Failed to schedule register order for expired assets job, reason: %s".formatted(e.getMessage()));
        }
    }

    private void scheduleRegisterOrderJob(String cronExpressionRegisterOrderTTLReached, OrderConfiguration orderConfiguration) {
        if (currentScheduleRegisterOrderFuture != null && !currentScheduleRegisterOrderFuture.isCancelled()) {
            log.info("Cancelling the current schedule for register order for expired assets");
            currentScheduleRegisterOrderFuture.cancel(false);
        }

        currentScheduleRegisterOrderFuture = registerOrderScheduler.schedule(() -> decentralRegistryService.registerOrdersForExpiredAssets(orderConfiguration),
                new CronTrigger(cronExpressionRegisterOrderTTLReached));

        log.info("Successfully scheduled register order for expired assets job");
    }
}
