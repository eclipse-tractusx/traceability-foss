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

import jakarta.persistence.Column;
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
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;

@Entity
@Table(name = "trigger_configuration")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TriggerConfigurationEntity extends ConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cron_expression_register_order_ttl_reached")
    private String cronExpressionRegisterOrderTTLReached;

    @Column(name = "cron_expression_map_completed_orders")
    private String cronExpressionMapCompletedOrders;

    @Column(name = "cron_expression_aas_lookup_ttl_reached")
    private String cronExpressionAASLookup;

    @Column(name = "cron_expression_aas_cleanup_ttl_reached")
    private String cronExpressionAASCleanup;

    @Column(name = "part_ttl")
    private int partTTL;

    @Column(name = "aas_ttl")
    private int aasTTL;

    public static TriggerConfiguration toDomain(TriggerConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }
        return TriggerConfiguration.builder()
                .id(entity.getId())
                .aasTTL(entity.getAasTTL())
                .partTTL(entity.getPartTTL())
                .cronExpressionMapCompletedOrders(entity.getCronExpressionMapCompletedOrders())
                .cronExpressionRegisterOrderTTLReached(entity.getCronExpressionRegisterOrderTTLReached())
                .cronExpressionAASLookup(entity.getCronExpressionAASLookup())
                .cronExpressionAASCleanup(entity.getCronExpressionAASCleanup())
                .build();
    }

    public static TriggerConfigurationEntity toEntity(TriggerConfiguration domain) {
        return TriggerConfigurationEntity.builder()
                .id(domain.getId())
                .aasTTL(domain.getAasTTL())
                .partTTL(domain.getPartTTL())
                .cronExpressionMapCompletedOrders(domain.getCronExpressionMapCompletedOrders())
                .cronExpressionRegisterOrderTTLReached(domain.getCronExpressionRegisterOrderTTLReached())
                .cronExpressionAASLookup(domain.getCronExpressionAASLookup())
                .cronExpressionAASCleanup(domain.getCronExpressionAASCleanup())
                .build();
    }


}
