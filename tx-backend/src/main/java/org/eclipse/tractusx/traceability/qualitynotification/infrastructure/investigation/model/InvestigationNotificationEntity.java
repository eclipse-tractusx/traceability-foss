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
package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.infrastructure.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationMessageBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "investigation_notification")
public class InvestigationNotificationEntity extends QualityNotificationMessageBaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "investigation_id")
    private InvestigationEntity investigation;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assets_as_built_notifications",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetAsBuiltEntity> assets;


    public static QualityNotificationMessage toDomain(InvestigationNotificationEntity investigationNotificationEntity) {
        return QualityNotificationMessage.builder()
                .id(investigationNotificationEntity.getId())
                .notificationReferenceId(investigationNotificationEntity.getNotificationReferenceId())
                .senderBpnNumber(investigationNotificationEntity.getSenderBpnNumber())
                .senderManufacturerName(investigationNotificationEntity.getSenderManufacturerName())
                .receiverBpnNumber(investigationNotificationEntity.getReceiverBpnNumber())
                .receiverManufacturerName(investigationNotificationEntity.getReceiverManufacturerName())
                .description(investigationNotificationEntity.getInvestigation().getDescription())
                .edcUrl(investigationNotificationEntity.getEdcUrl())
                .contractAgreementId(investigationNotificationEntity.getContractAgreementId())
                .notificationStatus(QualityNotificationStatus.fromStringValue(investigationNotificationEntity.getStatus().name()))
                .affectedParts(investigationNotificationEntity.getAssets().stream()
                        .map(asset -> new QualityNotificationAffectedPart(asset.getId()))
                        .toList())
                .targetDate(investigationNotificationEntity.getTargetDate())
                .severity(investigationNotificationEntity.getSeverity())
                .edcNotificationId(investigationNotificationEntity.getEdcNotificationId())
                .messageId(investigationNotificationEntity.getMessageId())
                .created(investigationNotificationEntity.getCreated())
                .updated(investigationNotificationEntity.getUpdated())
                .isInitial(investigationNotificationEntity.getIsInitial())
                .build();
    }

    public static InvestigationNotificationEntity from(InvestigationEntity investigationEntity,
                                                       QualityNotificationMessage qualityNotificationMessage,
                                                       List<AssetAsBuiltEntity> notificationAssets) {
        return InvestigationNotificationEntity
                .builder()
                .id(qualityNotificationMessage.getId())
                .investigation(investigationEntity)
                .created(qualityNotificationMessage.getCreated())
                .senderBpnNumber(qualityNotificationMessage.getSenderBpnNumber())
                .senderManufacturerName(qualityNotificationMessage.getSenderManufacturerName())
                .receiverBpnNumber(qualityNotificationMessage.getReceiverBpnNumber())
                .receiverManufacturerName(qualityNotificationMessage.getReceiverManufacturerName())
                .assets(notificationAssets)
                .notificationReferenceId(qualityNotificationMessage.getNotificationReferenceId())
                .targetDate(qualityNotificationMessage.getTargetDate())
                .severity(qualityNotificationMessage.getSeverity())
                .edcNotificationId(qualityNotificationMessage.getEdcNotificationId())
                .status(QualityNotificationStatusBaseEntity.fromStringValue(qualityNotificationMessage.getNotificationStatus().name()))
                .messageId(qualityNotificationMessage.getMessageId())
                .isInitial(qualityNotificationMessage.getIsInitial())
                .build();
    }

}
