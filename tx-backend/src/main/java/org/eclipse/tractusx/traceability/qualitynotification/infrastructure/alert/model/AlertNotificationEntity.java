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
package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationMessageBaseEntity;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "alert_notification")
public class AlertNotificationEntity extends QualityNotificationMessageBaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "alert_id")
    private AlertEntity alert;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "asset_as_built_alert_notifications",
            joinColumns = @JoinColumn(name = "alert_notification_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetAsBuiltEntity> assets;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "asset_as_planned_alert_notifications",
            joinColumns = @JoinColumn(name = "alert_notification_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetAsPlannedEntity> assetsAsPlanned;

    public static QualityNotificationMessage toDomain(AlertNotificationEntity alertNotificationEntity) {
        return QualityNotificationMessage.builder()
                .id(alertNotificationEntity.getId())
                .notificationReferenceId(alertNotificationEntity.getNotificationReferenceId())
                .createdBy(alertNotificationEntity.getCreatedBy())
                .createdByName(alertNotificationEntity.getCreatedByName())
                .sendTo(alertNotificationEntity.getSendTo())
                .sendToName(alertNotificationEntity.getSendToName())
                .description(alertNotificationEntity.getAlert().getDescription())
                .edcUrl(alertNotificationEntity.getEdcUrl())
                .contractAgreementId(alertNotificationEntity.getContractAgreementId())
                .notificationStatus(QualityNotificationStatus.fromStringValue(alertNotificationEntity.getStatus().name()))
                .affectedParts(alertNotificationEntity.getAssets().stream()
                        .map(asset -> new QualityNotificationAffectedPart(asset.getId()))
                        .toList())
                .targetDate(alertNotificationEntity.getTargetDate())
                .severity(alertNotificationEntity.getSeverity())
                .edcNotificationId(alertNotificationEntity.getEdcNotificationId())
                .messageId(alertNotificationEntity.getMessageId())
                .created(alertNotificationEntity.getCreated())
                .updated(alertNotificationEntity.getUpdated())
                .isInitial(alertNotificationEntity.getIsInitial())
                .type(QualityNotificationType.ALERT)
                .build();
    }

    public static AlertNotificationEntity from(AlertEntity alertEntity,
                                               QualityNotificationMessage qualityNotificationMessage,
                                               List<AssetAsBuiltEntity> notificationAssets,
                                               List<AssetAsPlannedEntity> assetAsPlannedEntitiesByAlert) {
        return AlertNotificationEntity
                .builder()
                .id(qualityNotificationMessage.getId())
                .alert(alertEntity)
                .created(qualityNotificationMessage.getCreated())
                .createdBy(qualityNotificationMessage.getCreatedBy())
                .createdByName(qualityNotificationMessage.getCreatedByName())
                .sendTo(qualityNotificationMessage.getSendTo())
                .sendToName(qualityNotificationMessage.getSendToName())
                .assets(notificationAssets)
                .assetsAsPlanned(assetAsPlannedEntitiesByAlert)
                .notificationReferenceId(qualityNotificationMessage.getNotificationReferenceId())
                .targetDate(qualityNotificationMessage.getTargetDate())
                .severity(qualityNotificationMessage.getSeverity())
                .edcNotificationId(qualityNotificationMessage.getEdcNotificationId())
                .status(NotificationStatusBaseEntity.fromStringValue(qualityNotificationMessage.getNotificationStatus().name()))
                .messageId(qualityNotificationMessage.getMessageId())
                .isInitial(qualityNotificationMessage.getIsInitial())
                .build();
    }

}
