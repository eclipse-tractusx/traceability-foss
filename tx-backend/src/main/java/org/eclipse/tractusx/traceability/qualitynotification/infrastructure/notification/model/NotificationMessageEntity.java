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
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification_message")
public class NotificationMessageEntity extends NotificationMessageBaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_id")
    private NotificationEntity notification;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assets_as_built_notification_messages",
            joinColumns = @JoinColumn(name = "notification_message_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetAsBuiltEntity> assets;

    public static QualityNotificationMessage toDomain(NotificationMessageEntity notificationMessageEntity) {
        return QualityNotificationMessage.builder()
                .id(notificationMessageEntity.getId())
                .notificationReferenceId(notificationMessageEntity.getNotificationReferenceId())
                .createdBy(notificationMessageEntity.getCreatedBy())
                .createdByName(notificationMessageEntity.getCreatedByName())
                .sendTo(notificationMessageEntity.getSendTo())
                .sendToName(notificationMessageEntity.getSendToName())
                .description(notificationMessageEntity.getNotification().getDescription())
                .contractAgreementId(notificationMessageEntity.getContractAgreementId())
                .notificationStatus(QualityNotificationStatus.fromStringValue(notificationMessageEntity.getStatus().name()))
                .affectedParts(notificationMessageEntity.getAssets().stream()
                        .map(asset -> new QualityNotificationAffectedPart(asset.getId()))
                        .toList())
                .targetDate(notificationMessageEntity.getTargetDate())
                .severity(notificationMessageEntity.getSeverity())
                .edcNotificationId(notificationMessageEntity.getEdcNotificationId())
                .messageId(notificationMessageEntity.getMessageId())
                .created(notificationMessageEntity.getCreated())
                .updated(notificationMessageEntity.getUpdated())
                .type(notificationMessageEntity.getNotification().getType().toDomain())
                .errorMessage(notificationMessageEntity.getErrorMessage())
                .build();
    }

    public static NotificationMessageEntity from(NotificationEntity notificationEntity,
                                                       QualityNotificationMessage qualityNotificationMessage,
                                                       List<AssetAsBuiltEntity> notificationAssets) {
        return NotificationMessageEntity
                .builder()
                .id(qualityNotificationMessage.getId())
                .notification(notificationEntity)
                .errorMessage(qualityNotificationMessage.getErrorMessage())
                .contractAgreementId(qualityNotificationMessage.getContractAgreementId())
                .created(qualityNotificationMessage.getCreated())
                .createdBy(qualityNotificationMessage.getCreatedBy())
                .createdByName(qualityNotificationMessage.getCreatedByName())
                .sendTo(qualityNotificationMessage.getSendTo())
                .sendToName(qualityNotificationMessage.getSendToName())
                .assets(notificationAssets)
                .notificationReferenceId(qualityNotificationMessage.getNotificationReferenceId())
                .targetDate(qualityNotificationMessage.getTargetDate())
                .severity(qualityNotificationMessage.getSeverity())
                .edcNotificationId(qualityNotificationMessage.getEdcNotificationId())
                .status(NotificationStatusBaseEntity.fromStringValue(qualityNotificationMessage.getNotificationStatus().name()))
                .messageId(qualityNotificationMessage.getMessageId())
                .build();
    }
}
