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

package org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementAsBuiltEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationTypeEntity;
import org.springframework.data.annotation.Immutable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "assets_as_built_view")
@Immutable
public class AssetAsBuiltViewEntity extends AssetBaseEntity {

    private Instant manufacturingDate;
    private String manufacturingCountry;
    private String nameAtCustomer;
    private String customerPartId;
    private String productType;
    private String receivedActiveAlerts;
    private String sentActiveAlerts;
    private String receivedActiveInvestigations;
    private String sentActiveInvestigations;

    @ElementCollection
    @CollectionTable(name = "traction_battery_code_subcomponent", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<TractionBatteryCodeSubcomponents> subcomponents;


    @ElementCollection
    @CollectionTable(name = "assets_as_built_childs", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<AssetAsBuiltEntity.ChildDescription> childDescriptors;

    @ElementCollection
    @CollectionTable(name = "assets_as_built_parents", joinColumns = {@JoinColumn(name = "asset_as_built_id")})
    private List<AssetAsBuiltEntity.ParentDescription> parentDescriptors;

    @ManyToMany
    @JoinTable(
            name = "assets_as_built_notifications",
            joinColumns = @JoinColumn(name = "asset_id"),
            inverseJoinColumns = @JoinColumn(name = "notification_id"))
    private List<NotificationEntity> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "assetAsBuiltView", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ContractAgreementAsBuiltEntity> contractAgreements;

    public AssetBase toDomain() {
        return AssetBase.builder()
                .id(this.getId())
                .idShort(this.getIdShort())
                .semanticDataModel(SemanticDataModelEntity.toDomain(this.getSemanticDataModel()))
                .semanticModelId(this.getSemanticModelId())
                .manufacturerId(this.getManufacturerId())
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
                .importNote(this.getImportNote())
                .importState(this.getImportState())
                .policyId(this.getPolicyId())
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
                .tombstone(this.getTombstone())
                .contractAgreements(ContractAgreement.fromAsBuiltEntityToContractAgreements(this.getContractAgreements()))
                .build();
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
