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

package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.service.EdcNotificationService;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class InvestigationsPublisherService {

    private final TraceabilityProperties traceabilityProperties;
    private final EdcNotificationService notificationsService;
    private final InvestigationRepository investigationsRepository;
    private final AssetRepository assetRepository;
    private final AssetService assetService;
    private final BpnRepository bpnRepository;
    private final Clock clock;


    /**
     * Starts a new investigation with the given BPN, asset IDs and description.
     *
     * @param assetIds    the IDs of the assets to investigate
     * @param description the description of the investigation
     * @param targetDate  the targetDate of the investigation
     * @param severity    the severity of the investigation
     * @return the ID of the newly created investigation
     */
    public QualityNotificationId startInvestigation(List<String> assetIds, String description, Instant targetDate, QualityNotificationSeverity severity) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        QualityNotification investigation = QualityNotification.startInvestigation(clock.instant(), applicationBPN, description);

        Map<String, List<Asset>> assetsByBPN = assetRepository.getAssetsById(assetIds).stream().collect(Collectors.groupingBy(Asset::getManufacturerId));

        assetsByBPN
                .entrySet()
                .stream()
                .map(it -> createNotification(applicationBPN, description, targetDate, severity, it, QualityNotificationStatus.CREATED))
                .forEach(investigation::addNotification);

        assetService.setAssetsInvestigationStatus(investigation);
        log.info("Start Investigation {}", investigation);
        return investigationsRepository.saveQualityNotificationEntity(investigation);
    }

    private QualityNotificationMessage createNotification(BPN applicationBpn, String description, Instant targetDate, QualityNotificationSeverity severity, Map.Entry<String, List<Asset>> asset, QualityNotificationStatus investigationStatus) {
        final String notificationId = UUID.randomUUID().toString();
        final String messageId = UUID.randomUUID().toString();
        return QualityNotificationMessage.builder()
                .id(notificationId)
                .created(LocalDateTime.now())
                .senderBpnNumber(applicationBpn.value())
                .senderManufacturerName(getManufacturerName(applicationBpn.value()))
                .receiverBpnNumber(asset.getKey())
                .receiverManufacturerName(getManufacturerName(asset.getKey()))
                .description(description)
                .investigationStatus(investigationStatus)
                .affectedParts(asset.getValue().stream().map(Asset::getId).map(QualityNotificationAffectedPart::new).toList())
                .targetDate(targetDate)
                .severity(severity)
                .edcNotificationId(notificationId)
                .messageId(messageId)
                .isInitial(true)
                .build();
    }

    private String getManufacturerName(String bpn) {
        return bpnRepository.findManufacturerName(bpn)
                .orElse(null);
    }

    /**
     * Cancels an ongoing investigation with the given BPN and ID.
     *
     * @param investigation the Investigation to cancel
     */
    public void cancelInvestigation(QualityNotification investigation) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        investigation.cancel(applicationBPN);
        assetService.setAssetsInvestigationStatus(investigation);
        investigationsRepository.updateQualityNotificationEntity(investigation);
    }

    /**
     * Approves an ongoing investigation with the given BPN and ID to the next stage.
     *
     * @param investigation the Investigation to send
     */
    public void approveInvestigation(QualityNotification investigation) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        investigation.send(applicationBPN);
        investigationsRepository.updateQualityNotificationEntity(investigation);
        // For each asset within investigation a notification was created before
        investigation.getNotifications().forEach(notificationsService::asyncNotificationExecutor);
    }

    /**
     * Updates an ongoing investigation with the given BPN, ID, status and reason.
     *
     * @param investigation the Investigation to update
     * @param status        the InvestigationStatus of the investigation to update
     * @param reason        the reason for update of the investigation
     */
    public void updateInvestigationPublisher(QualityNotification investigation, QualityNotificationStatus status, String reason) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        validate(applicationBPN, status, investigation);

        List<QualityNotificationMessage> allLatestNotificationForEdcNotificationId = getAllLatestNotificationForEdcNotificationId(investigation);
        List<QualityNotificationMessage> notificationsToSend = new ArrayList<>();
        log.info("::updateInvestigationPublisher::allLatestNotificationForEdcNotificationId {}", allLatestNotificationForEdcNotificationId);
        allLatestNotificationForEdcNotificationId.forEach(notification -> {
            QualityNotificationMessage notificationToSend = notification.copyAndSwitchSenderAndReceiver(applicationBPN);
            switch (status) {
                case ACKNOWLEDGED -> investigation.acknowledge(notificationToSend);
                case ACCEPTED -> investigation.accept(reason, notificationToSend);
                case DECLINED -> investigation.decline(reason, notificationToSend);
                case CLOSED -> investigation.close(reason, notificationToSend);
                default -> throw new InvestigationIllegalUpdate("Transition from status %s to status %s is not allowed for investigation with id %s".formatted(investigation.getInvestigationStatus().name(), status, investigation.getInvestigationId()));
            }
            log.info("::updateInvestigationPublisher::notificationToSend {}", notificationToSend);
            investigation.addNotification(notificationToSend);
            notificationsToSend.add(notificationToSend);
        });
        assetService.setAssetsInvestigationStatus(investigation);
        investigationsRepository.updateQualityNotificationEntity(investigation);
        notificationsToSend.forEach(notificationsService::asyncNotificationExecutor);
    }

    private void validate(BPN applicationBpn, QualityNotificationStatus status, QualityNotification investigation) {

        final boolean isInvalidAcknowledgeOrAcceptOrDecline = !QualityNotificationSide.RECEIVER.equals(investigation.getInvestigationSide()) && applicationBpn.value().equals(investigation.getBpn());
        final boolean isInvalidClose = QualityNotificationStatus.CLOSED.equals(status) && !applicationBpn.value().equals(investigation.getBpn());
        switch (status) {
            case ACKNOWLEDGED, ACCEPTED, DECLINED -> {
                if (isInvalidAcknowledgeOrAcceptOrDecline) {
                    throw new InvestigationIllegalUpdate("Transition from status %s to status %s is not allowed for investigation with BPN %s and application with BPN %s".formatted(investigation.getInvestigationStatus().name(), status, investigation.getBpn(), applicationBpn.value()));
                }
            }
            case CLOSED -> {
                if (isInvalidClose) {
                    throw new InvestigationIllegalUpdate("Transition from status %s to status %s is not allowed for investigation with BPN %s and application with BPN %s".formatted(investigation.getInvestigationStatus().name(), status, investigation.getBpn(), applicationBpn.value()));
                }
            }
            default -> throw new InvestigationIllegalUpdate("Unknown Transition from status %s to status %s is not allowed for investigation with id %s".formatted(investigation.getInvestigationStatus().name(), status, investigation.getInvestigationId()));
        }
    }

    private List<QualityNotificationMessage> getAllLatestNotificationForEdcNotificationId(QualityNotification investigation) {
        Map<String, List<QualityNotificationMessage>> notificationMap = new HashMap<>();

        for (QualityNotificationMessage notification : investigation.getNotifications()) {
            String edcNotificationId = notification.getEdcNotificationId();
            List<QualityNotificationMessage> notificationGroup = notificationMap.getOrDefault(edcNotificationId, new ArrayList<>());
            if (notificationGroup.isEmpty()) {
                notificationGroup.add(notification);
            } else {
                Optional<QualityNotificationMessage> latestNotification = notificationGroup.stream().max(Comparator.comparing(QualityNotificationMessage::getCreated));
                if (latestNotification.isEmpty() || notification.getCreated().isAfter(latestNotification.get().getCreated())) {
                    notificationGroup.clear();
                    notificationGroup.add(notification);
                } else if (notification.getCreated().isEqual(latestNotification.get().getCreated())) {
                    throw new IllegalArgumentException("Two notifications with same edcNotificationId have the same status. This can happen on old datasets.");
                }
            }
            notificationMap.put(edcNotificationId, notificationGroup);
        }

        List<QualityNotificationMessage> latestNotificationElements = new ArrayList<>();
        for (List<QualityNotificationMessage> notificationGroup : notificationMap.values()) {
            latestNotificationElements.addAll(notificationGroup);
        }
        return latestNotificationElements;
    }
}
