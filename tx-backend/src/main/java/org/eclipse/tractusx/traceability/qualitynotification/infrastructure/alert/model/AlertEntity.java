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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@Table(name = "alert")
public class AlertEntity extends QualityNotificationBaseEntity {

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assets_as_built_alerts",
            joinColumns = @JoinColumn(name = "alert_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetAsBuiltEntity> assets;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assets_as_planned_alerts",
            joinColumns = @JoinColumn(name = "alert_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetAsPlannedEntity> assetsAsPlanned;

    @OneToMany(mappedBy = "alert")
    private List<AlertNotificationEntity> notifications;

    public static QualityNotification toDomain(AlertEntity alertNotificationEntity) {
        List<QualityNotificationMessage> notifications = alertNotificationEntity.getNotifications().stream()
                .map(AlertNotificationEntity::toDomain)
                .toList();

        List<String> assetIds = alertNotificationEntity.getAssets().stream()
                .map(AssetAsBuiltEntity::getId)
                .toList();

        return QualityNotification.builder()
                .notificationId(new QualityNotificationId(alertNotificationEntity.getId()))
                .bpn(BPN.of(alertNotificationEntity.getBpn()))
                .notificationStatus(QualityNotificationStatus.fromStringValue(alertNotificationEntity.getStatus().name()))
                .notificationSide(QualityNotificationSide.valueOf(alertNotificationEntity.getSide().name()))
                .closeReason(alertNotificationEntity.getCloseReason())
                .acceptReason(alertNotificationEntity.getAcceptReason())
                .declineReason(alertNotificationEntity.getDeclineReason())
                .createdAt(alertNotificationEntity.getCreatedDate())
                .description(alertNotificationEntity.getDescription())
                .assetIds(assetIds)
                .notifications(notifications)
                .errorMessage(alertNotificationEntity.getErrorMessage())
                .build();
    }

    public static AlertEntity from(QualityNotification qualityNotification, List<AssetAsBuiltEntity> assetEntities) {
        return AlertEntity.builder()
                // TODO clarify how to handle assetsAsPlanned
                .assets(assetEntities)
                .bpn(qualityNotification.getBpn())
                .description(qualityNotification.getDescription())
                .status(QualityNotificationStatusBaseEntity.fromStringValue(qualityNotification.getNotificationStatus().name()))
                .side(QualityNotificationSideBaseEntity.valueOf(qualityNotification.getNotificationSide().name()))
                .createdDate(qualityNotification.getCreatedAt())
                .errorMessage(qualityNotification.getErrorMessage())
                .build();
    }

}
