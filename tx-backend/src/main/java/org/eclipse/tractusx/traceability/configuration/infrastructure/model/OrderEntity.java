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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order;

@Entity
@Table(name = "orders")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrderEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private ProcessingState status;

    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long orderConfigurationId;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchEntity> batches;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    Set<AssetAsBuiltEntity> assetsAsBuilt;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    Set<AssetAsPlannedEntity> assetsAsPlanned;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    public void preCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addBatch(BatchEntity batch) {
        batch.setOrder(this);
        this.batches.add(batch);
    }

    public static Order toDomain(OrderEntity entity) {
        return Order.builder()
                .id(entity.getId())
                .message(entity.getMessage())
                .status(entity.getStatus())
                .batchList(entity.getBatches().stream()
                        .map(BatchEntity::toDomain)
                        .collect(Collectors.toList()))
                .partsAsBuilt(entity.getAssetsAsBuilt().stream()
                        .map(AssetAsBuiltEntity::toDomain)
                        .collect(Collectors.toSet()))
                .partsAsPlanned(entity.getAssetsAsPlanned().stream()
                        .map(AssetAsPlannedEntity::toDomain)
                        .collect(Collectors.toSet()))
                .build();
    }
}
