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
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.SemanticDataModelEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.common.date.DateUtil.toInstant;

@Getter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "assets_as_planned")
public class AssetAsPlannedEntity extends AssetBaseEntity {

    private Instant validityPeriodFrom;
    private Instant validityPeriodTo;
    private Instant functionValidUntil;
    private String function;
    private Instant functionValidFrom;
    private String catenaxSiteId;


    @ElementCollection
    @CollectionTable(name = "assets_as_planned_childs", joinColumns = {@JoinColumn(name = "asset_as_planned_id")})
    private List<AssetAsPlannedEntity.ChildDescription> childDescriptors;

    @ManyToMany(mappedBy = "assetsAsPlanned")
    private List<InvestigationEntity> investigations = new ArrayList<>();

    @ManyToMany(mappedBy = "assetsAsPlanned")
    private List<AlertEntity> alerts = new ArrayList<>();

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
        List<DetailAspectModel> detailAspectModels = asset.getDetailAspectModels();
        AsPlannedInfo asPlannedInfo = AsPlannedInfo.from(detailAspectModels);

        return AssetAsPlannedEntity.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .manufacturerId(asset.getManufacturerId())
                .nameAtManufacturer(asset.getNameAtManufacturer())
                .manufacturerPartId(asset.getManufacturerPartId())
                .manufacturerName(asset.getManufacturerName())
                .semanticModelId(asset.getSemanticModelId())
                .van(asset.getVan())
                .functionValidFrom(toInstant(asPlannedInfo.getFunctionValidFrom()))
                .function(asPlannedInfo.getFunction())
                .functionValidUntil(toInstant(asPlannedInfo.getFunctionValidUntil()))
                .validityPeriodFrom(toInstant(asPlannedInfo.getValidityPeriodFrom()))
                .validityPeriodTo(toInstant(asPlannedInfo.getValidityPeriodTo()))
                .owner(asset.getOwner())
                .classification(asset.getClassification())
                .childDescriptors(asset.getChildRelations().stream()
                        .map(child -> new ChildDescription(child.id(), child.idShort()))
                        .toList())
                .qualityType(asset.getQualityType())
                .activeAlert(asset.isActiveAlert())
                .inInvestigation(asset.isInInvestigation())
                .semanticDataModel(SemanticDataModelEntity.from(asset.getSemanticDataModel()))
                .catenaxSiteId(asPlannedInfo.getCatenaxSiteId())
                .assetState(asset.getAssetState())
                .build();
    }

    public static AssetBase toDomain(AssetAsPlannedEntity entity) {
        return AssetBase.builder()
                .id(entity.getId())
                .manufacturerPartId(entity.getManufacturerPartId())
                .nameAtManufacturer(entity.getNameAtManufacturer())
                .manufacturerName(entity.getManufacturerName())
                .manufacturerId(entity.getManufacturerId())
                .van(entity.getVan())
                .classification(entity.getClassification())
                .idShort(entity.getIdShort())
                .semanticDataModel(SemanticDataModelEntity.toDomain(entity.getSemanticDataModel()))
                .semanticModelId(entity.getSemanticModelId())
                .owner(entity.getOwner())
                .childRelations(entity.getChildDescriptors().stream()
                        .map(child -> new Descriptions(child.getId(), child.getIdShort()))
                        .toList())
                .inInvestigation(entity.isInInvestigation())
                .activeAlert(entity.isActiveAlert())
                .qualityType(entity.getQualityType())
                .detailAspectModels(DetailAspectModel.from(entity))
                .sentQualityAlerts(emptyIfNull(entity.alerts).stream().filter(alert -> NotificationSideBaseEntity.SENDER.equals(alert.getSide())).map(AlertEntity::toDomain).toList())
                .receivedQualityAlerts(emptyIfNull(entity.alerts).stream().filter(alert -> NotificationSideBaseEntity.RECEIVER.equals(alert.getSide())).map(AlertEntity::toDomain).toList())
                .sentQualityInvestigations(emptyIfNull(entity.investigations).stream().filter(alert -> NotificationSideBaseEntity.SENDER.equals(alert.getSide())).map(InvestigationEntity::toDomain).toList())
                .receivedQualityInvestigations(emptyIfNull(entity.investigations).stream().filter(alert -> NotificationSideBaseEntity.RECEIVER.equals(alert.getSide())).map(InvestigationEntity::toDomain).toList())
                .assetState(entity.getAssetState())

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
