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
package org.eclipse.tractusx.traceability.configuration.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderConfigurationEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderConfigurationJPARepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderConfigurationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConfigurationRepositoryImpl implements OrderConfigurationRepository {

    private final OrderConfigurationJPARepository orderConfigurationJPARepository;

    @Override
    public OrderConfiguration findTopByOrderIdOrderByCreatedAtDesc(Long orderId) {
        OrderConfiguration orderConfiguration = orderConfigurationJPARepository
                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .map(OrderConfigurationEntity::toDomain)
                .orElse(null);

        return orderConfiguration;
    }

    @Override
    public void save(OrderConfiguration orderConfiguration) {
        OrderConfigurationEntity entity = OrderConfigurationEntity.toEntity(orderConfiguration);
        orderConfigurationJPARepository.save(entity);
    }
}
