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
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset.AssetEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset.JpaAssetsRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.AffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Investigation;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.Notification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationsRepository;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.NotificationEntity;
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
public class InvestigationsRepositoryImpl implements InvestigationsRepository {

    private final JpaInvestigationRepository investigationRepository;

    private final JpaAssetsRepository assetsRepository;

    private final JpaNotificationRepository notificationRepository;

    private final Clock clock;

    @Override
    public void update(Notification notification) {
        NotificationEntity entity = notificationRepository.findById(notification.getId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Notification with id %s not found!", notification.getId())));
        handleNotificationUpdate(entity, notification);

    }

    @Override
    public InvestigationId update(Investigation investigation) {
        InvestigationEntity investigationEntity = investigationRepository.findById(investigation.getInvestigationId().value())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Investigation with id %s not found!", investigation.getInvestigationId().value())));

        investigationEntity.setStatus(investigation.getInvestigationStatus());
        investigationEntity.setUpdated(clock.instant());
        investigationEntity.setCloseReason(investigation.getCloseReason());
        investigationEntity.setAcceptReason(investigation.getAcceptReason());
        investigationEntity.setDeclineReason(investigation.getDeclineReason());

        handleNotificationUpdate(investigationEntity, investigation);
        investigationRepository.save(investigationEntity);

        return investigation.getInvestigationId();
    }

    @Override
    public InvestigationId save(Investigation investigation) {

        List<AssetEntity> assetEntities = getAssetEntitiesByInvestigation(investigation);

        if (!assetEntities.isEmpty()) {
            InvestigationEntity investigationEntity = InvestigationEntity.builder()
                    .assets(assetEntities)
                    .bpn(investigation.getBpn())
                    .description(investigation.getDescription())
                    .status(investigation.getInvestigationStatus())
                    .side(investigation.getInvestigationSide())
                    .created(investigation.getCreatedAt())
                    .build();

            investigationRepository.save(investigationEntity);

            investigation.getNotifications()
                    .forEach(notification -> handleNotificationCreate(investigationEntity, notification, assetEntities));

            return new InvestigationId(investigationEntity.getId());
        } else {
            throw new IllegalArgumentException("No assets found for %s asset ids".formatted(String.join(", ", investigation.getAssetIds())));
        }
    }

    @Override
    public PageResult<Investigation> getInvestigations(InvestigationSide investigationSide, Pageable pageable) {
        Page<InvestigationEntity> entities = investigationRepository.findAllBySideEqualsOrderByCreatedDesc(investigationSide, pageable);
        return new PageResult<>(entities, this::toInvestigation);
    }

    @Override
    public Optional<Investigation> findById(InvestigationId investigationId) {
        return investigationRepository.findById(investigationId.value())
                .map(this::toInvestigation);
    }

    @Override
    public long countPendingInvestigations() {
        return investigationRepository.countAllByStatusEquals(InvestigationStatus.RECEIVED);
    }

    @Override
    public Optional<Investigation> findByNotificationId(String notificationId) {
        return investigationRepository.findByNotificationsNotificationId(notificationId)
                .map(this::toInvestigation);
    }

    @Override
    public Optional<Investigation> findByNotificationReferenceId(String notificationReferenceId) {
        return investigationRepository.findByNotificationsNotificationReferenceId(notificationReferenceId)
                .map(this::toInvestigation);
    }

    @Override
    public Optional<Investigation> findByEdcNotificationId(String edcNotificationId) {
        return investigationRepository.findByNotificationsEdcNotificationId(edcNotificationId)
                .map(this::toInvestigation);
    }

    @Override
    public long countInvestigations(Set<InvestigationStatus> statuses) {
        return investigationRepository.countAllByStatusIn(statuses);
    }

    @Override
    public long countInvestigations(InvestigationSide investigationSide) {
        return investigationRepository.countAllBySideEquals(investigationSide);
    }

    private void handleNotificationUpdate(InvestigationEntity investigationEntity, Investigation investigation) {

        List<NotificationEntity> notificationEntities = new ArrayList<>(investigationEntity.getNotifications());
        Map<String, NotificationEntity> notificationEntityMap = notificationEntities.stream().collect(Collectors.toMap(NotificationEntity::getId, notificationEntity -> notificationEntity));
        for (Notification notification : investigation.getNotifications()) {
            if (notificationExists(investigationEntity, notification.getId())) {
                log.info("handleNotificationUpdate::notificationExists with id {} for investigation with id {}", notification.getId(), investigation.getInvestigationId());
                handleNotificationUpdate(notificationEntityMap.get(notification.getId()), notification);
            } else {
                log.info("handleNotificationUpdate::new notification with id {} for investigation with id {}", notification.getId(), investigation.getInvestigationId());
                List<AssetEntity> assetEntitiesByInvestigation = getAssetEntitiesByInvestigation(investigation);
                handleNotificationCreate(investigationEntity, notification, assetEntitiesByInvestigation);
            }
        }

    }

    private List<AssetEntity> getAssetEntitiesByInvestigation(Investigation investigation) {
        return assetsRepository.findByIdIn(investigation.getAssetIds());
    }

    private void handleNotificationCreate(InvestigationEntity investigationEntity, Notification notificationDomain, List<AssetEntity> assetEntities) {
        NotificationEntity notificationEntity = toNotificationEntity(investigationEntity, notificationDomain, assetEntities);
        NotificationEntity savedEntity = notificationRepository.save(notificationEntity);
        log.info("Successfully persisted notification entity {}", savedEntity);
    }

    private boolean notificationExists(InvestigationEntity investigationEntity, String notificationId) {
        List<NotificationEntity> notificationEntities = new ArrayList<>(investigationEntity.getNotifications());
        return notificationEntities.stream().anyMatch(notification -> notification.getId().equals(notificationId));
    }

    private void handleNotificationUpdate(NotificationEntity notificationEntity, Notification notification) {
        notificationEntity.setEdcUrl(notification.getEdcUrl());
        notificationEntity.setContractAgreementId(notification.getContractAgreementId());
        notificationEntity.setNotificationReferenceId(notification.getNotificationReferenceId());
        notificationEntity.setTargetDate(notification.getTargetDate());
        notificationRepository.save(notificationEntity);
    }

    private Investigation toInvestigation(InvestigationEntity investigationEntity) {
        List<Notification> notifications = investigationEntity.getNotifications().stream()
                .map(this::toNotification)
                .toList();

        List<String> assetIds = investigationEntity.getAssets().stream()
                .map(AssetEntity::getId)
                .toList();

        return new Investigation(
                new InvestigationId(investigationEntity.getId()),
                new BPN(investigationEntity.getBpn()),
                investigationEntity.getStatus(),
                investigationEntity.getSide(),
                investigationEntity.getCloseReason(),
                investigationEntity.getAcceptReason(),
                investigationEntity.getDeclineReason(),
                investigationEntity.getDescription(),
                investigationEntity.getCreated(),
                assetIds,
                notifications
        );
    }

    private Notification toNotification(NotificationEntity notificationEntity) {
        InvestigationEntity investigation = notificationEntity.getInvestigation();

        return new Notification(
                notificationEntity.getId(),
                notificationEntity.getNotificationReferenceId(),
                notificationEntity.getSenderBpnNumber(),
                notificationEntity.getSenderManufacturerName(),
                notificationEntity.getReceiverBpnNumber(),
                notificationEntity.getReceiverManufacturerName(),
                notificationEntity.getEdcUrl(),
                notificationEntity.getContractAgreementId(),
                investigation.getDescription(),
                investigation.getStatus(),
                notificationEntity.getAssets().stream()
                        .map(asset -> new AffectedPart(asset.getId()))
                        .toList(),
                notificationEntity.getTargetDate(),
                notificationEntity.getSeverity(),
                notificationEntity.getEdcNotificationId(),
                notificationEntity.getCreated(),
                notificationEntity.getUpdated(),
                notificationEntity.getMessageId(),
                notificationEntity.getIsInitial()
        );
    }


    private NotificationEntity toNotificationEntity(InvestigationEntity investigationEntity, Notification notification, List<AssetEntity> investigationAssets) {
        List<AssetEntity> notificationAssets = filterNotificationAssets(notification, investigationAssets);

        if (notificationAssets.isEmpty()) {
            throw new IllegalStateException("Investigation with id %s has no notification assets".formatted(investigationEntity.getId()));
        }

        return NotificationEntity
                .builder()
                .id(notification.getId())
                .investigation(investigationEntity)
                .senderBpnNumber(notification.getSenderBpnNumber())
                .senderManufacturerName(notification.getSenderManufacturerName())
                .receiverBpnNumber(notification.getReceiverBpnNumber())
                .receiverManufacturerName(notification.getReceiverManufacturerName())
                .assets(notificationAssets)
                .notificationReferenceId(notification.getNotificationReferenceId())
                .targetDate(notification.getTargetDate())
                .severity(notification.getSeverity())
                .edcNotificationId(notification.getEdcNotificationId())
                .status(notification.getInvestigationStatus())
                .messageId(notification.getMessageId())
                .isInitial(notification.getIsInitial())
                .build();
    }

    private List<AssetEntity> filterNotificationAssets(Notification notification, List<AssetEntity> assets) {
        Set<String> notificationAffectedAssetIds = notification.getAffectedParts().stream()
                .map(AffectedPart::assetId)
                .collect(Collectors.toSet());

        return assets.stream()
                .filter(it -> notificationAffectedAssetIds.contains(it.getId()))
                .toList();
    }
}
