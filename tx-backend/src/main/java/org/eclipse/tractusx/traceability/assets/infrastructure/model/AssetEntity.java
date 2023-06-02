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
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "asset")
public class AssetEntity {

    @Id
    private String id;
    private String idShort;
    private String nameAtManufacturer;
    private String manufacturerPartId;
    private String partInstanceId;
    private String manufacturerId;
    private String batchId;
    private String manufacturerName;
    private String nameAtCustomer;
    private String customerPartId;
    private Instant manufacturingDate;
    private String manufacturingCountry;
    private Owner owner;
    private QualityType qualityType;
    private String van;
    private boolean inInvestigation;

    @ElementCollection
    @CollectionTable(name = "asset_child_descriptors")
    private List<ChildDescription> childDescriptors;

    @ElementCollection
    @CollectionTable(name = "asset_parent_descriptors")
    private List<ParentDescription> parentDescriptors;

    @ManyToMany(mappedBy = "assets")
    private List<InvestigationEntity> investigations = new ArrayList<>();

    public static AssetEntity from(Asset asset) {
        return AssetEntity.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .nameAtManufacturer(asset.getNameAtManufacturer())
                .manufacturerPartId(asset.getManufacturerPartId())
                .partInstanceId(asset.getPartInstanceId())
                .manufacturerId(asset.getManufacturerId())
                .batchId(asset.getBatchId())
                .manufacturerName(asset.getManufacturerName())
                .nameAtCustomer(asset.getNameAtCustomer())
                .customerPartId(asset.getCustomerPartId())
                .manufacturingDate(asset.getManufacturingDate())
                .manufacturingCountry(asset.getManufacturingCountry())
                .owner(asset.getOwner())
                .childDescriptors(asset.getChildDescriptions().stream()
                        .map(child -> new ChildDescription(child.id(), child.idShort()))
                        .toList())
                .parentDescriptors(asset.getParentDescriptions().stream()
                        .map(parent -> new ParentDescription(parent.id(), parent.idShort()))
                        .toList())
                .qualityType(asset.getQualityType())
                .van(asset.getVan())
                .inInvestigation(asset.isUnderInvestigation())
                .build();
    }

    public static Asset toDomain(AssetEntity entity) {

        return Asset.builder()
                .id(entity.getId())
                .idShort(entity.getIdShort())
                .nameAtManufacturer(entity.getNameAtManufacturer())
                .manufacturerPartId(entity.getManufacturerPartId())
                .partInstanceId(entity.getPartInstanceId())
                .manufacturerId(entity.getManufacturerId())
                .batchId(entity.getBatchId())
                .manufacturerName(entity.getManufacturerName())
                .nameAtCustomer(entity.getNameAtCustomer())
                .customerPartId(entity.getCustomerPartId())
                .manufacturingDate(entity.getManufacturingDate())
                .manufacturingCountry(entity.getManufacturingCountry())
                .owner(entity.getOwner())
                .childDescriptions(entity.getChildDescriptors().stream()
                        .map(child -> new Descriptions(child.getId(), child.getIdShort()))
                        .toList())
                .parentDescriptions(entity.getParentDescriptors().stream()
                        .map(parent -> new Descriptions(parent.getId(), parent.getIdShort()))
                        .toList())
                .underInvestigation(entity.isInInvestigation())
                .qualityType(entity.getQualityType())
                .van(entity.getVan())
                .build();

    }


    public static List<Asset> toDomainList(List<AssetEntity> entities) {
        return entities.stream()
                .map(AssetEntity::toDomain)
                .toList();
    }

    public static List<AssetEntity> fromList(List<Asset> assets) {
        return assets.stream()
                .map(AssetEntity::from)
                .toList();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class ChildDescription {
        private String id;
        private String idShort;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @Embeddable
    public static class ParentDescription {
        private String id;
        private String idShort;
    }

}
