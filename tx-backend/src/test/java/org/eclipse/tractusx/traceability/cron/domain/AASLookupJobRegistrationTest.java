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

import org.eclipse.tractusx.traceability.aas.application.service.AASService;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AASLookupJobRegistrationTest {

    @Mock
    private AASService aasService;
    @Mock
    private TaskScheduler scheduler;
    @Mock
    private ScheduledFuture<?> scheduledFuture;
    @Mock
    private Config config;
    @Mock
    private TriggerConfiguration triggerConfiguration;

    @InjectMocks
    private AASLookupJobRegistration jobRegistration;


    @Test
    void testGetJobName() {
        assertThat(jobRegistration.getJobName()).isEqualTo("aas lookup");
    }

    @Test
    void testGetCronExpression() {

        when(config.getTriggerConfiguration()).thenReturn(triggerConfiguration);
        when(triggerConfiguration.getCronExpressionAASLookup()).thenReturn("0 0 * * * *");
        assertThat(jobRegistration.getCronExpression(config)).isEqualTo("0 0 * * * *");
    }

}
