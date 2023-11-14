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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@RequiredArgsConstructor
@Component
public class InvestigationsRepositoryImpl implements InvestigationRepository {

    private final JpaInvestigationRepository jpaInvestigationRepository;

    private final JpaAssetAsBuiltRepository assetsRepository;

    private final JpaInvestigationNotificationRepository notificationRepository;

    private final Clock clock;

    @Override
    public void updateQualityNotificationMessageEntity(QualityNotificationMessage notification) {
        InvestigationNotificationEntity entity = notificationRepository.findById(notification.getId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Notification with id %s not found!", notification.getId())));
        handleNotificationUpdate(entity, notification);
    }

    @Override
    public QualityNotificationId updateQualityNotificationEntity(QualityNotification investigation) {
        InvestigationEntity investigationEntity = jpaInvestigationRepository.findById(investigation.getNotificationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Investigation with id %s not found!", investigation.getNotificationId().value())));

        investigationEntity.setStatus(NotificationStatusBaseEntity.fromStringValue(investigation.getNotificationStatus().name()));
        investigationEntity.setUpdated(clock.instant());
        investigationEntity.setCloseReason(investigation.getCloseReason());
        investigationEntity.setAcceptReason(investigation.getAcceptReason());
        investigationEntity.setDeclineReason(investigation.getDeclineReason());

        handleNotificationUpdate(investigationEntity, investigation);
        jpaInvestigationRepository.save(investigationEntity);

        return investigation.getNotificationId();
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
    public PageResult<QualityNotification> findQualityNotificationsBySide(QualityNotificationSide investigationSide, Pageable pageable) {
        Page<InvestigationEntity> entities = jpaInvestigationRepository.findAllBySideEquals(NotificationSideBaseEntity.valueOf(investigationSide.name()), pageable);
        return new PageResult<>(entities, InvestigationEntity::toDomain);
    }

    @Override
    public PageResult<QualityNotification> findAll(Pageable pageable, SearchCriteria searchCriteria) {
        List<InvestigationSpecification> investigationSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream().map(InvestigationSpecification::new).toList();
        Specification<InvestigationEntity> specification = InvestigationSpecification.toSpecification(investigationSpecifications, searchCriteria.getSearchCriteriaOperator());
        Page<InvestigationEntity> investigationEntityPage = jpaInvestigationRepository.findAll(specification, pageable);
        return new PageResult<>(investigationEntityPage, InvestigationEntity::toDomain);
    }


    @Override
    public Optional<QualityNotification> findOptionalQualityNotificationById(QualityNotificationId investigationId) {
        return jpaInvestigationRepository.findById(investigationId.value())
                .map(InvestigationEntity::toDomain);
    }

    @Override
    public long countQualityNotificationEntitiesByStatus(QualityNotificationStatus qualityNotificationStatus) {
        return jpaInvestigationRepository.countAllByStatusEquals(NotificationStatusBaseEntity.valueOf(qualityNotificationStatus.name()));
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

        List<InvestigationNotificationEntity> notificationEntities = new ArrayList<>(investigationEntity.getNotifications());
        Map<String, InvestigationNotificationEntity> notificationEntityMap = notificationEntities.stream().collect(Collectors.toMap(InvestigationNotificationEntity::getId, notificationEntity -> notificationEntity));
        for (QualityNotificationMessage notification : investigation.getNotifications()) {
            if (notificationExists(investigationEntity, notification.getId())) {
                log.info("handleNotificationUpdate::notificationExists with id {} for investigation with id {}", notification.getId(), investigation.getNotificationId());
                handleNotificationUpdate(notificationEntityMap.get(notification.getId()), notification);
            } else {
                log.info("handleNotificationUpdate::new notification with id {} for investigation with id {}", notification.getId(), investigation.getNotificationId());
                List<AssetAsBuiltEntity> assetEntitiesByInvestigation = getAssetEntitiesByInvestigation(investigation);
                handleNotificationCreate(investigationEntity, notification, assetEntitiesByInvestigation);
            }
        }

    }

    private List<AssetAsBuiltEntity> getAssetEntitiesByInvestigation(QualityNotification investigation) {
        return assetsRepository.findByIdIn(investigation.getAssetIds());
    }

    private void handleNotificationCreate(InvestigationEntity investigationEntity, QualityNotificationMessage notificationDomain, List<AssetAsBuiltEntity> assetEntities) {
        InvestigationNotificationEntity notificationEntity = toNotificationEntity(investigationEntity, notificationDomain, assetEntities);
        InvestigationNotificationEntity savedEntity = notificationRepository.save(notificationEntity);
        log.info("Successfully persisted notification entity {}", savedEntity);
    }

    private boolean notificationExists(InvestigationEntity investigationEntity, String notificationId) {
        List<InvestigationNotificationEntity> notificationEntities = new ArrayList<>(investigationEntity.getNotifications());
        return notificationEntities.stream().anyMatch(notification -> notification.getId().equals(notificationId));
    }

    private void handleNotificationUpdate(InvestigationNotificationEntity notificationEntity, QualityNotificationMessage notification) {
        notificationEntity.setEdcUrl(notification.getEdcUrl());
        notificationEntity.setContractAgreementId(notification.getContractAgreementId());
        notificationEntity.setNotificationReferenceId(notification.getNotificationReferenceId());
        notificationEntity.setTargetDate(notification.getTargetDate());
        notificationRepository.save(notificationEntity);
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
}
