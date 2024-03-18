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

package org.eclipse.tractusx.traceability.qualitynotification.domain.base.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.service.DiscoveryService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.ContractNegotiationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.NoCatalogItemException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.NoEndpointDataReferenceException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.common.config.ApplicationProfiles.NOT_INTEGRATION_TESTS;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(dontRollbackOn = ContractNegotiationException.class)
@Profile(NOT_INTEGRATION_TESTS)
public class EdcNotificationServiceImpl implements EdcNotificationService {

    private final InvestigationsEDCFacade edcFacade;
    private final DiscoveryService discoveryService;
    private final InvestigationRepository investigationRepository;

    @Override
    @Async(value = AssetsAsyncConfig.UPDATE_NOTIFICATION_EXECUTOR)
    public CompletableFuture<QualityNotificationMessage> asyncNotificationMessageExecutor(QualityNotificationMessage message) {
        log.info("::asyncNotificationExecutor::message {}", message);
        Discovery discovery = discoveryService.getDiscoveryByBPN(message.getSendTo());
        String senderEdcUrl = discovery.getSenderUrl();
        List<String> receiverUrls = emptyIfNull(discovery.getReceiverUrls());
        List<Boolean> sendResults = List.of();

        if (message.getType().equals(QualityNotificationType.ALERT)) {
            log.info("::asyncNotificationExecutor::isQualityAlert");
            sendResults = receiverUrls
                    .stream().map(receiverUrl -> handleSendingNotification(message, senderEdcUrl, receiverUrl)).toList();
        }

        if (message.getType().equals(QualityNotificationType.INVESTIGATION)) {
            log.info("::asyncNotificationExecutor::isQualityInvestigation");
            sendResults = receiverUrls
                    .stream().map(receiverUrl -> handleSendingNotification(message, senderEdcUrl, receiverUrl)).toList();
        }

        Boolean wasSent = sendResults.stream().anyMatch(Boolean.TRUE::equals);

        if (Boolean.TRUE.equals(wasSent)) {
            return CompletableFuture.completedFuture(message);
        }

        return CompletableFuture.completedFuture(null);
    }

    private boolean handleSendingNotification(QualityNotificationMessage message, String senderEdcUrl, String receiverUrl) {
        try {
            edcFacade.startEdcTransfer(message, receiverUrl, senderEdcUrl);
            return true;
        } catch (NoCatalogItemException e) {
            log.warn("Could not send message to {} no catalog item found. ", receiverUrl, e);
            enrichQualityNotificationByError(e, message);
        } catch (SendNotificationException e) {
            log.warn("Could not send message to {} ", receiverUrl, e);
            enrichQualityNotificationByError(e, message);
        } catch (NoEndpointDataReferenceException e) {
            log.warn("Could not send message to {} no endpoint data reference found", receiverUrl, e);
            enrichQualityNotificationByError(e, message);
        } catch (ContractNegotiationException e) {
            log.warn("Could not send message to {} could not negotiate contract agreement", receiverUrl, e);
            enrichQualityNotificationByError(e, message);
        }
        return false;
    }

    private void enrichQualityNotificationByError(Exception e, QualityNotificationMessage message) {
        log.info("Retrieving quality notification by message id {}", message.getEdcNotificationId());

        Optional<QualityNotification> optionalQualityNotificationById = investigationRepository.findByNotificationMessageId(message.getEdcNotificationId());
        log.info("Successfully executed retrieving quality notification by message id");
        if (optionalQualityNotificationById.isPresent()) {
            log.info("Quality Notification for error message enrichment {}", optionalQualityNotificationById.get());
            optionalQualityNotificationById.get().secondLatestNotifications().forEach(qmMessage -> qmMessage.setErrorMessage(e.getMessage()));
            investigationRepository.updateErrorMessage(optionalQualityNotificationById.get());
        } else {
            log.warn("Quality Notification NOT FOUND for error message enrichment notification id {}", message.getId());
        }
    }
}

