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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability.cron.domain;

import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.eclipse.tractusx.traceability.cron.application.CronJobRegistration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CronJobRegistrationServiceImplTest {

    @Test
    void updateCronJobs_shouldScheduleJobsWithValidExpressions() {
        CronJobRegistration mockRegistrar = mock(CronJobRegistration.class);
        when(mockRegistrar.getCronExpression(any())).thenReturn("0 0/5 * * * ?");
        when(mockRegistrar.getJobName()).thenReturn("TestJob");

        List<CronJobRegistration> registrars = List.of(mockRegistrar);
        CronJobRegistrationServiceImpl service = new CronJobRegistrationServiceImpl(registrars);

        TriggerConfiguration triggerConfig = TriggerConfiguration.builder().build();
        OrderConfiguration orderConfiguration = OrderConfiguration.builder().build();
        service.updateCronJobs(triggerConfig, orderConfiguration);
        Config config = Config.builder().triggerConfiguration(triggerConfig).orderConfiguration(orderConfiguration).build();
        verify(mockRegistrar, times(1)).schedule("0 0/5 * * * ?", config);
        verify(mockRegistrar, never()).cancel();
    }

    @Test
    void updateCronJobs_shouldCancelJobsWhenExpressionIsNull() {
        CronJobRegistration mockRegistrar = mock(CronJobRegistration.class);
        when(mockRegistrar.getCronExpression(any())).thenReturn(null);
        when(mockRegistrar.getJobName()).thenReturn("TestJob");

        List<CronJobRegistration> registrars = List.of(mockRegistrar);
        CronJobRegistrationServiceImpl service = new CronJobRegistrationServiceImpl(registrars);

        TriggerConfiguration triggerConfig = TriggerConfiguration.builder().build();
        OrderConfiguration orderConfiguration = OrderConfiguration.builder().build();
        service.updateCronJobs(triggerConfig, orderConfiguration);

        verify(mockRegistrar, never()).schedule(anyString(), any(Config.class));
        verify(mockRegistrar, times(1)).cancel();
    }

    @Test
    void updateCronJobs_shouldThrowExceptionWhenSchedulingFails() {
        CronJobRegistration mockRegistrar = mock(CronJobRegistration.class);
        when(mockRegistrar.getCronExpression(any())).thenReturn("invalid expression");
        when(mockRegistrar.getJobName()).thenReturn("TestJob");
        doThrow(new RuntimeException("Invalid cron expression")).when(mockRegistrar).schedule(eq("invalid expression"), any(Config.class));

        List<CronJobRegistration> registrars = List.of(mockRegistrar);
        CronJobRegistrationServiceImpl service = new CronJobRegistrationServiceImpl(registrars);

        TriggerConfiguration triggerConfig = TriggerConfiguration.builder().build();

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                service.updateCronJobs(triggerConfig, OrderConfiguration.builder().build()));

        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().contains("Failed to schedule TestJob job"));
        verify(mockRegistrar, times(1)).schedule(eq("invalid expression"), any(Config.class));
    }
}
