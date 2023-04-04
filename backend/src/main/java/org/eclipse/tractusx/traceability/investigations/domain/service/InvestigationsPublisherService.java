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

package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.investigations.domain.model.AffectedPart;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.model.Severity;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvestigationsPublisherService {

    private final NotificationsService notificationsService;
    private final InvestigationsRepository repository;
    private final InvestigationsReadService investigationsReadService;
    private final AssetRepository assetRepository;

    private final BpnRepository bpnRepository;
    private final Clock clock;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public InvestigationsPublisherService(NotificationsService notificationsService,
                                          InvestigationsRepository repository,
                                          InvestigationsReadService investigationsReadService,
                                          AssetRepository assetRepository,
                                          BpnRepository bpnRepository,
                                          Clock clock) {
        this.notificationsService = notificationsService;
        this.repository = repository;
        this.investigationsReadService = investigationsReadService;
        this.assetRepository = assetRepository;
        this.bpnRepository = bpnRepository;
        this.clock = clock;
    }

    /**
     * Starts a new investigation with the given BPN, asset IDs and description.
     *
     * @param applicationBpn the BPN to use for the investigation
     * @param assetIds       the IDs of the assets to investigate
     * @param description    the description of the investigation
     * @param targetDate     the targetDate of the investigation
     * @param severity       the severity of the investigation
     * @return the ID of the newly created investigation
     */
    public InvestigationId startInvestigation(BPN applicationBpn, List<String> assetIds, String description, Instant targetDate, Severity severity) {
        Investigation investigation = Investigation.startInvestigation(clock.instant(), applicationBpn, description);

        Map<String, List<Asset>> assetsByManufacturer = assetRepository.getAssetsById(assetIds).stream().collect(Collectors.groupingBy(Asset::getManufacturerId));

        assetsByManufacturer
                .entrySet()
                .stream()
                .map(it -> createNotification(applicationBpn, description, targetDate, severity, it, InvestigationStatus.CREATED))
                .forEach(investigation::addNotification);
        logger.info("Start Investigation {}", investigation);
        return repository.save(investigation);
    }

    private Notification createNotification(BPN applicationBpn, String description, Instant targetDate, Severity severity, Map.Entry<String, List<Asset>> asset, InvestigationStatus investigationStatus) {
        final String notificationId = UUID.randomUUID().toString();
        return new Notification(
                notificationId,
                null,
                applicationBpn.value(),
                getManufacturerName(applicationBpn.value()),
                asset.getKey(),
                getManufacturerName(asset.getKey()),
                null,
                null,
                description,
                investigationStatus,
                asset.getValue().stream().map(Asset::getId).map(AffectedPart::new).toList(),
                targetDate,
                severity,
                notificationId,
                null,
                null
        );
    }

    private String getManufacturerName(String bpn) {
        return bpnRepository.findManufacturerName(bpn)
                .orElse(null);
    }

    /**
     * Cancels an ongoing investigation with the given BPN and ID.
     *
     * @param applicationBpn the BPN associated with the investigation
     * @param id             the ID of the investigation to cancel
     */
    public void cancelInvestigation(BPN applicationBpn, Long id) {
        InvestigationId investigationId = new InvestigationId(id);
        Investigation investigation = investigationsReadService.loadInvestigation(investigationId);
        investigation.cancel(applicationBpn);
        repository.update(investigation);
    }

    /**
     * Approves an ongoing investigation with the given BPN and ID to the next stage.
     *
     * @param applicationBpn the BPN associated with the investigation
     * @param id             the ID of the investigation to send
     */
    public void approveInvestigation(BPN applicationBpn, Long id) {
        InvestigationId investigationId = new InvestigationId(id);
        Investigation investigation = investigationsReadService.loadInvestigation(investigationId);
        investigation.send(applicationBpn);
        repository.update(investigation);
        // For each asset within investigation a notification was created before
        investigation.getNotifications().forEach(notificationsService::updateAsync);
    }

    /**
     * Updates an ongoing investigation with the given BPN, ID, status and reason.
     *
     * @param applicationBpn     the BPN associated with the investigation
     * @param investigationIdRaw the ID of the investigation to update
     * @param status             the InvestigationStatus of the investigation to update
     * @param reason             the reason for update of the investigation
     */
    public void updateInvestigationPublisher(BPN applicationBpn, Long investigationIdRaw, InvestigationStatus status, String reason) {
        Investigation investigation = investigationsReadService.loadInvestigation(new InvestigationId(investigationIdRaw));

        validate(applicationBpn, status, investigation);

        List<Notification> allLatestNotificationForEdcNotificationId = getAllLatestNotificationForEdcNotificationId(investigation);
        List<Notification> notificationsToSend = new ArrayList<>();
        logger.info("::updateInvestigationPublisher::allLatestNotificationForEdcNotificationId {}", allLatestNotificationForEdcNotificationId);
        allLatestNotificationForEdcNotificationId.forEach(notification -> {
            Notification notificationToSend = notification.copyAndSwitchSenderAndReceiver(applicationBpn);
            switch (status) {
                case ACKNOWLEDGED -> investigation.acknowledge(notificationToSend);
                case ACCEPTED -> investigation.accept(reason, notificationToSend);
                case DECLINED -> investigation.decline(reason, notificationToSend);
                case CLOSED -> investigation.close(reason, notificationToSend);
                default -> throw new InvestigationIllegalUpdate("Can't update %s investigation with %s status".formatted(investigationIdRaw, status));
            }
            logger.info("::updateInvestigationPublisher::notificationToSend {}", notificationToSend);
            investigation.addNotification(notificationToSend);
            notificationsToSend.add(notificationToSend);
        });
        repository.update(investigation);
        notificationsToSend.forEach(notificationsService::updateAsync);
    }

    private void validate(BPN applicationBpn, InvestigationStatus status, Investigation investigation) {

       final boolean isInvalidAcknowledgeOrAcceptOrDecline = !InvestigationSide.RECEIVER.equals(investigation.getInvestigationSide()) && applicationBpn.value().equals(investigation.getBpn());
       final boolean isInvalidClose = InvestigationStatus.CLOSED.equals(status) && !applicationBpn.value().equals(investigation.getBpn());
        switch (status) {
            case ACKNOWLEDGED, ACCEPTED, DECLINED -> {
                if (isInvalidAcknowledgeOrAcceptOrDecline) {
                    throw new InvestigationIllegalUpdate("Can't update investigation to status: %s for appBpn: %s and investigationBpn: %s".formatted(status, applicationBpn.value(), investigation.getBpn()));
                }
            }
            case CLOSED -> {
                if (isInvalidClose) {
                    throw new InvestigationIllegalUpdate("Can't update investigation to status: %s for appBpn: %s and investigationBpn: %s".formatted(status, applicationBpn.value(), investigation.getBpn()));
                }
            }
            default -> throw new InvestigationIllegalUpdate("Can't perform unknown status update to: %s for investigation with %s status".formatted(status, investigation.getId()));
        }
    }

    private List<Notification> getAllLatestNotificationForEdcNotificationId(Investigation investigation) {
        Map<String, List<Notification>> notificationMap = new HashMap<>();

        for (Notification notification : investigation.getNotifications()) {
            String edcNotificationId = notification.getEdcNotificationId();
            List<Notification> notificationGroup = notificationMap.getOrDefault(edcNotificationId, new ArrayList<>());
            if (notificationGroup.isEmpty()) {
                notificationGroup.add(notification);
            } else {
                Notification latestNotification = notificationGroup.stream().max(Comparator.comparing(Notification::getCreated)).orElse(null);

                if (notification.getCreated().isAfter(latestNotification.getCreated())) {
                    notificationGroup.clear();
                    notificationGroup.add(notification);
                } else if (notification.getCreated().isEqual(latestNotification.getCreated())) {
                    throw new IllegalArgumentException("Two notifications with same edcNotificationId have the same status. This can be happen on old datasets.");
                }
            }
            notificationMap.put(edcNotificationId, notificationGroup);
        }

        List<Notification> latestNotificationElements = new ArrayList<>();
        for (List<Notification> notificationGroup : notificationMap.values()) {
            latestNotificationElements.addAll(notificationGroup);
        }
        return latestNotificationElements;
    }
}
