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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.OrderRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.configuration.application.service.OrderService;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public void persistOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public List<Order> findOrdersByStatus(List<String> statuses) {
        return orderRepository.findOrdersByStatus(statuses.stream().map(ProcessingState::valueOf).toList());
    }

    @Transactional
    @Override
    public void mapCompletedOrdersJobRegistration() {
        log.info("Starting job: Mapping completed orders with status '{}'.", ProcessingState.COMPLETED);

        List<Order> ordersByStatus = findOrdersByStatus(List.of(ProcessingState.COMPLETED.toString()));
        log.info("Found {} completed orders to process.", ordersByStatus.size());

        ordersByStatus.forEach(order -> {
            log.debug("Processing order with ID: {}", order.getId());

            order.getBatchList().forEach(batch -> {
                log.debug("Processing batch with ID: {} and status: {} for order ID: {}",
                        batch.getId(), batch.getStatus(), order.getId());

                orderRepository.requestOrderBatchAndMapAssets(order, batch.getId(), batch.getStatus());
                persistOrder(order);
                log.info("Successfully persisted assets in batch with ID: {} and status: {} for order ID: {}", batch.getId(), batch.getStatus(), order.getId());
            });
        });

        log.info("Completed job: Mapping of completed orders.");
    }
}
