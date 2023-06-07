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

package org.eclipse.tractusx.traceability.qualitynotification.domain.alert.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.exception.QualityNotificationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.repository.AlertRepository;
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
public class AlertsPublisherService {

    private final TraceabilityProperties traceabilityProperties;
    private final EdcNotificationService notificationsService;
    private final AlertRepository alertRepository;
    private final AssetRepository assetRepository;
    private final AssetService assetService;
    private final BpnRepository bpnRepository;
    private final Clock clock;


    /**
     * Starts a new alert with the given BPN, asset IDs and description.
     *
     * @param assetIds    the IDs of the assets to create alert
     * @param description the description of the alert
     * @param targetDate  the targetDate of the alert
     * @param severity    the severity of the alert
     * @return the ID of the newly created alert
     */
    public QualityNotificationId startAlert(List<String> assetIds, String description, Instant targetDate, QualityNotificationSeverity severity) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        QualityNotification alert = QualityNotification.startInvestigation(clock.instant(), applicationBPN, description);

        Map<String, List<Asset>> assetsByBPN = assetRepository.getAssetsById(assetIds).stream().collect(Collectors.groupingBy(Asset::getManufacturerId));

        assetsByBPN
                .entrySet()
                .stream()
                .map(it -> createNotification(applicationBPN, description, targetDate, severity, it, QualityNotificationStatus.CREATED))
                .forEach(alert::addNotification);

        assetService.setAssetsAlertStatus(alert);
        log.info("Start Alert {}", alert);
        return alertRepository.saveQualityNotificationEntity(alert);
    }

    private QualityNotificationMessage createNotification(BPN applicationBpn, String description, Instant targetDate, QualityNotificationSeverity severity, Map.Entry<String, List<Asset>> asset, QualityNotificationStatus alertStatus) {
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
                .investigationStatus(alertStatus)
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
     * Cancels an ongoing alert with the given BPN and ID.
     *
     * @param alert the Investigation to cancel
     */
    public void cancelAlert(QualityNotification alert) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        alert.cancel(applicationBPN);
        assetService.setAssetsAlertStatus(alert);
        alertRepository.updateQualityNotificationEntity(alert);
    }

    /**
     * Approves an ongoing alert with the given BPN and ID to the next stage.
     *
     * @param alert the Alert to send
     */
    public void approveAlert(QualityNotification alert) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        alert.send(applicationBPN);
        alertRepository.updateQualityNotificationEntity(alert);
        // For each asset within investigation a notification was created before
        alert.getNotifications().forEach(notificationsService::asyncNotificationExecutor);
    }

    /**
     * Updates an ongoing alert with the given BPN, ID, status and reason.
     *
     * @param alert the Alert to update
     * @param status        the AlertStatus of the alert to update
     * @param reason        the reason for update of the alert
     */
    public void updateAlertPublisher(QualityNotification alert, QualityNotificationStatus status, String reason) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        validate(applicationBPN, status, alert);

        List<QualityNotificationMessage> allLatestNotificationForEdcNotificationId = getAllLatestNotificationForEdcNotificationId(alert);
        List<QualityNotificationMessage> notificationsToSend = new ArrayList<>();
        log.info("::updateAlertPublisher::allLatestNotificationForEdcNotificationId {}", allLatestNotificationForEdcNotificationId);
        allLatestNotificationForEdcNotificationId.forEach(notification -> {
            QualityNotificationMessage notificationToSend = notification.copyAndSwitchSenderAndReceiver(applicationBPN);
            switch (status) {
                case ACKNOWLEDGED -> alert.acknowledge(notificationToSend);
                case ACCEPTED -> alert.accept(reason, notificationToSend);
                case DECLINED -> alert.decline(reason, notificationToSend);
                case CLOSED -> alert.close(reason, notificationToSend);
                default -> throw new QualityNotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for alert with id '%s'".formatted(alert.getInvestigationStatus().name(), status, alert.getInvestigationId()));
            }
            log.info("::updateAlertPublisher::notificationToSend {}", notificationToSend);
            alert.addNotification(notificationToSend);
            notificationsToSend.add(notificationToSend);
        });
        assetService.setAssetsAlertStatus(alert);
        alertRepository.updateQualityNotificationEntity(alert);
        notificationsToSend.forEach(notificationsService::asyncNotificationExecutor);
    }

    private void validate(BPN applicationBpn, QualityNotificationStatus status, QualityNotification alert) {

        final boolean isInvalidAcknowledgeOrAcceptOrDecline = !QualityNotificationSide.RECEIVER.equals(alert.getInvestigationSide()) && applicationBpn.value().equals(alert.getBpn());
        final boolean isInvalidClose = QualityNotificationStatus.CLOSED.equals(status) && !applicationBpn.value().equals(alert.getBpn());
        switch (status) {
            case ACKNOWLEDGED, ACCEPTED, DECLINED -> {
                if (isInvalidAcknowledgeOrAcceptOrDecline) {
                    throw new QualityNotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for alert with BPN '%s' and application with BPN '%s'".formatted(alert.getInvestigationStatus().name(), status, alert.getBpn(), applicationBpn.value()));
                }
            }
            case CLOSED -> {
                if (isInvalidClose) {
                    throw new QualityNotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for investigation with BPN '%s' and application with BPN '%s'".formatted(alert.getInvestigationStatus().name(), status, alert.getBpn(), applicationBpn.value()));
                }
            }
            default -> throw new QualityNotificationIllegalUpdate("Unknown Transition from status '%s' to status '%s' is not allowed for investigation with id '%s'".formatted(alert.getInvestigationStatus().name(), status, alert.getInvestigationId()));
        }
    }

    private List<QualityNotificationMessage> getAllLatestNotificationForEdcNotificationId(QualityNotification alert) {
        Map<String, List<QualityNotificationMessage>> notificationMap = new HashMap<>();

        for (QualityNotificationMessage notification : alert.getNotifications()) {
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
                    throw new IllegalArgumentException("Two notifications with the same edcNotificationId have the same status. This can happen on old datasets.");
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
