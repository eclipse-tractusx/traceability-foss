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

package org.eclipse.tractusx.traceability.integration.common.support;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderConfigurationJPARepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderJpaRepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.TriggerConfigurationJPARepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderConfigurationEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.TriggerConfigurationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigurationSupport {

    @Autowired
    OrderConfigurationJPARepository orderConfigurationRepository;

    @Autowired
    TriggerConfigurationJPARepository triggerConfigurationRepository;

    @Autowired
    OrderJpaRepository orderJpaRepository;

    public void storeOrder(OrderEntity orderEntity) {
        orderJpaRepository.save(orderEntity);
    }

    public void storeOrderConfiguration(OrderConfigurationEntity orderConfigurationEntity) {
        orderConfigurationRepository.save(orderConfigurationEntity);
    }

    public void storeTriggerConfiguration(TriggerConfigurationEntity entity) {
        triggerConfigurationRepository.save(entity);
    }

    public List<OrderConfigurationEntity> getOrderConfigurations() {
        return orderConfigurationRepository.findAll();
    }

    public List<TriggerConfigurationEntity> getTriggersConfigurations() {
        return triggerConfigurationRepository.findAll();
    }
}
