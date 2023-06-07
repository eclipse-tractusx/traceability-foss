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

package org.eclipse.tractusx.traceability.assets.infrastructure.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.SemanticModel;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "assets_as_built")
public class AssetAsBuiltEntity extends AssetBaseEntity {

    @Id
    @Column(name = "id")
    private String id;

    @ElementCollection
    @CollectionTable(name = "assets_as_built_childs")
    private List<AssetAsBuiltEntity.ChildDescription> childDescriptors;

    @ElementCollection
    @CollectionTable(name = "assets_as_built_parents")
    private List<AssetAsBuiltEntity.ParentDescription> parentDescriptors;


    @ManyToMany(mappedBy = "assetsAsBuilt")
    private List<InvestigationEntity> investigations = new ArrayList<>();


    public static AssetAsBuiltEntity from(Asset asset) {
        return AssetAsBuiltEntity.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .nameAtManufacturer(asset.getSemanticModel().getNameAtManufacturer())
                .manufacturerPartId(asset.getSemanticModel().getManufacturerPartId())
                .semanticModelId(asset.getSemanticModelId())
                .manufacturerId(asset.getManufacturerId())
                .manufacturerName(asset.getManufacturerName())
                .nameAtCustomer(asset.getSemanticModel().getNameAtCustomer())
                .customerPartId(asset.getSemanticModel().getCustomerPartId())
                .manufacturingDate(asset.getSemanticModel().getManufacturingDate())
                .manufacturingCountry(asset.getSemanticModel().getManufacturingCountry())
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
                .inInvestigation(asset.isUnderInvestigation())
                .build();
    }

    public static Asset toDomain(AssetAsBuiltEntity entity) {
        return Asset.builder()
                .id(entity.getId())
                .idShort(entity.getIdShort())
                .semanticModel(SemanticModel.from(entity))
                .semanticModelId(entity.getSemanticModelId())
                .manufacturerId(entity.getManufacturerId())
                .manufacturerName(entity.getManufacturerName())
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
                .build();
    }

    public static List<Asset> toDomainList(List<AssetAsBuiltEntity> entities) {
        return entities.stream()
                .map(AssetAsBuiltEntity::toDomain)
                .toList();
    }

    public static List<AssetAsBuiltEntity> fromList(List<Asset> assets) {
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


}
