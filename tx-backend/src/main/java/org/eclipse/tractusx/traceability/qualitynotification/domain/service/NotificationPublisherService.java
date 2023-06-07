package org.eclipse.tractusx.traceability.qualitynotification.domain.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationAffectedPart;
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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationPublisherService {

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
    public QualityNotification startNotification(List<String> assetIds, String description, Instant targetDate, QualityNotificationSeverity severity) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        QualityNotification notification = QualityNotification.startNotification(clock.instant(), applicationBPN, description);

        Map<String, List<Asset>> assetsByBPN = assetRepository.getAssetsById(assetIds).stream().collect(Collectors.groupingBy(Asset::getManufacturerId));

        assetsByBPN
                .entrySet()
                .stream()
                .map(it -> createNotification(applicationBPN, description, targetDate, severity, it, QualityNotificationStatus.CREATED))
                .forEach(notification::addNotification);

        return notification;
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
                .notificationStatus(investigationStatus)
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
     * Approves an ongoing notification with the given BPN and ID to the next stage.
     *
     * @param notification the Notification to send
     */
    public QualityNotification approveNotification(QualityNotification notification) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        notification.send(applicationBPN);

        // For each asset within investigation a notification was created before
        notification.getNotifications().forEach(notificationsService::asyncNotificationExecutor);

        return notification;
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
                default -> throw new InvestigationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for investigation with id '%s'".formatted(investigation.getNotificationStatus().name(), status, investigation.getNotificationId()));
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

        final boolean isInvalidAcknowledgeOrAcceptOrDecline = !QualityNotificationSide.RECEIVER.equals(investigation.getNotificationSide()) && applicationBpn.value().equals(investigation.getBpn());
        final boolean isInvalidClose = QualityNotificationStatus.CLOSED.equals(status) && !applicationBpn.value().equals(investigation.getBpn());
        switch (status) {
            case ACKNOWLEDGED, ACCEPTED, DECLINED -> {
                if (isInvalidAcknowledgeOrAcceptOrDecline) {
                    throw new InvestigationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for investigation with BPN '%s' and application with BPN '%s'".formatted(investigation.getNotificationStatus().name(), status, investigation.getBpn(), applicationBpn.value()));
                }
            }
            case CLOSED -> {
                if (isInvalidClose) {
                    throw new InvestigationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for investigation with BPN '%s' and application with BPN '%s'".formatted(investigation.getNotificationStatus().name(), status, investigation.getBpn(), applicationBpn.value()));
                }
            }
            default -> throw new InvestigationIllegalUpdate("Unknown Transition from status '%s' to status '%s' is not allowed for investigation with id '%s'".formatted(investigation.getNotificationStatus().name(), status, investigation.getNotificationId()));
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

