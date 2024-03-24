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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.repository.CriteriaUtility;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
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
@Transactional
@Component
public class AlertsRepositoryImpl implements AlertRepository {

    private final JpaAlertRepository jpaAlertRepository;

    private final JpaAssetAsBuiltRepository assetAsBuiltRepository;

    private final JpaAlertNotificationRepository notificationRepository;

    private final Clock clock;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PageResult<QualityNotification> getNotifications(Pageable pageable, SearchCriteria searchCriteria) {
        List<AlertSpecification> alertsSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream()
                .map(AlertSpecification::new)
                .toList();
        Specification<AlertEntity> specification = AlertSpecification.toSpecification(alertsSpecifications);
        return new PageResult<>(jpaAlertRepository.findAll(specification, pageable), AlertEntity::toDomain);
    }

    @Override
    public void updateQualityNotificationEntity(QualityNotification alert) {
        AlertEntity alertEntity = jpaAlertRepository.findById(alert.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Alert with id %s not found!", alert.getNotificationId().value())));

        alertEntity.setStatus(NotificationStatusBaseEntity.fromStringValue(alert.getNotificationStatus().name()));
        alertEntity.setUpdated(clock.instant());
        alertEntity.setCloseReason(alert.getCloseReason());
        alertEntity.setAcceptReason(alert.getAcceptReason());
        alertEntity.setDeclineReason(alert.getDeclineReason());

        handleNotificationUpdate(alertEntity, alert);
        jpaAlertRepository.save(alertEntity);
    }

    @Override
    public void updateErrorMessage(QualityNotification alert) {

        AlertEntity alertEntity = jpaAlertRepository.findById(alert.getNotificationId().value()).orElseThrow(() -> new IllegalArgumentException(String.format("Investigation with id %s not found!", alert.getNotificationId().value())));

        for (QualityNotificationMessage notification : alert.getNotifications()) {
            List<AssetAsBuiltEntity> assetEntitiesByAlert = getAssetAsBuiltEntitiesByAlert(alert);
            AlertNotificationEntity notificationEntity = toNotificationEntity(alertEntity, notification, assetEntitiesByAlert);
            Optional<AlertNotificationEntity> optionalNotification = notificationRepository.findById(notificationEntity.getId());
            optionalNotification.ifPresentOrElse(alertNotificationEntity -> {
                alertNotificationEntity.setErrorMessage(notification.getErrorMessage());
                alertNotificationEntity.setUpdated(LocalDateTime.ofInstant(clock.instant(), clock.getZone()));
                notificationRepository.save(notificationEntity);

            }, () -> log.info("Could not find notification by id {}. Error could not be enriched {}", notification.getId(), notification.getErrorMessage()));
        }
        jpaAlertRepository.save(alertEntity);
    }

    @Override
    public QualityNotificationId saveQualityNotificationEntity(QualityNotification alert) {

        List<AssetAsBuiltEntity> assetAsBuiltEntities = getAssetAsBuiltEntitiesByAlert(alert);

        if (assetAsBuiltEntities.isEmpty()) {
            throw new IllegalArgumentException("No assets found for %s asset ids".formatted(String.join(", ", alert.getAssetIds())));
        }

        AlertEntity alertEntity = AlertEntity.from(alert, assetAsBuiltEntities);
        jpaAlertRepository.save(alertEntity);
        alert.getNotifications()
                .forEach(notification -> handleNotificationCreate(alertEntity, notification, assetAsBuiltEntities));
        return new QualityNotificationId(alertEntity.getId());
    }

    @Override
    public Optional<QualityNotification> findOptionalQualityNotificationById(QualityNotificationId alertId) {
        return jpaAlertRepository.findById(alertId.value())
                .map(AlertEntity::toDomain);
    }

    @Override
    public Optional<QualityNotification> findByNotificationMessageId(String id) {
        return jpaAlertRepository.findByNotificationMessageId(id).map(AlertEntity::toDomain);
    }

    @Override
    public long countOpenNotificationsByOwnership(List<Owner> owners) {
        return jpaAlertRepository.findAllByStatusIn(NotificationStatusBaseEntity.from(QualityNotificationStatus.ACTIVE_STATES))
                .stream()
                .map(AlertEntity::getAssets)
                .flatMap(Collection::stream)
                .filter(assetAsBuiltEntity -> owners.contains(assetAsBuiltEntity.getOwner()))
                .distinct()
                .toList().size();
    }

    @Override
    public long countOpenNotificationsByOwnershipAndNotificationType(List<Owner> owners, QualityNotificationType notificationType) {
        return 0;
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

    @Override
    public long countQualityNotificationEntitiesBySideAndNotificationType(QualityNotificationSide investigationSide, QualityNotificationType notificationType) {
        return 0;
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
                handleNotificationCreate(alertEntity, notification, assetAsBuiltEntitiesByAlert);
            }
        }

    }

    private List<AssetAsBuiltEntity> getAssetAsBuiltEntitiesByAlert(QualityNotification alert) {
        return assetAsBuiltRepository.findByIdIn(alert.getAssetIds());
    }

    private void handleNotificationCreate(AlertEntity alertEntity, QualityNotificationMessage notificationDomain,
                                          List<AssetAsBuiltEntity> assetEntities) {
        AlertNotificationEntity notificationEntity = toNotificationEntity(alertEntity, notificationDomain, assetEntities);
        AlertNotificationEntity savedEntity = notificationRepository.save(notificationEntity);
        log.info("Successfully persisted alert notification entity {}", savedEntity);
    }

    private boolean notificationExists(AlertEntity alertEntity, String notificationId) {
        List<AlertNotificationEntity> notificationEntities = new ArrayList<>(alertEntity.getNotifications());
        return notificationEntities.stream().anyMatch(notification -> notification.getId().equals(notificationId));
    }

    private void handleNotificationUpdate(AlertNotificationEntity notificationEntity, QualityNotificationMessage notification) {
        notificationEntity.setContractAgreementId(notification.getContractAgreementId());
        notificationEntity.setNotificationReferenceId(notification.getNotificationReferenceId());
        notificationEntity.setTargetDate(notification.getTargetDate());
        notificationRepository.save(notificationEntity);
    }


    private AlertNotificationEntity toNotificationEntity(AlertEntity alertEntity, QualityNotificationMessage notification, List<AssetAsBuiltEntity> alertAssets) {
        List<AssetAsBuiltEntity> filteredAsBuiltAssets = filterNotificationAssets(notification, alertAssets);

        if (filteredAsBuiltAssets.isEmpty()) {
            throw new IllegalStateException(" with id %s has no notification assets".formatted(alertEntity.getId()));
        }
        return AlertNotificationEntity.from(alertEntity, notification, filteredAsBuiltAssets);
    }

    private <T extends AssetBaseEntity> List<T> filterNotificationAssets(QualityNotificationMessage notification, List<T> assets) {
        Set<String> notificationAffectedAssetIds = notification.getAffectedParts().stream()
                .map(QualityNotificationAffectedPart::assetId)
                .collect(Collectors.toSet());

        return assets.stream()
                .filter(it -> notificationAffectedAssetIds.contains(it.getId()))
                .toList();
    }

    @Override
    public List<String> getDistinctFieldValues(String fieldName, String startWith, Integer resultLimit, QualityNotificationSide side) {
        return CriteriaUtility.getDistinctNotificationFieldValues(fieldName, startWith, resultLimit, side, AlertEntity.class, entityManager);
    }
}
