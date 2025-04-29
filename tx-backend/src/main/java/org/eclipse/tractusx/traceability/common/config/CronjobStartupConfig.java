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

package org.eclipse.tractusx.traceability.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.configuration.application.service.ConfigurationService;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.eclipse.tractusx.traceability.cron.domain.CronJobRegistrationServiceImpl;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Component
@RequiredArgsConstructor
public class CronjobStartupConfig {

    private final CronJobRegistrationServiceImpl cronRegistrationService;
    ;
    private final ConfigurationService configurationService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeCronJobs() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            log.info("on ApplicationReadyEvent create cron jobs.");
            try {
                TriggerConfiguration triggerConfig = configurationService.getLatestTriggerConfiguration();
                OrderConfiguration orderConfiguration = configurationService.getLatestOrderConfiguration();
                cronRegistrationService.updateCronJobs(triggerConfig, orderConfiguration);
            } catch (IllegalStateException e) {
                log.error("Required configuration for initializing cron jobs missing: {}", e.getMessage());
            } catch (Exception exception) {
                log.error("Failed to initialize cron jobs:", exception);
            }
            log.info("on ApplicationReadyEvent successfully initialized cron jobs.");
        });
        executor.shutdown();
    }

}
