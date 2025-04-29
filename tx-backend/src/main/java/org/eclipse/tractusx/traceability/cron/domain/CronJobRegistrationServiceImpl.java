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
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.eclipse.tractusx.traceability.cron.application.CronJobRegistration;
import org.eclipse.tractusx.traceability.cron.application.CronJobRegistrationService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;

@Service
@RequiredArgsConstructor
@Slf4j
public class CronJobRegistrationServiceImpl implements CronJobRegistrationService {
    private final List<CronJobRegistration> registeredCronJobs;

    @Override
    public void updateCronJobs(TriggerConfiguration triggerConfig, OrderConfiguration orderConfiguration) {
        Config config = Config.builder().triggerConfiguration(triggerConfig).orderConfiguration(orderConfiguration).build();
        for (CronJobRegistration registeredCronJob : registeredCronJobs) {
            String expression = registeredCronJob.getCronExpression(config);
            String jobName = registeredCronJob.getJobName();
            if (expression == null) {
                log.warn("[{}] No cron expression defined. Clear job...", jobName);
                registeredCronJob.cancel();
            } else {
                try {
                    registeredCronJob.schedule(expression, config);
                } catch (Exception e) {
                    throw new CronJobRegistrationException("Failed to schedule %s job: %s".formatted(registeredCronJob.getJobName(), e.getMessage()));
                }
            }
        }
    }
}
