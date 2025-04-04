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

package org.eclipse.tractusx.traceability.configuration.service;

import configuration.request.OrderConfigurationRequest;
import configuration.request.TriggerConfigurationRequest;
import java.util.Optional;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.service.ConfigurationServiceImpl;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderConfigurationRepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.TriggerConfigurationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {

    @Mock
    private OrderConfigurationRepository orderConfigurationRepository;

    @Mock
    private TriggerConfigurationRepository triggerConfigurationRepository;

    @InjectMocks
    private ConfigurationServiceImpl configurationServiceImpl;

    @Test
    void shouldSaveAndReturnOrderConfigurationEntity() {
        OrderConfigurationRequest request = OrderConfigurationRequest.builder()
                .batchSize(10)
                .timeoutMs(5000)
                .jobTimeoutMs(10000)
                .build();

        configurationServiceImpl.persistOrderConfiguration(request);

        verify(orderConfigurationRepository).save(OrderConfiguration.builder().batchSize(10).jobTimeoutMs(10000).timeoutMs(5000).build());
    }

    @Test
    void shouldReturnLatestOrderConfigurationEntity() {
        OrderConfiguration orderConfiguration = OrderConfiguration.builder().build();
        when(orderConfigurationRepository.findTopByCreatedAtDesc()).thenReturn(orderConfiguration);

        Optional<OrderConfiguration> result = configurationServiceImpl.getLatestOrderConfiguration();

        assertNotNull(result);
        verify(orderConfigurationRepository).findTopByCreatedAtDesc();
    }

    @Test
    void shouldSaveAndReturnTriggerConfigurationEntity() {
        TriggerConfigurationRequest request = TriggerConfigurationRequest.builder()
                .cronExpressionRegisterOrderTTLReached("0 0 * * *")
                .cronExpressionMapCompletedOrders("* * * * *")
                .partTTL(3600)
                .aasTTL(7200)
                .cronExpressionAASLookup("* * * * *")
                .cronExpressionAASCleanup("* * * * *")
                .build();

        configurationServiceImpl.persistTriggerConfiguration(request);

        verify(triggerConfigurationRepository).save(any(TriggerConfiguration.class));
    }

    @Test
    void shouldReturnLatestTriggerConfigurationEntity() {
        TriggerConfiguration triggerConfiguration = TriggerConfiguration.builder().build();
        when(triggerConfigurationRepository.findTopByCreatedAtDesc()).thenReturn(triggerConfiguration);

        Optional<TriggerConfiguration> result = configurationServiceImpl.getLatestTriggerConfiguration();

        assertNotNull(result);
        verify(triggerConfigurationRepository).findTopByCreatedAtDesc();
    }
}
