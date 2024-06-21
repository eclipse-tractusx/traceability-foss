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

package org.eclipse.tractusx.traceability.notification.infrastructure.notification.repository;

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
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.eclipse.tractusx.traceability.common.repository.CriteriaUtility;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationId;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.notification.repository.NotificationRepository;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSeverityBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationTypeEntity;
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
    public Optional<Notification> findOptionalNotificationById(NotificationId notificationId) {
        return jpaNotificationRepository.findById(notificationId.value()).map(NotificationEntity::toDomain);
    }

    @Override
    public Optional<Notification> findByEdcNotificationId(String edcNotificationId) {
        return jpaNotificationRepository.findByNotificationMessageEdcNotificationId(edcNotificationId).map(NotificationEntity::toDomain);
    }

    @Override
    public long countNotificationsBySideAndType(NotificationSide notificationSide, NotificationType notificationType) {
        return jpaNotificationRepository.countAllBySideEqualsAndTypeEquals(
                NotificationSideBaseEntity.valueOf(notificationSide.name()),
                NotificationTypeEntity.valueOf(notificationType.name())
        );
    }

    @Override
    public NotificationId saveNotification(Notification notification) {
        List<AssetAsBuiltEntity> assetEntities = getAssetEntitiesByNotification(notification);

        if (assetEntities.isEmpty()) {
            throw new IllegalArgumentException("No assets found for %s asset ids".formatted(String.join(", ", notification.getAffectedPartIds())));
        }
        NotificationEntity notificationEntity = NotificationEntity.from(notification, assetEntities);

        jpaNotificationRepository.save(notificationEntity);

        notification.getNotifications().forEach(notificationMessage -> handleMessageCreate(notificationEntity, notificationMessage, assetEntities));

        return new NotificationId(notificationEntity.getId());
    }

    @Override
    public void updateNotification(Notification notification) {
        NotificationEntity notificationEntity = jpaNotificationRepository.findById(notification.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Investigation with id %s not found!", notification.getNotificationId().value())));
        notificationEntity.setStatus(NotificationStatusBaseEntity.fromStringValue(notification.getNotificationStatus().name()));
        notificationEntity.setUpdated(clock.instant());
        handleMessageUpdate(notificationEntity, notification);
        jpaNotificationRepository.save(notificationEntity);
    }

    @Override
    public void updateNotificationAndMessage(Notification notification) {
        NotificationEntity notificationEntity = jpaNotificationRepository.findById(notification.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Investigation with id %s not found!", notification.getNotificationId().value())));
        notificationEntity.setTitle(notification.getTitle());
        notificationEntity.setDescription(notification.getDescription());
        notificationEntity.setBpn(notification.getBpn());
        notificationEntity.setAssets(getAssetEntitiesByAssetIds(notification.getAffectedPartIds()));
        notificationEntity.setStatus(NotificationStatusBaseEntity.fromStringValue(notification.getNotificationStatus().name()));
        notificationEntity.setUpdated(clock.instant());
        notificationEntity.setSeverity(NotificationSeverityBaseEntity.fromString(notification.getSeverity().getRealName()));
        handleMessageUpdate(notificationEntity, notification);
        jpaNotificationRepository.save(notificationEntity);
    }

    @Override
    public PageResult<Notification> getNotifications(Pageable pageable, SearchCriteria searchCriteria) {
        List<NotificationSpecification> notificationSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList())
                .stream()
                .map(NotificationSpecification::new)
                .toList();
        Specification<NotificationEntity> specification = BaseSpecification.toSpecification(notificationSpecifications);
        return new PageResult<>(jpaNotificationRepository.findAll(specification, pageable), NotificationEntity::toDomain);
    }

    @Override
    public long countOpenNotificationsByOwnershipAndNotificationType(List<Owner> owners, NotificationType notificationType) {
        return jpaNotificationRepository.findAllByStatusInAndType(
                        NotificationStatusBaseEntity.from(NotificationStatus.ACTIVE_STATES),
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
    public List<String> getDistinctFieldValues(String fieldName, String startWith, Integer resultLimit, NotificationSide side) {
        return CriteriaUtility.getDistinctNotificationFieldValues(fieldName, startWith, resultLimit, side, NotificationEntity.class, entityManager);
    }

    @Override
    public void updateErrorMessage(Notification notification) {
        log.info("Starting update of error message with notification {}", notification);
        NotificationEntity notificationEntity = jpaNotificationRepository.findById(notification.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Notification with id %s not found!", notification.getNotificationId().value())));

        for (NotificationMessage notificationMessage : notification.getNotifications()) {
            List<AssetAsBuiltEntity> assetEntitiesByNotification = getAssetEntitiesByNotification(notification);
            NotificationMessageEntity notificationMessageEntity = toNotificationMessageEntity(notificationEntity, notificationMessage, assetEntitiesByNotification);
            notificationMessageEntity.setErrorMessage(notificationMessage.getErrorMessage());
            notificationMessageEntity.setUpdated(LocalDateTime.ofInstant(clock.instant(), clock.getZone()));
            jpaNotificationMessageRepository.save(notificationMessageEntity);
        }
        jpaNotificationRepository.save(notificationEntity);
    }

    @Override
    public void deleteByIdIn(List<String> messageIds) {
        jpaNotificationMessageRepository.deleteAllByIdInBatch(messageIds);
    }

    private List<AssetAsBuiltEntity> getAssetEntitiesByNotification(Notification notification) {
        return assetsAsBuiltRepository.findByIdIn(notification.getAffectedPartIds());
    }

    private List<AssetAsBuiltEntity> getAssetEntitiesByAssetIds(List<String> assetIds) {
        return assetsAsBuiltRepository.findByIdIn(assetIds);
    }

    private void handleMessageCreate(NotificationEntity notificationEntity, NotificationMessage messageDomain, List<AssetAsBuiltEntity> assetEntities) {
        NotificationMessageEntity notificationMessageEntity = toNotificationMessageEntity(notificationEntity, messageDomain, assetEntities);

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

    private NotificationMessageEntity toNotificationMessageEntity(NotificationEntity notificationEntity, NotificationMessage notificationMessage, List<AssetAsBuiltEntity> investigationAssets) {
        List<AssetAsBuiltEntity> notificationAssets = filterNotificationAssets(notificationMessage, investigationAssets);

        if (notificationAssets.isEmpty()) {
            throw new IllegalStateException("Investigation with id %s has no notificationMessage assets".formatted(notificationEntity.getId()));
        }
        return NotificationMessageEntity.from(notificationEntity, notificationMessage, notificationAssets);
    }

    private List<AssetAsBuiltEntity> filterNotificationAssets(NotificationMessage notificationMessage, List<AssetAsBuiltEntity> assets) {
        Set<String> notificationAffectedAssetIds = notificationMessage.getAffectedParts().stream().map(NotificationAffectedPart::assetId).collect(Collectors.toSet());

        return assets.stream().filter(it -> notificationAffectedAssetIds.contains(it.getId())).toList();
    }

    private void handleMessageUpdate(NotificationEntity notificationEntity, Notification notification) {
        for (NotificationMessage notificationMessage : notification.getNotifications()) {
            List<AssetAsBuiltEntity> assetEntitiesByNotification = getAssetEntitiesByNotification(notification);
            handleMessageCreate(notificationEntity, notificationMessage, assetEntitiesByNotification);
        }
    }

}
