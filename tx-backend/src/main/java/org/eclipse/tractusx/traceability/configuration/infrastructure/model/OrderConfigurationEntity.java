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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private static final int DEFAULT_BATCH_SIZE = 100;
    private static final int DEFAULT_JOB_TIMEOUT_MS = 7200;
    private static final int DEFAULT_TIMEOUT_MS = 86400;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer batchSize;

    private Integer timeoutMs;

    private Integer jobTimeoutMs;

    public static OrderConfiguration toDomain(OrderConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }

        return OrderConfiguration.builder()
                .batchSize(entity.getBatchSize())
                .jobTimeoutMs(entity.getJobTimeoutMs())
                .timeoutMs(entity.getTimeoutMs())
                .id(entity.getId())
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


    public static OrderConfigurationEntity defaultOrderConfigurationEntity() {
        return OrderConfigurationEntity.builder()
                .batchSize(DEFAULT_BATCH_SIZE)
                .jobTimeoutMs(DEFAULT_JOB_TIMEOUT_MS)
                .timeoutMs(DEFAULT_TIMEOUT_MS)
                .build();
    }

}
