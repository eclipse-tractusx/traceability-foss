package org.eclipse.tractusx.traceability.qualitynotification.domain.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.repository.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
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
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationPublisherService {

    private final TraceabilityProperties traceabilityProperties;
    private final EdcNotificationService edcNotificationService;
    private final InvestigationRepository investigationsRepository;
    private final AlertRepository alertRepository;
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
        QualityNotification notification = QualityNotification.startNotification(clock.instant(), applicationBPN, description);

        Map<String, List<Asset>> assetsByBPN = assetRepository.getAssetsById(assetIds).stream().collect(groupingBy(Asset::getManufacturerId));

        assetsByBPN
                .entrySet()
                .stream()
                .map(it -> createInvestigation(applicationBPN, description, targetDate, severity, it))
                .forEach(notification::addNotification);

        assetService.setAssetsInvestigationStatus(notification);
        log.info("Start Investigation {}", notification);
        return investigationsRepository.saveQualityNotificationEntity(notification);
    }

    /**
     * Starts a new alert with the given BPN, asset IDs and description.
     *
     * @param assetIds    the IDs of the assets to create alert for
     * @param description the description of the alert
     * @param targetDate  the targetDate of the alert
     * @param severity    the severity of the alert
     * @return the ID of the newly created alert
     */
    public QualityNotificationId startAlert(List<String> assetIds, String description, Instant targetDate, QualityNotificationSeverity severity) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        QualityNotification notification = QualityNotification.startNotification(clock.instant(), applicationBPN, description);

        List<Asset> assets = assetRepository.getAssetsById(assetIds);
        Map<Asset, List<Descriptions>> parentDescriptionsByAsset = assets.stream().collect(toMap(Function.identity(), Asset::getParentRelations));

        Map<Asset, List<Asset>> parentAssetsByAlertedAsset = parentDescriptionsByAsset.entrySet().stream().map(
                entry -> Map.entry(
                        entry.getKey(),
                        assetRepository.getAssetsById(entry.getValue().stream().map(Descriptions::id).toList()))
        ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        parentAssetsByAlertedAsset.forEach((key, value) -> value.stream().map(it -> createAlert(applicationBPN, description, targetDate, severity, key, it))
                .forEach(notification::addNotification));

        assetService.setAssetsInvestigationStatus(notification);
        log.info("Start Alert {}", notification);
        return alertRepository.saveQualityNotificationEntity(notification);
    }

    private QualityNotificationMessage createInvestigation(BPN applicationBpn, String description, Instant targetDate, QualityNotificationSeverity severity, Map.Entry<String, List<Asset>> asset) {
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
                .notificationStatus( QualityNotificationStatus.CREATED)
                .affectedParts(asset.getValue().stream().map(Asset::getId).map(QualityNotificationAffectedPart::new).toList())
                .targetDate(targetDate)
                .severity(severity)
                .edcNotificationId(notificationId)
                .messageId(messageId)
                .isInitial(true)
                .build();
    }

    private QualityNotificationMessage createAlert(BPN applicationBpn, String description, Instant targetDate, QualityNotificationSeverity severity, Asset affectedAsset, Asset parentAsset) {
        final String notificationId = UUID.randomUUID().toString();
        final String messageId = UUID.randomUUID().toString();
        return QualityNotificationMessage.builder()
                .id(notificationId)
                .created(LocalDateTime.now())
                .senderBpnNumber(applicationBpn.value())
                .senderManufacturerName(getManufacturerName(applicationBpn.value()))
                .receiverBpnNumber(parentAsset.getManufacturerId())
                .receiverManufacturerName(getManufacturerName(parentAsset.getManufacturerId()))
                .description(description)
                .notificationStatus( QualityNotificationStatus.CREATED)
                .affectedParts(List.of(new QualityNotificationAffectedPart(affectedAsset.getId())))
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
     * @param status        the NotificationStatus of the notification to update
     * @param reason        the reason for update of the notification
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
                default -> throw new InvestigationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with id '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getNotificationId()));
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
                    throw new InvestigationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with BPN '%s' and application with BPN '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getBpn(), applicationBpn.value()));
                }
            }
            case CLOSED -> {
                if (isInvalidClose) {
                    throw new InvestigationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with BPN '%s' and application with BPN '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getBpn(), applicationBpn.value()));
                }
            }
            default -> throw new InvestigationIllegalUpdate("Unknown Transition from status '%s' to status '%s' is not allowed for notification with id '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getNotificationId()));
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

