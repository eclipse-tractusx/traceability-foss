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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model;

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
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@NoArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@Table(name = "investigation")
public class InvestigationEntity extends NotificationBaseEntity {

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assets_as_built_investigations",
            joinColumns = @JoinColumn(name = "investigation_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetAsBuiltEntity> assets;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assets_as_planned_investigations",
            joinColumns = @JoinColumn(name = "investigation_id"),
            inverseJoinColumns = @JoinColumn(name = "asset_id")
    )
    private List<AssetAsPlannedEntity> assetsAsPlanned;

    @OneToMany(mappedBy = "investigation")
    private List<InvestigationNotificationEntity> notifications;


    public static QualityNotification toDomain(InvestigationEntity investigationEntity) {
        List<QualityNotificationMessage> notifications = emptyIfNull(investigationEntity.getNotifications()).stream()
                .map(InvestigationNotificationEntity::toDomain)
                .toList();

        List<String> assetIds = investigationEntity.getAssets().stream()
                .map(AssetAsBuiltEntity::getId)
                .toList();

        return QualityNotification.builder()
                .notificationId(new QualityNotificationId(investigationEntity.getId()))
                .bpn(BPN.of(investigationEntity.getBpn()))
                .notificationStatus(QualityNotificationStatus.fromStringValue(investigationEntity.getStatus().name()))
                .notificationSide(QualityNotificationSide.valueOf(investigationEntity.getSide().name()))
                .closeReason(investigationEntity.getCloseReason())
                .acceptReason(investigationEntity.getAcceptReason())
                .declineReason(investigationEntity.getDeclineReason())
                .createdAt(investigationEntity.getCreatedDate())
                .description(investigationEntity.getDescription())
                .assetIds(assetIds)
                .notifications(notifications)
                .errorMessage(investigationEntity.getErrorMessage())
                .build();
    }

    public static InvestigationEntity from(QualityNotification qualityNotification, List<AssetAsBuiltEntity> assetEntities) {
        return InvestigationEntity.builder()
                .assets(assetEntities)
                // TODO clarify how to handle assetsAsPlanned
                .assetsAsPlanned(null)
                .bpn(qualityNotification.getBpn())
                .description(qualityNotification.getDescription())
                .status(NotificationStatusBaseEntity.fromStringValue(qualityNotification.getNotificationStatus().name()))
                .side(NotificationSideBaseEntity.valueOf(qualityNotification.getNotificationSide().name()))
                .createdDate(qualityNotification.getCreatedAt())
                .errorMessage(qualityNotification.getErrorMessage())
                .build();
    }

}
