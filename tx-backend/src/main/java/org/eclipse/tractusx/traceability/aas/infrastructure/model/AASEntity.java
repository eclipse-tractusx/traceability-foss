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
package org.eclipse.tractusx.traceability.aas.infrastructure.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "aas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AASEntity {
    @Id
    @Column(name = "aas_id")
    private String aasId;

    @Column(name = "ttl", nullable = false)
    private Integer ttl;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "updated", nullable = false)
    private LocalDateTime updated;

    @Column(name = "expiration", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "actor", nullable = false)
    private String actor;

    @Column(name = "digital_twin_type", nullable = false)
    private String digitalTwinType;

    @Column(name = "bpn", nullable = false)
    private String bpn;

    @OneToOne
    @JoinColumn(name = "global_asset_id", referencedColumnName = "id", insertable = false, updatable = false)
    private AssetAsBuiltEntity assetAsBuilt;

    @OneToOne
    @JoinColumn(name = "global_asset_id", referencedColumnName = "id", insertable = false, updatable = false)
    private AssetAsPlannedEntity assetAsPlanned;

    public static AAS toDomain(AASEntity aasEntity) {
        return AAS.builder()
                .aasId(aasEntity.getAasId())
                .ttl(aasEntity.getTtl())
                .created(aasEntity.getCreated())
                .updated(aasEntity.getUpdated())
                .actor(AAS.actorFromString(aasEntity.getActor()))
                .digitalTwinType(AAS.digitalTwinTypeFromString(aasEntity.getDigitalTwinType()))
                .bpn(AAS.bpnFromString(aasEntity.getBpn()))
                .expiryDate(aasEntity.getExpiryDate())
                .build();
    }

    public static List<AAS> toDomainList(List<AASEntity> aasEntityList) {
        return aasEntityList.stream().map(AASEntity::toDomain).toList();
    }

    public static AASEntity from(AAS aas) {
        return AASEntity.builder()
                .aasId(aas.getAasId())
                .ttl(aas.getTtl())
                .created(aas.getCreated())
                .updated(aas.getUpdated())
                .actor(aas.getActor().name())
                .digitalTwinType(Optional.ofNullable(aas.getDigitalTwinType())
                        .map(Enum::name)
                        .orElse(null))
                .bpn(aas.getBpn().toString())
                .expiryDate(aas.getUpdated().plusSeconds(aas.getTtl()))
                .build();
    }

    public static List<AASEntity> fromList(List<AAS> aasList) {
        return aasList.stream().map(AASEntity::from).toList();
    }
}
