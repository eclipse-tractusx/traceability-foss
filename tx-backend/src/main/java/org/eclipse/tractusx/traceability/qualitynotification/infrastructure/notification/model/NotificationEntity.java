/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@NoArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@Table(name = "notification")
public class NotificationEntity extends NotificationBaseEntity {


    @Enumerated(EnumType.STRING)
    private NotificationTypeEntity type;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assets_as_built_notifications",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetAsBuiltEntity> assets;

    @OneToMany(mappedBy = "notification")
    private List<NotificationMessageEntity> messages;

    public static QualityNotification toDomain(NotificationEntity notificationEntity) {
        List<QualityNotificationMessage> messages = emptyIfNull(notificationEntity.getMessages()).stream()
                .map(NotificationMessageEntity::toDomain)
                .toList();

        List<String> assetIds = notificationEntity.getAssets().stream()
                .map(AssetAsBuiltEntity::getId)
                .toList();
        return QualityNotification.builder()
                .title(notificationEntity.getTitle())
                .notificationId(new QualityNotificationId(notificationEntity.getId()))
                .bpn(BPN.of(notificationEntity.getBpn()))
                .notificationStatus(QualityNotificationStatus.fromStringValue(notificationEntity.getStatus().name()))
                .notificationSide(QualityNotificationSide.valueOf(notificationEntity.getSide().name()))
                .closeReason(notificationEntity.getCloseReason())
                .acceptReason(notificationEntity.getAcceptReason())
                .declineReason(notificationEntity.getDeclineReason())
                .createdAt(notificationEntity.getCreatedDate())
                .description(notificationEntity.getDescription())
                .notificationType(QualityNotificationType.valueOf(notificationEntity.getType().name()))
                .assetIds(assetIds)
                .notifications(messages)
                .build();
    }

    public static NotificationEntity from(QualityNotification notification, List<AssetAsBuiltEntity> assetEntities) {
        return NotificationEntity.builder()
                .title(notification.getTitle())
                .assets(assetEntities)
                .bpn(notification.getBpn())
                .description(notification.getDescription())
                .status(NotificationStatusBaseEntity.fromStringValue(notification.getNotificationStatus().name()))
                .side(NotificationSideBaseEntity.valueOf(notification.getNotificationSide().name()))
                .createdDate(notification.getCreatedAt())
                .type(NotificationTypeEntity.from(notification.getNotificationType()))
                .build();
    }
}
