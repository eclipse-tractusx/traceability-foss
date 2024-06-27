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

package org.eclipse.tractusx.traceability.notification.infrastructure.notification.model;

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
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationId;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationNotFoundException;

import java.time.Instant;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.common.date.DateUtil.convertInstantToString;

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

    public static Notification toDomain(NotificationEntity notificationEntity) {
        List<NotificationMessage> messages = emptyIfNull(notificationEntity.getMessages()).stream()
                .map(NotificationMessageEntity::toDomain)
                .toList();

        List<String> assetIds = notificationEntity.getAssets().stream()
                .map(AssetAsBuiltEntity::getId)
                .toList();
        String initialReceiverBpn = notificationEntity.getInitialReceiverBpn();
        if (initialReceiverBpn == null) {
            initialReceiverBpn = notificationEntity.getBpn();
        }
        return Notification.builder()
                .title(notificationEntity.getTitle())
                .notificationId(new NotificationId(notificationEntity.getId()))
                .bpn(BPN.of(notificationEntity.getBpn()))
                .notificationStatus(NotificationStatus.fromStringValue(notificationEntity.getStatus().name()))
                .notificationSide(NotificationSide.valueOf(notificationEntity.getSide().name()))
                .createdAt(notificationEntity.getCreatedDate())
                .description(notificationEntity.getDescription())
                .notificationType(NotificationType.valueOf(notificationEntity.getType().name()))
                .affectedPartIds(assetIds)
                .sendTo(initialReceiverBpn)
                .targetDate(convertInstantToString(notificationEntity.getTargetDate()))
                .severity(NotificationSeverity.fromString(notificationEntity.getSeverity() != null ? notificationEntity.getSeverity().getRealName() : null))
                .notifications(messages)
                .updatedDate(notificationEntity.getUpdated() != null ? notificationEntity.getUpdated() : Instant.now())
                .initialReceiverBpns(List.of(initialReceiverBpn))
                .build();
    }

    public static NotificationEntity from(Notification notification, List<AssetAsBuiltEntity> assetEntities) {
        return NotificationEntity.builder()
                .title(notification.getTitle())
                .assets(assetEntities)
                .bpn(notification.getBpn())
                .targetDate(notification.getTargetDate() == null ? null : Instant.parse(notification.getTargetDate()))
                .description(notification.getDescription())
                .status(NotificationStatusBaseEntity.fromStringValue(notification.getNotificationStatus().name()))
                .side(NotificationSideBaseEntity.valueOf(notification.getNotificationSide().name()))
                .createdDate(notification.getCreatedAt())
                .severity(NotificationSeverityBaseEntity.fromString(notification.getSeverity() != null ? notification.getSeverity().getRealName() : null))
                .type(NotificationTypeEntity.from(notification.getNotificationType()))
                .initialReceiverBpn(notification.getInitialReceiverBpns().stream().findFirst().orElseThrow(() -> new NotificationNotFoundException("Initial Receiver BPN not found")))
                .updated(Instant.now())
                .build();
    }
}
