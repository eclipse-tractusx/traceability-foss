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

package org.eclipse.tractusx.traceability.qualitynotification.domain.base.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.service.AssetAsPlannedServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.exception.QualityNotificationIllegalUpdate;
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

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationPublisherService {

    private final TraceabilityProperties traceabilityProperties;
    private final EdcNotificationService edcNotificationService;
    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final AssetAsBuiltServiceImpl assetAsBuiltService;
    private final AssetAsPlannedServiceImpl assetAsPlannedService;

    private final BpnRepository bpnRepository;
    private final Clock clock;


    /**
     * Starts a new investigation with the given BPN, asset IDs and description.
     *
     * @param assetIds    the IDs of the assets to investigate
     * @param description the description of the investigation
     * @param targetDate  the targetDate of the investigation
     * @param severity    the severity of the investigation
     * @param isAsBuilt   the isAsBuilt of the investigation
     * @return the ID of the newly created investigation
     */
    public QualityNotification startInvestigation(List<String> assetIds, String description, Instant targetDate, QualityNotificationSeverity severity, String receiverBpn, boolean isAsBuilt) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        QualityNotification notification = QualityNotification.startNotification(clock.instant(), applicationBPN, description);
        if (isAsBuilt) {
            Map<String, List<AssetBase>> assetsAsBuiltBPNMap = assetAsBuiltRepository.getAssetsById(assetIds).stream().collect(groupingBy(AssetBase::getManufacturerId));

            assetsAsBuiltBPNMap
                    .entrySet()
                    .stream()
                    .map(it -> createInvestigation(applicationBPN, receiverBpn, description, targetDate, severity, it))
                    .forEach(notification::addNotification);
            assetAsBuiltService.setAssetsInvestigationStatus(notification);
            return notification;
        } else {
            Map<String, List<AssetBase>> assetsAsPlannedBPNMap = assetAsPlannedRepository.getAssetsById(assetIds).stream().collect(groupingBy(AssetBase::getManufacturerId));

            assetsAsPlannedBPNMap
                    .entrySet()
                    .stream()
                    .map(it -> createInvestigation(applicationBPN, receiverBpn, description, targetDate, severity, it))
                    .forEach(notification::addNotification);
            assetAsPlannedService.setAssetsInvestigationStatus(notification);
            return notification;
        }


    }


    /**
     * Starts a new alert with the given BPN, asset IDs and description.
     *
     * @param assetIds    the IDs of the assets to create alert for
     * @param description the description of the alert
     * @param targetDate  the targetDate of the alert
     * @param severity    the severity of the alert
     * @param receiverBpn the bpn of the receiver
     * @return the ID of the newly created alert
     */
    public QualityNotification startAlert(List<String> assetIds, String description, Instant targetDate, QualityNotificationSeverity severity, String receiverBpn, boolean isAsBuilt) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        QualityNotification notification = QualityNotification.startNotification(clock.instant(), applicationBPN, description);
        List<AssetBase> assets = new ArrayList<>();
        if (isAsBuilt) {
            assets.addAll(assetAsBuiltRepository.getAssetsById(assetIds));
        } else {
            assets.addAll(assetAsPlannedRepository.getAssetsById(assetIds));
        }


        QualityNotificationMessage qualityNotificationMessage = createAlert(applicationBPN, description, targetDate, severity, assets, receiverBpn);
        notification.addNotification(qualityNotificationMessage);

        if (isAsBuilt) {
            assetAsBuiltService.setAssetsAlertStatus(notification);
        } else {
            assetAsPlannedService.setAssetsAlertStatus(notification);
        }
        return notification;
    }

    private QualityNotificationMessage createInvestigation(BPN applicationBpn, String receiverBpn, String description, Instant targetDate, QualityNotificationSeverity severity, Map.Entry<String, List<AssetBase>> asset) {
        final String notificationId = UUID.randomUUID().toString();
        final String messageId = UUID.randomUUID().toString();
        return QualityNotificationMessage.builder()
                .id(notificationId)
                .created(LocalDateTime.now())
                .createdBy(applicationBpn.value())
                .createdByName(getManufacturerName(applicationBpn.value()))
                .sendTo(StringUtils.isBlank(receiverBpn) ? asset.getKey() : receiverBpn)
                .sendToName(getManufacturerName(asset.getKey()))
                .description(description)
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(asset.getValue().stream().map(AssetBase::getId).map(QualityNotificationAffectedPart::new).toList())
                .targetDate(targetDate)
                .severity(severity)
                .edcNotificationId(notificationId)
                .messageId(messageId)
                .isInitial(true)
                .build();
    }

    private QualityNotificationMessage createAlert(BPN applicationBpn, String description, Instant targetDate, QualityNotificationSeverity severity, List<AssetBase> affectedAssets, String targetBpn) {
        final String notificationId = UUID.randomUUID().toString();
        final String messageId = UUID.randomUUID().toString();
        return QualityNotificationMessage.builder()
                .id(notificationId)
                .created(LocalDateTime.now())
                .createdBy(applicationBpn.value())
                .createdByName(getManufacturerName(applicationBpn.value()))
                .sendTo(targetBpn)
                .sendToName(getManufacturerName(targetBpn))
                .description(description)
                .notificationStatus(QualityNotificationStatus.CREATED)
                .affectedParts(affectedAssets.stream().map(AssetBase::getId).map(QualityNotificationAffectedPart::new).toList())
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
     * Cancels an ongoing notification with the given BPN and ID.
     *
     * @param notification the Notification to cancel
     */
    public QualityNotification cancelNotification(QualityNotification notification) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        notification.cancel(applicationBPN); // Shouldn't cancel notification trigger update on other side ?
        return notification;
    }

    /**
     * Approves an ongoing notification with the given BPN and ID to the next stage.
     *
     * @param notification the Notification to send
     */
    public QualityNotification approveNotification(QualityNotification notification) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        notification.send(applicationBPN);

        // For each asset within investigation a notification was created before
        notification.getNotifications().forEach(edcNotificationService::asyncNotificationExecutor);

        return notification;
    }

    /**
     * Updates an ongoing notification with the given BPN, ID, status and reason.
     *
     * @param notification the Notification to update
     * @param status       the NotificationStatus of the notification to update
     * @param reason       the reason for update of the notification
     */
    public QualityNotification updateNotificationPublisher(QualityNotification notification, QualityNotificationStatus status, String reason) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        validate(applicationBPN, status, notification);

        List<QualityNotificationMessage> allLatestNotificationForEdcNotificationId = getAllLatestNotificationForEdcNotificationId(notification);
        List<QualityNotificationMessage> notificationsToSend = new ArrayList<>();
        log.info("::updateNotificationPublisher::allLatestNotificationForEdcNotificationId {}", allLatestNotificationForEdcNotificationId);
        allLatestNotificationForEdcNotificationId.forEach(qNotification -> {
            QualityNotificationMessage notificationToSend = qNotification.copyAndSwitchSenderAndReceiver(applicationBPN);
            switch (status) {
                case ACKNOWLEDGED -> notification.acknowledge(notificationToSend);
                case ACCEPTED -> notification.accept(reason, notificationToSend);
                case DECLINED -> notification.decline(reason, notificationToSend);
                case CLOSED -> notification.close(reason, notificationToSend);
                default ->
                        throw new QualityNotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with id '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getNotificationId()));
            }
            log.info("::updateNotificationPublisher::notificationToSend {}", notificationToSend);
            notification.addNotification(notificationToSend);
            notificationsToSend.add(notificationToSend);
        });
        notificationsToSend.forEach(edcNotificationService::asyncNotificationExecutor);
        return notification;
    }

    private void validate(BPN applicationBpn, QualityNotificationStatus status, QualityNotification notification) {

        final boolean isInvalidAcknowledgeOrAcceptOrDecline = !QualityNotificationSide.RECEIVER.equals(notification.getNotificationSide()) && applicationBpn.value().equals(notification.getBpn());
        final boolean isInvalidClose = QualityNotificationStatus.CLOSED.equals(status) && !applicationBpn.value().equals(notification.getBpn());
        switch (status) {
            case ACKNOWLEDGED, ACCEPTED, DECLINED -> {
                if (isInvalidAcknowledgeOrAcceptOrDecline) {
                    throw new QualityNotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with BPN '%s' and application with BPN '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getBpn(), applicationBpn.value()));
                }
            }
            case CLOSED -> {
                if (isInvalidClose) {
                    throw new QualityNotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with BPN '%s' and application with BPN '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getBpn(), applicationBpn.value()));
                }
            }
            default ->
                    throw new QualityNotificationIllegalUpdate("Unknown Transition from status '%s' to status '%s' is not allowed for notification with id '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getNotificationId()));
        }
    }

    private List<QualityNotificationMessage> getAllLatestNotificationForEdcNotificationId(QualityNotification notification) {
        Map<String, List<QualityNotificationMessage>> notificationMap = new HashMap<>();

        for (QualityNotificationMessage notificationMessage : notification.getNotifications()) {
            String edcNotificationId = notificationMessage.getEdcNotificationId();
            List<QualityNotificationMessage> notificationGroup = notificationMap.getOrDefault(edcNotificationId, new ArrayList<>());
            if (notificationGroup.isEmpty()) {
                notificationGroup.add(notificationMessage);
            } else {
                Optional<QualityNotificationMessage> latestNotification = notificationGroup.stream().max(Comparator.comparing(QualityNotificationMessage::getCreated));
                if (latestNotification.isEmpty() || notificationMessage.getCreated().isAfter(latestNotification.get().getCreated())) {
                    notificationGroup.clear();
                    notificationGroup.add(notificationMessage);
                } else if (notificationMessage.getCreated().isEqual(latestNotification.get().getCreated())) {
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

