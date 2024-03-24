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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.repository.CriteriaUtility;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.NotificationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.notification.model.NotificationMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JpaNotificationRepository jpaNotificationRepository;
    private final JpaNotificationMessageRepository jpaNotificationMessageRepository;
    private final JpaAssetAsBuiltRepository assetsAsBuiltRepository;

    private final Clock clock;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<QualityNotification> findOptionalQualityNotificationById(QualityNotificationId notificationId) {
        return jpaNotificationRepository.findById(notificationId.value()).map(NotificationEntity::toDomain);
    }

    @Override
    public Optional<QualityNotification> findByEdcNotificationId(String edcNotificationId) {
        return jpaNotificationRepository.findByNotificationsEdcNotificationId(edcNotificationId).map(NotificationEntity::toDomain);
    }

    @Override
    public Optional<QualityNotification> findByNotificationMessageId(String id) {
        return jpaNotificationRepository.findByNotificationMessageId(id).map(NotificationEntity::toDomain);
    }

    @Override
    public long countQualityNotificationEntitiesBySide(QualityNotificationSide notificationSide) {
        return jpaNotificationRepository.countAllBySideEquals(NotificationSideBaseEntity.valueOf(notificationSide.name()));
    }

    @Override
    public long countQualityNotificationEntitiesBySideAndNotificationType(QualityNotificationSide notificationSide, QualityNotificationType notificationType) {
        return jpaNotificationRepository.countAllBySideEqualsAndTypeEquals(
                NotificationSideBaseEntity.valueOf(notificationSide.name()),
                NotificationTypeEntity.valueOf(notificationType.name())
        );
    }

    @Override
    public QualityNotificationId saveQualityNotificationEntity(QualityNotification notification) {
        List<AssetAsBuiltEntity> assetEntities = getAssetEntitiesByNotification(notification);

        if (assetEntities.isEmpty()) {
            throw new IllegalArgumentException("No assets found for %s asset ids".formatted(String.join(", ", notification.getAssetIds())));
        }
        NotificationEntity notificationEntity = NotificationEntity.from(notification, assetEntities);

        jpaNotificationRepository.save(notificationEntity);

        notification.getNotifications().forEach(notificationMessage -> handleNotificationCreate(notificationEntity, notificationMessage, assetEntities));

        return new QualityNotificationId(notificationEntity.getId());
    }

    @Override
    public void updateQualityNotificationEntity(QualityNotification notification) {
        NotificationEntity notificationEntity = jpaNotificationRepository.findById(notification.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Investigation with id %s not found!", notification.getNotificationId().value())));

        notificationEntity.setStatus(NotificationStatusBaseEntity.fromStringValue(notification.getNotificationStatus().name()));
        notificationEntity.setUpdated(clock.instant());
        notificationEntity.setCloseReason(notification.getCloseReason());
        notificationEntity.setAcceptReason(notification.getAcceptReason());
        notificationEntity.setDeclineReason(notification.getDeclineReason());
        handleNotificationUpdate(notificationEntity, notification);
        jpaNotificationRepository.save(notificationEntity);
    }

    @Override
    public PageResult<QualityNotification> getNotifications(Pageable pageable, SearchCriteria searchCriteria) {
        List<NotificationSpecification> notificationSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList())
                .stream()
                .map(NotificationSpecification::new)
                .toList();
        Specification<NotificationEntity> specification = NotificationSpecification.toSpecification(notificationSpecifications);
        return new PageResult<>(jpaNotificationRepository.findAll(specification, pageable), NotificationEntity::toDomain);
    }

    @Transactional
    @Override
    public long countOpenNotificationsByOwnership(List<Owner> owners) {
        return jpaNotificationRepository.findAllByStatusIn(NotificationStatusBaseEntity.from(QualityNotificationStatus.ACTIVE_STATES))
                .stream()
                .map(NotificationEntity::getAssets)
                .flatMap(Collection::stream)
                .filter(assetAsBuiltEntity -> owners.contains(assetAsBuiltEntity.getOwner()))
                .distinct()
                .toList()
                .size();
    }

    @Override
    public long countOpenNotificationsByOwnershipAndNotificationType(List<Owner> owners, QualityNotificationType notificationType) {
        return jpaNotificationRepository.findAllByStatusInAndType(
                        NotificationStatusBaseEntity.from(QualityNotificationStatus.ACTIVE_STATES),
                        NotificationTypeEntity.from(notificationType))
                .stream()
                .map(NotificationEntity::getAssets)
                .flatMap(Collection::stream)
                .filter(assetAsBuiltEntity -> owners.contains(assetAsBuiltEntity.getOwner()))
                .distinct()
                .toList()
                .size();
    }

    @Override
    public List<String> getDistinctFieldValues(String fieldName, String startWith, Integer resultLimit, QualityNotificationSide side) {
        return CriteriaUtility.getDistinctNotificationFieldValues(fieldName, startWith, resultLimit, side, NotificationEntity.class, entityManager);
    }

    @Override
    public void updateErrorMessage(QualityNotification notification) {
        log.info("Starting update of error message with notification {}", notification);
        NotificationEntity notificationEntity = jpaNotificationRepository.findById(notification.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Notification with id %s not found!", notification.getNotificationId().value())));

        for (QualityNotificationMessage notificationMessage : notification.getNotifications()) {
            List<AssetAsBuiltEntity> assetEntitiesByNotification = getAssetEntitiesByNotification(notification);
            NotificationMessageEntity notificationMessageEntity = toNotificationMessageEntity(notificationEntity, notificationMessage, assetEntitiesByNotification);
            Optional<NotificationMessageEntity> optionalNotificationMessage = jpaNotificationMessageRepository.findById(notificationMessageEntity.getId());
            optionalNotificationMessage.ifPresentOrElse(messageEntity -> {
                messageEntity.setErrorMessage(notificationMessage.getErrorMessage());
                messageEntity.setUpdated(LocalDateTime.ofInstant(clock.instant(), clock.getZone()));
                jpaNotificationMessageRepository.save(notificationMessageEntity);
                log.info("Update of error message with notificationMessageEntity {}", notificationMessageEntity);
            }, () -> log.info("Could not find notification message by id {}. Error could not be enriched {}", notificationMessage.getId(), notificationMessage.getErrorMessage()));
        }
        jpaNotificationRepository.save(notificationEntity);
    }

    private List<AssetAsBuiltEntity> getAssetEntitiesByNotification(QualityNotification notification) {
        return assetsAsBuiltRepository.findByIdIn(notification.getAssetIds());
    }

    private void handleNotificationCreate(NotificationEntity notificationEntity, QualityNotificationMessage notificationDomain, List<AssetAsBuiltEntity> assetEntities) {
        NotificationMessageEntity notificationMessageEntity = toNotificationMessageEntity(notificationEntity, notificationDomain, assetEntities);

        Optional<NotificationMessageEntity> optionalNotificationMessage = jpaNotificationMessageRepository.findById(notificationMessageEntity.getId());

        optionalNotificationMessage.ifPresentOrElse(
                // If present
                investigationNotificationEntity -> log.info("Investigation has the following old notification with id {} and status {}", investigationNotificationEntity.getId(), investigationNotificationEntity.getStatus().name()),
                // If not present
                () -> {
                    // Persist
                    log.info("Investigation has the following new notification with id {} and status {}", notificationMessageEntity.getId(), notificationMessageEntity.getStatus().name());
                    jpaNotificationMessageRepository.save(notificationMessageEntity);
                    log.info("Successfully persisted notification entity {}", notificationMessageEntity);
                });
    }

    private NotificationMessageEntity toNotificationMessageEntity(NotificationEntity notificationEntity, QualityNotificationMessage notification, List<AssetAsBuiltEntity> investigationAssets) {
        List<AssetAsBuiltEntity> notificationAssets = filterNotificationAssets(notification, investigationAssets);

        if (notificationAssets.isEmpty()) {
            throw new IllegalStateException("Investigation with id %s has no notification assets".formatted(notificationEntity.getId()));
        }
        return NotificationMessageEntity.from(notificationEntity, notification, notificationAssets);
    }

    private List<AssetAsBuiltEntity> filterNotificationAssets(QualityNotificationMessage notificationMessage, List<AssetAsBuiltEntity> assets) {
        Set<String> notificationAffectedAssetIds = notificationMessage.getAffectedParts().stream().map(QualityNotificationAffectedPart::assetId).collect(Collectors.toSet());

        return assets.stream().filter(it -> notificationAffectedAssetIds.contains(it.getId())).toList();
    }

    private void handleNotificationUpdate(NotificationEntity notificationEntity, QualityNotification notification) {
        for (QualityNotificationMessage notificationMessage : notification.getNotifications()) {
            List<AssetAsBuiltEntity> assetEntitiesByNotification = getAssetEntitiesByNotification(notification);
            handleNotificationCreate(notificationEntity, notificationMessage, assetEntitiesByNotification);
        }
    }
}
