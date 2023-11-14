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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.repository.JpaAssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlertsRepositoryImpl implements AlertRepository {

    private final JpaAlertRepository jpaAlertRepository;

    private final JpaAssetAsBuiltRepository assetAsBuiltRepository;

    private final JpaAssetAsPlannedRepository assetAsPlannedRepository;

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

        alertEntity.setStatus(NotificationStatusBaseEntity.fromStringValue(alert.getNotificationStatus().name()));
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

        List<AssetAsBuiltEntity> assetAsBuiltEntities = getAssetAsBuiltEntitiesByAlert(alert);
        List<AssetAsPlannedEntity> assetAsPlannedEntities = getAssetAsPlannedEntitiesByAlert(alert);

        if (assetAsBuiltEntities.isEmpty() && assetAsPlannedEntities.isEmpty()) {
            throw new IllegalArgumentException("No assets found for %s asset ids".formatted(String.join(", ", alert.getAssetIds())));
        }

        AlertEntity alertEntity = AlertEntity.from(alert, assetAsBuiltEntities);
        jpaAlertRepository.save(alertEntity);
        alert.getNotifications()
                .forEach(notification -> handleNotificationCreate(alertEntity, notification, assetAsBuiltEntities, assetAsPlannedEntities));
        return new QualityNotificationId(alertEntity.getId());
    }

    @Override
    public PageResult<QualityNotification> findQualityNotificationsBySide(QualityNotificationSide alertSide, Pageable pageable) {
        Page<AlertEntity> entities = jpaAlertRepository.findAllBySideEquals(NotificationSideBaseEntity.valueOf(alertSide.name()), pageable);
        return new PageResult<>(entities, AlertEntity::toDomain);
    }

    @Override
    public PageResult<QualityNotification> findAll(Pageable pageable, SearchCriteria searchCriteria) {
        List<AlertSpecification> alertSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream().map(AlertSpecification::new).toList();
        Specification<AlertEntity> specification = AlertSpecification.toSpecification(alertSpecifications, searchCriteria.getSearchCriteriaOperator());
        Page<AlertEntity> alertEntityPage = jpaAlertRepository.findAll(specification, pageable);
        return new PageResult<>(alertEntityPage, AlertEntity::toDomain);
    }

    @Override
    public Optional<QualityNotification> findOptionalQualityNotificationById(QualityNotificationId alertId) {
        return jpaAlertRepository.findById(alertId.value())
                .map(AlertEntity::toDomain);
    }

    @Override
    public long countQualityNotificationEntitiesByStatus(QualityNotificationStatus qualityNotificationStatus) {
        return jpaAlertRepository.countAllByStatusEquals(NotificationStatusBaseEntity.valueOf(qualityNotificationStatus.name()));
    }

    @Transactional
    @Override
    public long countPartsByStatusAndOwnership(List<QualityNotificationStatus> statuses, Owner owner) {
        return jpaAlertRepository.findAllByStatusIn(NotificationStatusBaseEntity.from(statuses))
                .stream()
                .map(AlertEntity::getAssets)
                .flatMap(Collection::stream)
                .filter(assetAsBuiltEntity -> assetAsBuiltEntity.getOwner().equals(owner))
                .distinct()
                .toList().size();
    }

    @Override
    public Optional<QualityNotification> findByEdcNotificationId(String edcNotificationId) {
        return jpaAlertRepository.findByNotificationsEdcNotificationId(edcNotificationId)
                .map(AlertEntity::toDomain);
    }

    @Override
    public long countQualityNotificationEntitiesBySide(QualityNotificationSide alertSide) {
        return jpaAlertRepository.countAllBySideEquals(NotificationSideBaseEntity.valueOf(alertSide.name()));
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
                List<AssetAsBuiltEntity> assetAsBuiltEntitiesByAlert = getAssetAsBuiltEntitiesByAlert(alert);
                List<AssetAsPlannedEntity> assetAsPlannedEntitiesByAlert = getAssetAsPlannedEntitiesByAlert(alert);
                handleNotificationCreate(alertEntity, notification, assetAsBuiltEntitiesByAlert, assetAsPlannedEntitiesByAlert);
            }
        }

    }

    private List<AssetAsBuiltEntity> getAssetAsBuiltEntitiesByAlert(QualityNotification alert) {
        return assetAsBuiltRepository.findByIdIn(alert.getAssetIds());
    }

    private List<AssetAsPlannedEntity> getAssetAsPlannedEntitiesByAlert(QualityNotification alert) {
        return assetAsPlannedRepository.findByIdIn(alert.getAssetIds());
    }

    private void handleNotificationCreate(AlertEntity alertEntity, QualityNotificationMessage notificationDomain,
                                          List<AssetAsBuiltEntity> assetEntities, List<AssetAsPlannedEntity> assetAsPlannedEntitiesByAlert) {
        AlertNotificationEntity notificationEntity = toNotificationEntity(alertEntity, notificationDomain, assetEntities, assetAsPlannedEntitiesByAlert);
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


    private AlertNotificationEntity toNotificationEntity(AlertEntity alertEntity, QualityNotificationMessage notification, List<AssetAsBuiltEntity> alertAssets, List<AssetAsPlannedEntity> assetAsPlannedEntitiesByAlert) {
        List<AssetAsBuiltEntity> filteredAsBuiltAssets = filterNotificationAssets(notification, alertAssets);
        List<AssetAsPlannedEntity> filteredAsPlannedAssets = filterNotificationAssets(notification, assetAsPlannedEntitiesByAlert);

        if (filteredAsBuiltAssets.isEmpty() && filteredAsPlannedAssets.isEmpty()) {
            throw new IllegalStateException(" with id %s has no notification assets".formatted(alertEntity.getId()));
        }
        return AlertNotificationEntity.from(alertEntity, notification, filteredAsBuiltAssets, filteredAsPlannedAssets);
    }

    private <T extends AssetBaseEntity> List<T> filterNotificationAssets(QualityNotificationMessage notification, List<T> assets) {
        Set<String> notificationAffectedAssetIds = notification.getAffectedParts().stream()
                .map(QualityNotificationAffectedPart::assetId)
                .collect(Collectors.toSet());

        return assets.stream()
                .filter(it -> notificationAffectedAssetIds.contains(it.getId()))
                .toList();
    }
}
