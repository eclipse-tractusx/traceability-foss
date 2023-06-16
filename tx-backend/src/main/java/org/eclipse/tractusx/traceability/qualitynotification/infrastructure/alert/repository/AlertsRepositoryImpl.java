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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.repository.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlertsRepositoryImpl implements AlertRepository {

    private final JpaAlertRepository jpaAlertRepository;

/*    private final JpaAssetAsBuiltRepository assetsRepository;*/

    private final JpaAlertNotificationRepository notificationRepository;

    private final Clock clock;

    @Override
    public void updateQualityNotificationMessageEntity(QualityNotificationMessage notification) {
        AlertNotificationEntity entity = notificationRepository.findById(notification.getId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Notification with id %s not found!", notification.getId())));
        handleNotificationUpdate(entity, notification);
    }

    @Override
    public QualityNotificationId updateQualityNotificationEntity(QualityNotification alert) {
        AlertEntity alertEntity = jpaAlertRepository.findById(alert.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Alert with id %s not found!", alert.getNotificationId().value())));

        alertEntity.setStatus(QualityNotificationStatusBaseEntity.fromStringValue(alert.getNotificationStatus().name()));
        alertEntity.setUpdated(clock.instant());
        alertEntity.setCloseReason(alert.getCloseReason());
        alertEntity.setAcceptReason(alert.getAcceptReason());
        alertEntity.setDeclineReason(alert.getDeclineReason());

        handleNotificationUpdate(alertEntity, alert);
        jpaAlertRepository.save(alertEntity);

        return alert.getNotificationId();
    }

    @Override
    public QualityNotificationId saveQualityNotificationEntity(QualityNotification alert) {

        List<AssetAsBuiltEntity> assetEntities = getAssetEntitiesByAlert(alert);

        if (!assetEntities.isEmpty()) {
            AlertEntity alertEntity = AlertEntity.from(alert, assetEntities);

            jpaAlertRepository.save(alertEntity);

            alert.getNotifications()
                    .forEach(notification -> handleNotificationCreate(alertEntity, notification, assetEntities));

            return new QualityNotificationId(alertEntity.getId());
        } else {
            throw new IllegalArgumentException("No assets found for %s asset ids".formatted(String.join(", ", alert.getAssetIds())));
        }
    }

    @Override
    public PageResult<QualityNotification> findQualityNotificationsBySide(QualityNotificationSide alertSide, Pageable pageable) {
        Page<AlertEntity> entities = jpaAlertRepository.findAllBySideEqualsOrderByCreatedDateDesc(QualityNotificationSideBaseEntity.valueOf(alertSide.name()), pageable);
        return new PageResult<>(entities, AlertEntity::toDomain);
    }

    @Override
    public Optional<QualityNotification> findOptionalQualityNotificationById(QualityNotificationId alertId) {
        return jpaAlertRepository.findById(alertId.value())
                .map(AlertEntity::toDomain);
    }

    @Override
    public long countQualityNotificationEntitiesByStatus(QualityNotificationStatus qualityNotificationStatus) {
        return jpaAlertRepository.countAllByStatusEquals(QualityNotificationStatusBaseEntity.valueOf(qualityNotificationStatus.name()));
    }

    @Override
    public Optional<QualityNotification> findByEdcNotificationId(String edcNotificationId) {
        return jpaAlertRepository.findByNotificationsEdcNotificationId(edcNotificationId)
                .map(AlertEntity::toDomain);
    }

    @Override
    public long countQualityNotificationEntitiesBySide(QualityNotificationSide alertSide) {
        return jpaAlertRepository.countAllBySideEquals(QualityNotificationSideBaseEntity.valueOf(alertSide.name()));
    }

    private void handleNotificationUpdate(AlertEntity alertEntity, QualityNotification alert) {

        List<AlertNotificationEntity> notificationEntities = new ArrayList<>(alertEntity.getNotifications());
        Map<String, AlertNotificationEntity> notificationEntityMap = notificationEntities.stream().collect(Collectors.toMap(AlertNotificationEntity::getId, notificationEntity -> notificationEntity));
        for (QualityNotificationMessage notification : alert.getNotifications()) {
            if (notificationExists(alertEntity, notification.getId())) {
                log.info("handleNotificationUpdate::notificationExists with id {} for alert with id {}", notification.getId(), alert.getNotificationId());
                handleNotificationUpdate(notificationEntityMap.get(notification.getId()), notification);
            } else {
                log.info("handleNotificationUpdate::new notification with id {} for alert with id {}", notification.getId(), alert.getNotificationId());
                List<AssetAsBuiltEntity> assetEntitiesByAlert = getAssetEntitiesByAlert(alert);
                handleNotificationCreate(alertEntity, notification, assetEntitiesByAlert);
            }
        }

    }

    private List<AssetAsBuiltEntity> getAssetEntitiesByAlert(QualityNotification alert) {
        // return assetsRepository.findByIdIn(alert.getAssetIds());
        return null;
    }

    private void handleNotificationCreate(AlertEntity alertEntity, QualityNotificationMessage notificationDomain, List<AssetAsBuiltEntity> assetEntities) {
        AlertNotificationEntity notificationEntity = toNotificationEntity(alertEntity, notificationDomain, assetEntities);
        AlertNotificationEntity savedEntity = notificationRepository.save(notificationEntity);
        log.info("Successfully persisted alert notification entity {}", savedEntity);
    }

    private boolean notificationExists(AlertEntity alertEntity, String notificationId) {
        List<AlertNotificationEntity> notificationEntities = new ArrayList<>(alertEntity.getNotifications());
        return notificationEntities.stream().anyMatch(notification -> notification.getId().equals(notificationId));
    }

    private void handleNotificationUpdate(AlertNotificationEntity notificationEntity, QualityNotificationMessage notification) {
        notificationEntity.setEdcUrl(notification.getEdcUrl());
        notificationEntity.setContractAgreementId(notification.getContractAgreementId());
        notificationEntity.setNotificationReferenceId(notification.getNotificationReferenceId());
        notificationEntity.setTargetDate(notification.getTargetDate());
        notificationRepository.save(notificationEntity);
    }


    private AlertNotificationEntity toNotificationEntity(AlertEntity alertEntity, QualityNotificationMessage notification, List<AssetAsBuiltEntity> alertAssets) {
        List<AssetAsBuiltEntity> notificationAssets = filterNotificationAssets(notification, alertAssets);

        if (notificationAssets.isEmpty()) {
            throw new IllegalStateException(" with id %s has no notification assets".formatted(alertEntity.getId()));
        }
        return AlertNotificationEntity.from(alertEntity, notification, notificationAssets);
    }

    private List<AssetAsBuiltEntity> filterNotificationAssets(QualityNotificationMessage notification, List<AssetAsBuiltEntity> assets) {
        Set<String> notificationAffectedAssetIds = notification.getAffectedParts().stream()
                .map(QualityNotificationAffectedPart::assetId)
                .collect(Collectors.toSet());

        return assets.stream()
                .filter(it -> notificationAffectedAssetIds.contains(it.getId()))
                .toList();
    }
}
