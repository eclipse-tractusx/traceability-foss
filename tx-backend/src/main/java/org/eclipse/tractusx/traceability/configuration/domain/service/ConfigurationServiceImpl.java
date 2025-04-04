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

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.eclipse.tractusx.traceability.configuration.application.service.ConfigurationService;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import configuration.request.OrderConfigurationRequest;
import configuration.request.TriggerConfigurationRequest;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderConfigurationRepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.TriggerConfigurationRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

    private final OrderConfigurationRepository orderConfigurationRepository;

    private final TriggerConfigurationRepository triggerConfigurationRepository;

    @Override
    public void persistOrderConfiguration(OrderConfigurationRequest request) {
        OrderConfiguration orderConfiguration = OrderConfiguration.builder()
                .batchSize(request.getBatchSize())
                .jobTimeoutMs(request.getJobTimeoutMs())
                .timeoutMs(request.getTimeoutMs())
                .build();
        orderConfigurationRepository.save(orderConfiguration);
    }

    @Override
    public Optional<OrderConfiguration> getLatestOrderConfiguration() {
        return Optional.ofNullable(orderConfigurationRepository.findTopByCreatedAtDesc());
    }

    @Override
    public void persistTriggerConfiguration(TriggerConfigurationRequest request) {
        TriggerConfiguration triggerConfiguration = TriggerConfiguration.builder()
                .partTTL(request.getPartTTL())
                .cronExpressionRegisterOrderTTLReached(request.getCronExpressionRegisterOrderTTLReached())
                .cronExpressionMapCompletedOrders(request.getCronExpressionMapCompletedOrders())
                .cronExpressionAASLookup(request.getCronExpressionAASLookup())
                .cronExpressionAASCleanup(request.getCronExpressionAASCleanup())
                .aasTTL(request.getAasTTL())
                .build();

        triggerConfigurationRepository.save(triggerConfiguration);
    }

    @Override
    public Optional<TriggerConfiguration> getLatestTriggerConfiguration() {
        return Optional.ofNullable(triggerConfigurationRepository.findTopByCreatedAtDesc());

    }
}
