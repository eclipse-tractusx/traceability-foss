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

package org.eclipse.tractusx.traceability.notification.domain.base.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.service.DiscoveryService;
import org.eclipse.tractusx.traceability.discovery.infrastructure.exception.DiscoveryFinderException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.ContractNegotiationException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.NoCatalogItemException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.NoEndpointDataReferenceException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.notification.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(dontRollbackOn = DiscoveryFinderException.class)
public class EdcNotificationServiceImpl implements EdcNotificationService {

    private final NotificationsEDCFacade edcFacade;
    private final DiscoveryService discoveryService;
    private final NotificationRepository notificationRepository;

    @Override
    @Async(value = AssetsAsyncConfig.UPDATE_NOTIFICATION_EXECUTOR)
    public CompletableFuture<NotificationMessage> asyncNotificationMessageExecutor(NotificationMessage message) {
        log.info("::asyncNotificationExecutor::message {}", message);
        try {
            Discovery discovery = discoveryService.getDiscoveryByBPN(message.getSendTo());

            String senderEdcUrl = discovery.getSenderUrl();
            List<String> receiverUrls = emptyIfNull(discovery.getReceiverUrls());
            List<Boolean> sendResults = List.of();

            if (message.getType().equals(NotificationType.ALERT)) {
                log.info("::asyncNotificationExecutor::isQualityAlert");
                sendResults = receiverUrls
                        .stream().map(receiverUrl -> handleSendingNotification(message, senderEdcUrl, receiverUrl)).toList();
            }

            if (message.getType().equals(NotificationType.INVESTIGATION)) {
                log.info("::asyncNotificationExecutor::isQualityInvestigation");
                sendResults = receiverUrls
                        .stream().map(receiverUrl -> handleSendingNotification(message, senderEdcUrl, receiverUrl)).toList();
            }

            Boolean wasSent = sendResults.stream().anyMatch(Boolean.TRUE::equals);

            if (Boolean.TRUE.equals(wasSent)) {
                return CompletableFuture.completedFuture(message);
            }

            return CompletableFuture.completedFuture(null);

        } catch (DiscoveryFinderException discoveryFinderException) {
            enrichNotificationByError(discoveryFinderException, message);
            return CompletableFuture.completedFuture(null);
        }
    }

    private boolean handleSendingNotification(NotificationMessage message, String senderEdcUrl, String receiverUrl) {
        try {
            edcFacade.startEdcTransfer(message, receiverUrl, senderEdcUrl);
            return true;
        } catch (NoCatalogItemException e) {
            log.warn("Could not send message to {} no catalog item found. ", receiverUrl, e);
            enrichNotificationByError(e, message);
        } catch (SendNotificationException e) {
            log.warn("Could not send message to {} ", receiverUrl, e);
            enrichNotificationByError(e, message);
        } catch (NoEndpointDataReferenceException e) {
            log.warn("Could not send message to {} no endpoint data reference found", receiverUrl, e);
            enrichNotificationByError(e, message);
        } catch (ContractNegotiationException e) {
            log.warn("Could not send message to {} could not negotiate contract agreement", receiverUrl, e);
            enrichNotificationByError(e, message);
        }
        return false;
    }

    private void enrichNotificationByError(Exception e, NotificationMessage notificationMessage) {
        log.info("Retrieving notification by message id {}", notificationMessage.getEdcNotificationId());

        Optional<Notification> optionalNotificationByEdcId = notificationRepository.findByEdcNotificationId(notificationMessage.getEdcNotificationId());

        log.info("Successfully executed retrieving quality notification by message id");
        if (optionalNotificationByEdcId.isPresent()) {
            log.info("Notification for error message enrichment {}", optionalNotificationByEdcId.get());
            optionalNotificationByEdcId.get().getNotifications().forEach(message1 -> log.info("Message found {}", message1));
            optionalNotificationByEdcId.get().secondLatestNotifications().forEach(qmMessage -> {
                log.info("Message from second latest notification {}", qmMessage);
                qmMessage.setErrorMessage(e.getMessage());
            });

            notificationRepository.updateErrorMessage(optionalNotificationByEdcId.get());
        } else {
            log.warn("Notification NOT FOUND for error message enrichment notification id {}", notificationMessage.getId());
        }
    }
}

