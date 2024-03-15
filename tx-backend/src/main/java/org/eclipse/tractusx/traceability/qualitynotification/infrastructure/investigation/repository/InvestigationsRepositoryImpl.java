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

package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.repository;

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
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@RequiredArgsConstructor
@Component
@Transactional
public class InvestigationsRepositoryImpl implements InvestigationRepository {

    private final JpaInvestigationRepository jpaInvestigationRepository;

    private final JpaAssetAsBuiltRepository assetsRepository;

    private final JpaInvestigationNotificationRepository notificationRepository;

    private final Clock clock;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PageResult<QualityNotification> getNotifications(Pageable pageable, SearchCriteria searchCriteria) {
        List<InvestigationSpecification> investigationSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream()
                .map(InvestigationSpecification::new)
                .toList();
        Specification<InvestigationEntity> specification = InvestigationSpecification.toSpecification(investigationSpecifications);
        return new PageResult<>(jpaInvestigationRepository.findAll(specification, pageable), InvestigationEntity::toDomain);
    }

    @Override
    public long countOpenNotificationsByOwnership(List<Owner> owners) {
        return jpaInvestigationRepository.findAllByStatusIn(NotificationStatusBaseEntity.from(QualityNotificationStatus.ACTIVE_STATES))
                .stream()
                .map(InvestigationEntity::getAssets)
                .flatMap(Collection::stream)
                .filter(assetAsBuiltEntity -> owners.contains(assetAsBuiltEntity.getOwner()))
                .distinct()
                .toList().size();
    }

    @Override
    public void updateQualityNotificationEntity(QualityNotification investigation) {
        InvestigationEntity investigationEntity = jpaInvestigationRepository.findById(investigation.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Investigation with id %s not found!", investigation.getNotificationId().value())));

        investigationEntity.setStatus(NotificationStatusBaseEntity.fromStringValue(investigation.getNotificationStatus().name()));
        investigationEntity.setUpdated(clock.instant());
        investigationEntity.setCloseReason(investigation.getCloseReason());
        investigationEntity.setAcceptReason(investigation.getAcceptReason());
        investigationEntity.setDeclineReason(investigation.getDeclineReason());
        handleNotificationUpdate(investigationEntity, investigation);
        jpaInvestigationRepository.save(investigationEntity);
    }

    @Override
    public void updateQualityNotificationEntityByErrorMessage(QualityNotification investigation) {
        InvestigationEntity investigationEntity = jpaInvestigationRepository.findById(investigation.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Investigation with id %s not found!", investigation.getNotificationId().value())));
        handleNotificationUpdate(investigationEntity, investigation);
        jpaInvestigationRepository.save(investigationEntity);
    }

    @Override
    public QualityNotificationId saveQualityNotificationEntity(QualityNotification investigation) {

        List<AssetAsBuiltEntity> assetEntities = getAssetEntitiesByInvestigation(investigation);

        if (!assetEntities.isEmpty()) {
            InvestigationEntity investigationEntity = InvestigationEntity.from(investigation, assetEntities);

            jpaInvestigationRepository.save(investigationEntity);

            investigation.getNotifications()
                    .forEach(notification -> handleNotificationCreate(investigationEntity, notification, assetEntities));

            return new QualityNotificationId(investigationEntity.getId());
        } else {
            throw new IllegalArgumentException("No assets found for %s asset ids".formatted(String.join(", ", investigation.getAssetIds())));
        }
    }

    @Override
    public Optional<QualityNotification> findOptionalQualityNotificationById(QualityNotificationId investigationId) {
        return jpaInvestigationRepository.findById(investigationId.value())
                .map(InvestigationEntity::toDomain);
    }

    @Override
    public Optional<QualityNotification> findByEdcNotificationId(String edcNotificationId) {
        return jpaInvestigationRepository.findByNotificationsEdcNotificationId(edcNotificationId)
                .map(InvestigationEntity::toDomain);
    }

    @Override
    public long countQualityNotificationEntitiesBySide(QualityNotificationSide investigationSide) {
        return jpaInvestigationRepository.countAllBySideEquals(NotificationSideBaseEntity.valueOf(investigationSide.name()));
    }

    private void handleNotificationUpdate(InvestigationEntity investigationEntity, QualityNotification investigation) {
        for (QualityNotificationMessage notification : investigation.getNotifications()) {
            List<AssetAsBuiltEntity> assetEntitiesByInvestigation = getAssetEntitiesByInvestigation(investigation);
            handleNotificationCreate(investigationEntity, notification, assetEntitiesByInvestigation);
        }

    }

    private List<AssetAsBuiltEntity> getAssetEntitiesByInvestigation(QualityNotification investigation) {
        return assetsRepository.findByIdIn(investigation.getAssetIds());
    }

    private void handleNotificationCreate(InvestigationEntity investigationEntity, QualityNotificationMessage notificationDomain, List<AssetAsBuiltEntity> assetEntities) {
        InvestigationNotificationEntity notificationEntity = toNotificationEntity(investigationEntity, notificationDomain, assetEntities);

        Optional<InvestigationNotificationEntity> optionalNotification = notificationRepository.findById(notificationEntity.getId());

        optionalNotification.ifPresentOrElse(
                // If present
                investigationNotificationEntity -> log.info("Investigation has the following old notification with id {} and status {}", investigationNotificationEntity.getId(), investigationNotificationEntity.getStatus().name()),
                // If not present
                () -> {
                    // Persist
                    log.info("Investigation has the following new notification with id {} and status {}", notificationEntity.getId(), notificationEntity.getStatus().name());
                    notificationRepository.save(notificationEntity);
                    log.info("Successfully persisted notification entity {}", notificationEntity);
                }
        );

    }

    private void updateErrorMessageInNotification(InvestigationEntity investigationEntity, QualityNotificationMessage notificationDomain, List<AssetAsBuiltEntity> assetEntities) {
        InvestigationNotificationEntity notificationEntity = toNotificationEntity(investigationEntity, notificationDomain, assetEntities);

        Optional<InvestigationNotificationEntity> optionalNotification = notificationRepository.findById(notificationEntity.getId());

        optionalNotification.ifPresentOrElse(investigationNotificationEntity -> {
            optionalNotification.get().setErrorMessage(notificationDomain.getErrorMessage());
            notificationRepository.save(notificationEntity);

        }, () -> {
            log.info("Could not find notification by id {}", notificationDomain.getId());
        });


        optionalNotification.ifPresentOrElse(
                // If present
                investigationNotificationEntity -> log.info("Investigation has the following old notification with id {} and status {}", investigationNotificationEntity.getId(), investigationNotificationEntity.getStatus().name()),
                // If not present
                () -> {
                    // Persist
                    log.info("Investigation has the following new notification with id {} and status {}", notificationEntity.getId(), notificationEntity.getStatus().name());
                    notificationRepository.save(notificationEntity);
                    log.info("Successfully persisted notification entity {}", notificationEntity);
                }
        );

    }

    private InvestigationNotificationEntity toNotificationEntity(InvestigationEntity investigationEntity, QualityNotificationMessage notification, List<AssetAsBuiltEntity> investigationAssets) {
        List<AssetAsBuiltEntity> notificationAssets = filterNotificationAssets(notification, investigationAssets);

        if (notificationAssets.isEmpty()) {
            throw new IllegalStateException("Investigation with id %s has no notification assets".formatted(investigationEntity.getId()));
        }
        return InvestigationNotificationEntity.from(investigationEntity, notification, notificationAssets);
    }

    private List<AssetAsBuiltEntity> filterNotificationAssets(QualityNotificationMessage notification, List<AssetAsBuiltEntity> assets) {
        Set<String> notificationAffectedAssetIds = notification.getAffectedParts().stream()
                .map(QualityNotificationAffectedPart::assetId)
                .collect(Collectors.toSet());

        return assets.stream()
                .filter(it -> notificationAffectedAssetIds.contains(it.getId()))
                .toList();
    }

    @Override
    public List<String> getDistinctFieldValues(String fieldName, String startWith, Integer resultLimit, QualityNotificationSide side) {
        return CriteriaUtility.getDistinctNotificationFieldValues(fieldName, startWith, resultLimit, side, InvestigationEntity.class, entityManager);
    }
}
