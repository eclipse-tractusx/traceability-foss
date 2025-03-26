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

package org.eclipse.tractusx.traceability.configuration.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;

@Entity
@Table(name = "order_configuration")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderConfigurationEntity extends ConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true, nullable = false)
    @JsonBackReference
    private OrderEntity order;

    private int batchSize;

    private int timeoutMs;

    private int jobTimeoutMs;

    public static OrderConfiguration toDomain(OrderConfigurationEntity entity) {
        return OrderConfiguration.builder()
                .batchSize(entity.getBatchSize())
                .jobTimeoutMs(entity.getJobTimeoutMs())
                .timeoutMs(entity.getTimeoutMs())
                .id(entity.getId())
                .orderId(entity.getOrder() != null ? entity.getOrder().getId() : null)
                .build();
    }

    public static OrderConfigurationEntity toEntity(OrderConfiguration domain) {
        return OrderConfigurationEntity.builder()
                .batchSize(domain.getBatchSize())
                .jobTimeoutMs(domain.getJobTimeoutMs())
                .timeoutMs(domain.getTimeoutMs())
                .id(domain.getId())
                .build();
    }


}
