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
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.investigations.domain.model.AffectedPart;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.model.Severity;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationReceiverBpnMismatchException;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.Clock;
import java.time.Instant;
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
    private final Clock clock;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public InvestigationsPublisherService(NotificationsService notificationsService, InvestigationsRepository repository, InvestigationsReadService investigationsReadService, AssetRepository assetRepository, Clock clock) {
        this.notificationsService = notificationsService;
        this.repository = repository;
        this.investigationsReadService = investigationsReadService;
        this.assetRepository = assetRepository;
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

        assetsByManufacturer.entrySet().stream()
                .map(it -> new Notification(
                        UUID.randomUUID().toString(),
                        null,
                        applicationBpn.value(),
                        it.getKey(),
                        null,
                        null,
                        description,
                        InvestigationStatus.RECEIVED,
                        it.getValue().stream().map(Asset::getId).map(AffectedPart::new).toList(),
                        targetDate,
                        severity
                )).forEach(investigation::addNotification);
        logger.info("Start Investigation {}", investigation);
        return repository.save(investigation);
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
     * Sends an ongoing investigation with the given BPN and ID to the next stage.
     *
     * @param applicationBpn the BPN associated with the investigation
     * @param id             the ID of the investigation to send
     */
    public void sendInvestigation(BPN applicationBpn, Long id) {
        InvestigationId investigationId = new InvestigationId(id);
        Investigation investigation = investigationsReadService.loadInvestigation(investigationId);
        investigation.send(applicationBpn);
        repository.update(investigation);
        final boolean isReceiver = investigation.getInvestigationSide().equals(InvestigationSide.RECEIVER);
        investigation.getNotifications().forEach(notification -> notificationsService.updateAsync(notification, isReceiver));
    }

    /**
     * Closes an ongoing investigation with the given BPN, ID and reason.
     *
     * @param applicationBpn the BPN associated with the investigation
     * @param id             the ID of the investigation to close
     * @param reason         the reason for closing the investigation
     */
    public void closeInvestigation(BPN applicationBpn, Long id, String reason) {
        InvestigationId investigationId = new InvestigationId(id);
        Investigation investigation = investigationsReadService.loadInvestigation(investigationId);
        logger.info("InvestigationPublisherService: closeInvestigation {}", investigation);
        investigation.close(applicationBpn, reason);
        repository.update(investigation);
        investigation.getNotifications().forEach(notification -> {
            if (notification.existOnReceiverSide()) {
                notificationsService.updateAsync(notification);
            } else {
                notification.updateNotificationReferenceId(notification.getId());
            }
        });
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
        List<Notification> invalidNotifications = invalidNotifications(investigation, applicationBpn);

        if (!invalidNotifications.isEmpty()) {
            StringBuilder builder = new StringBuilder("Investigation receiverBpnNumber mismatch for notifications with IDs: ");
            for (Notification notification : invalidNotifications) {
                builder.append(notification.getId()).append(", ");
            }
            builder.delete(builder.length() - 2, builder.length()); // Remove the last ", " from the string
            throw new InvestigationReceiverBpnMismatchException(builder.toString());
        }

        switch (status) {
            case ACKNOWLEDGED -> investigation.acknowledge();
            case ACCEPTED -> investigation.accept(reason);
            case DECLINED -> investigation.decline(reason);
            default -> throw new InvestigationIllegalUpdate("Can't update %s investigation with %s status".formatted(investigationIdRaw, status));
        }

        repository.update(investigation);

        final boolean isReceiver = investigation.getInvestigationSide().equals(InvestigationSide.RECEIVER);

        investigation.getNotifications().forEach(notification -> notificationsService.updateAsync(notification, isReceiver));
    }

    private List<Notification> invalidNotifications(final Investigation investigation, final BPN applicationBpn) {
        final String applicationBpnValue = applicationBpn.value();
        return investigation.getNotifications().stream()
                .filter(notification -> !notification.getReceiverBpnNumber().equals(applicationBpnValue)).toList();
    }
}
