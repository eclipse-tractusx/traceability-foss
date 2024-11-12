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

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.SemanticDataModelEntity;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementAsBuiltEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.submodel.infrastructure.model.SubmodelPayloadEntity;

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
@ToString
@Table(name = "assets_as_built")
public class AssetAsBuiltEntity extends AssetBaseEntity {

    private Instant manufacturingDate;
    private String manufacturingCountry;
    private String nameAtCustomer;
    private String customerPartId;
    private String productType;

    @ElementCollection
    @CollectionTable(name = "traction_battery_code_subcomponent", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<TractionBatteryCodeSubcomponents> subcomponents;

    @ElementCollection
    @CollectionTable(name = "assets_as_built_childs", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<AssetAsBuiltEntity.ChildDescription> childDescriptors;

    @ElementCollection
    @CollectionTable(name = "assets_as_built_parents", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<AssetAsBuiltEntity.ParentDescription> parentDescriptors;

    @ManyToMany(mappedBy = "assets")
    private List<NotificationEntity> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "assetAsBuilt", fetch = FetchType.EAGER)
    private List<SubmodelPayloadEntity> submodels;

    @OneToMany(mappedBy = "assetAsBuilt", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ContractAgreementAsBuiltEntity> contractAgreements;

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
                .digitalTwinType(asset.getDigitalTwinType())
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
                .classification(asset.getClassification())
                .semanticDataModel(SemanticDataModelEntity.from(asset.getSemanticDataModel()))
                .productType(tractionBatteryCodeObj.getProductType())
                .subcomponents(tractionBatteryCodeObj.getSubcomponents())
                .importState(asset.getImportState())
                .importNote(asset.getImportNote())
                .policyId(asset.getPolicyId())
                .tombstone(asset.getTombstone())
                .contractAgreements(ContractAgreementAsBuiltEntity.fromDomainToEntityList(asset.getContractAgreements()))
                .build();
    }

    public AssetBase toDomain() {
        return AssetBase.builder()
                .id(this.getId())
                .idShort(this.getIdShort())
                .semanticDataModel(SemanticDataModelEntity.toDomain(this.getSemanticDataModel()))
                .semanticModelId(this.getSemanticModelId())
                .manufacturerId(this.getManufacturerId())
                .digitalTwinType(this.getDigitalTwinType())
                .manufacturerName(this.getManufacturerName())
                .nameAtManufacturer(this.getNameAtManufacturer())
                .manufacturerPartId(this.getManufacturerPartId())
                .owner(this.getOwner())
                .childRelations(this.getChildDescriptors().stream()
                        .map(child -> new Descriptions(child.getId(), child.getIdShort(), null, null))
                        .toList())
                .parentRelations(this.getParentDescriptors().stream()
                        .map(parent -> new Descriptions(parent.getId(), parent.getIdShort(), null, null))
                        .toList())
                .qualityType(this.getQualityType())
                .van(this.getVan())
                .classification(this.getClassification())
                .detailAspectModels(DetailAspectModel.from(this))
                .sentQualityAlerts(
                        emptyIfNull(this.notifications).stream()
                                .filter(notification -> NotificationSideBaseEntity.SENDER.equals(notification.getSide()))
                                .filter(notification -> NotificationTypeEntity.ALERT.equals(notification.getType()))
                                .map(NotificationEntity::toDomain)
                                .toList()
                )
                .receivedQualityAlerts(
                        emptyIfNull(this.notifications).stream()
                                .filter(notification -> NotificationSideBaseEntity.RECEIVER.equals(notification.getSide()))
                                .filter(notification -> NotificationTypeEntity.ALERT.equals(notification.getType()))
                                .map(NotificationEntity::toDomain)
                                .toList()
                )
                .sentQualityInvestigations(
                        emptyIfNull(this.notifications).stream()
                                .filter(notification -> NotificationSideBaseEntity.SENDER.equals(notification.getSide()))
                                .filter(notification -> NotificationTypeEntity.INVESTIGATION.equals(notification.getType()))
                                .map(NotificationEntity::toDomain)
                                .toList()
                )
                .receivedQualityInvestigations(
                        emptyIfNull(this.notifications).stream()
                                .filter(notification -> NotificationSideBaseEntity.RECEIVER.equals(notification.getSide()))
                                .filter(notification -> NotificationTypeEntity.INVESTIGATION.equals(notification.getType()))
                                .map(NotificationEntity::toDomain)
                                .toList()
                )
                .importState(this.getImportState())
                .importNote(this.getImportNote())
                .policyId(this.getPolicyId())
                .tombstone(this.getTombstone())
                .contractAgreements(ContractAgreement.fromAsBuiltEntityToContractAgreements(emptyIfNull(this.getContractAgreements())))
                .build();
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
        @Column(name = "traction_battery_code")
        private String tractionBatteryCode;
        private String subcomponentTractionBatteryCode;
        private String productType;
    }
}
