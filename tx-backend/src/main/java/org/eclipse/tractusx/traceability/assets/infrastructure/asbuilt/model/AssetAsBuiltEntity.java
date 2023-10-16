/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model;

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
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.SemanticDataModelEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.common.date.DateUtil.toInstant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "assets_as_built")
public class AssetAsBuiltEntity extends AssetBaseEntity {

    private Instant manufacturingDate;
    private String manufacturingCountry;
    private String manufacturerId;
    private String nameAtCustomer;
    private String customerPartId;
    private String productType;
    private String tractionBatteryCode;

    @ElementCollection
    @CollectionTable(name = "traction_battery_code_subcomponent", joinColumns = {@JoinColumn(name = "traction_battery_code")})
    private List<TractionBatteryCodeSubcomponents> subcomponents;


    @ElementCollection
    @CollectionTable(name = "assets_as_built_childs", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<AssetAsBuiltEntity.ChildDescription> childDescriptors;

    @ElementCollection
    @CollectionTable(name = "assets_as_built_parents", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<AssetAsBuiltEntity.ParentDescription> parentDescriptors;

    @ManyToMany(mappedBy = "assets")
    private List<InvestigationEntity> investigations = new ArrayList<>();

    @ManyToMany(mappedBy = "assets")
    private List<AlertEntity> alerts = new ArrayList<>();

    public static AssetAsBuiltEntity from(AssetBase asset) {
        ManufacturingInfo manufacturingInfo = ManufacturingInfo.from(asset.getDetailAspectModels());
        TractionBatteryCode tractionBatteryCodeObj = TractionBatteryCode.from(asset.getDetailAspectModels());

        return AssetAsBuiltEntity.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .nameAtManufacturer(asset.getNameAtManufacturer())
                .manufacturerPartId(manufacturingInfo.getManufacturerPartId())
                .semanticModelId(asset.getSemanticModelId())
                .manufacturerId(asset.getManufacturerId())
                .manufacturerName(asset.getManufacturerName())
                .nameAtCustomer(manufacturingInfo.getNameAtCustomer())
                .customerPartId(manufacturingInfo.getCustomerPartId())
                .manufacturingDate(toInstant(manufacturingInfo.getManufacturingDate()))
                .manufacturingCountry(manufacturingInfo.getManufacturingCountry())
                .owner(asset.getOwner())
                .childDescriptors(asset.getChildRelations().stream()
                        .map(child -> new ChildDescription(child.id(), child.idShort()))
                        .toList())
                .parentDescriptors(asset.getParentRelations().stream()
                        .map(parent -> new ParentDescription(parent.id(), parent.idShort()))
                        .toList())
                .qualityType(asset.getQualityType())
                .van(asset.getVan())
                .activeAlert(asset.isActiveAlert())
                .classification(asset.getClassification())
                .inInvestigation(asset.isUnderInvestigation())
                .semanticDataModel(SemanticDataModelEntity.from(asset.getSemanticDataModel()))
                .productType(tractionBatteryCodeObj.getProductType())
                .tractionBatteryCode(tractionBatteryCodeObj.getTractionBatteryCode())
                .subcomponents(tractionBatteryCodeObj.getSubcomponents())
                .build();
    }

    public static AssetBase toDomain(AssetAsBuiltEntity entity) {
        return AssetBase.builder()
                .id(entity.getId())
                .idShort(entity.getIdShort())
                .semanticDataModel(SemanticDataModelEntity.toDomain(entity.getSemanticDataModel()))
                .semanticModelId(entity.getSemanticModelId())
                .manufacturerId(entity.getManufacturerId())
                .manufacturerName(entity.getManufacturerName())
                .nameAtManufacturer(entity.getNameAtManufacturer())
                .manufacturerPartId(entity.getManufacturerPartId())
                .owner(entity.getOwner())
                .childRelations(entity.getChildDescriptors().stream()
                        .map(child -> new Descriptions(child.getId(), child.getIdShort()))
                        .toList())
                .parentRelations(entity.getParentDescriptors().stream()
                        .map(parent -> new Descriptions(parent.getId(), parent.getIdShort()))
                        .toList())
                .underInvestigation(entity.isInInvestigation())
                .activeAlert(entity.isActiveAlert())
                .qualityType(entity.getQualityType())
                .van(entity.getVan())
                .classification(entity.getClassification())
                .detailAspectModels(DetailAspectModel.from(entity))
                .qualityAlerts(emptyIfNull(entity.alerts).stream().map(AlertEntity::toDomain).toList())
                .qualityInvestigations(emptyIfNull(entity.investigations).stream().map(InvestigationEntity::toDomain).toList())

                .build();
    }

    public static List<AssetBase> toDomainList(List<AssetAsBuiltEntity> entities) {
        return entities.stream()
                .map(AssetAsBuiltEntity::toDomain)
                .toList();
    }

    public static List<AssetAsBuiltEntity> fromList(List<AssetBase> assets) {
        return assets.stream()
                .map(AssetAsBuiltEntity::from)
                .toList();
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class ChildDescription {
        private String id;
        private String idShort;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class ParentDescription {
        private String id;
        private String idShort;
    }


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class TractionBatteryCodeSubcomponents {
        private String subcomponentTractionBatteryCode;
        private String productType;
    }
}
