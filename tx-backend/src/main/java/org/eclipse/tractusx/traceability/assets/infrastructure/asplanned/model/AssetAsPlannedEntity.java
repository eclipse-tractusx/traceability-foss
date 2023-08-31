/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.SemanticDataModelEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "assets_as_planned")
public class AssetAsPlannedEntity extends AssetBaseEntity {

    private LocalDateTime validityPeriodFrom;
    private LocalDateTime validityPeriodTo;

    @ElementCollection
    @CollectionTable(name = "assets_as_planned_childs", joinColumns = {@JoinColumn(name = "asset_as_planned_id")})
    private List<AssetAsPlannedEntity.ChildDescription> childDescriptors;

    @ManyToMany(mappedBy = "assetsAsPlanned")
    private List<InvestigationEntity> investigations = new ArrayList<>();

    @ManyToMany(mappedBy = "assetsAsPlanned")
    private List<AlertNotificationEntity> alertNotificationEntities = new ArrayList<>();

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class ChildDescription {
        private String id;
        private String idShort;
    }

    public static AssetAsPlannedEntity from(AssetBase asset) {
        // TODO add detailAspectModels



        return AssetAsPlannedEntity.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .validityPeriodFrom(// todo)
                .validityPeriodTo(// todo)
                .nameAtManufacturer(asset.getSemanticModel().getNameAtManufacturer())
                .manufacturerPartId(asset.getSemanticModel().getManufacturerPartId())
                .nameAtCustomer(asset.getSemanticModel().getNameAtCustomer())
                .customerPartId(asset.getSemanticModel().getCustomerPartId())
                .owner(asset.getOwner())
                .classification(asset.getClassification())
                .childDescriptors(asset.getChildRelations().stream()
                        .map(child -> new AssetAsPlannedEntity.ChildDescription(child.id(), child.idShort()))
                        .toList())
                .qualityType(asset.getQualityType())
                .activeAlert(asset.isActiveAlert())
                .inInvestigation(asset.isUnderInvestigation())
                .semanticDataModel(SemanticDataModelEntity.from(asset.getSemanticDataModel()))
                .build();
    }

    public static AssetBase toDomain(AssetAsPlannedEntity entity) {
// TODO add detailAspectModels
        return AssetBase.builder()
                .id(entity.getId())
                .classification(entity.getClassification())
                .idShort(entity.getIdShort())
                .semanticDataModel(SemanticDataModelEntity.toDomain(entity.getSemanticDataModel()))
                .owner(entity.getOwner())
                .childRelations(entity.getChildDescriptors().stream()
                        .map(child -> new Descriptions(child.getId(), child.getIdShort()))
                        .toList())
                .underInvestigation(entity.isInInvestigation())
                .activeAlert(entity.isActiveAlert())
                .qualityType(entity.getQualityType())
                .detailAspectModels()
                .build();
    }

    public static List<AssetBase> toDomainList(List<AssetAsPlannedEntity> entities) {
        return entities.stream()
                .map(org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity::toDomain)
                .toList();
    }

    public static List<AssetAsPlannedEntity> fromList(List<AssetBase> assets) {
        return assets.stream()
                .map(org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity::from)
                .toList();
    }
}
