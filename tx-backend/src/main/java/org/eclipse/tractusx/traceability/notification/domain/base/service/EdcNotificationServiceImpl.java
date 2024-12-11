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
import org.eclipse.tractusx.traceability.notification.domain.base.exception.CatalogItemPolicyMismatchException;
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
    public CompletableFuture<NotificationMessage> asyncNotificationMessageExecutor(NotificationMessage message, Notification notification) {
        log.info("::asyncNotificationExecutor::message {}", message);
        try {
            Discovery discovery = discoveryService.getDiscoveryByBPN(message.getSentTo());

            String senderEdcUrl = discovery.getSenderUrl();
            List<String> receiverUrls = emptyIfNull(discovery.getReceiverUrls());
            List<Boolean> sendResults = List.of();

            if (message.getType().equals(NotificationType.ALERT)) {
                log.info("::asyncNotificationExecutor::isQualityAlert");
                sendResults = receiverUrls
                        .stream().map(receiverUrl -> handleSendingNotification(message, senderEdcUrl, receiverUrl, notification)).toList();
            }

            if (message.getType().equals(NotificationType.INVESTIGATION)) {
                log.info("::asyncNotificationExecutor::isQualityInvestigation");
                sendResults = receiverUrls
                        .stream().map(receiverUrl -> handleSendingNotification(message, senderEdcUrl, receiverUrl, notification)).toList();
            }

            Boolean wasSent = sendResults.stream().anyMatch(Boolean.TRUE::equals);

            if (Boolean.TRUE.equals(wasSent)) {
                resetNotificationError(notification, message);
                return CompletableFuture.completedFuture(message);
            }

            return CompletableFuture.completedFuture(null);

        } catch (DiscoveryFinderException discoveryFinderException) {
            enrichNotificationByError(discoveryFinderException, notification, message);
            return CompletableFuture.completedFuture(null);
        }
    }

    private boolean handleSendingNotification(NotificationMessage message, String senderEdcUrl, String receiverUrl, Notification notification) {
        try {
            edcFacade.startEdcTransfer(message, receiverUrl, senderEdcUrl, notification);
            return true;
        } catch (NoCatalogItemException e) {
            log.warn("Could not send message to {} no catalog item found. ", receiverUrl, e);
            enrichNotificationByError(e, notification, message);
        } catch (CatalogItemPolicyMismatchException e) {
            String error = String.format("Could not send message to '%s'. Provided policy in the edc notification contract does not comply with configured application policy. Exception message: '%s'", receiverUrl, e.getMessage());
            log.warn(error);
            enrichNotificationByError(e, notification, message);
        } catch (SendNotificationException e) {
            log.warn("Could not send message to {} ", receiverUrl, e);
            enrichNotificationByError(e, notification, message);
        } catch (NoEndpointDataReferenceException e) {
            log.warn("Could not send message to {} no endpoint data reference found", receiverUrl, e);
            enrichNotificationByError(e, notification, message);
        } catch (ContractNegotiationException e) {
            log.warn("Could not send message to {} could not negotiate contract agreement", receiverUrl, e);
            enrichNotificationByError(e, notification, message);
        }
        return false;
    }

    private void enrichNotificationByError(Exception e, Notification notification, NotificationMessage message) {
        log.info("Notification for error message enrichment {}", message);
        message.setErrorMessage(e.getMessage());
        notificationRepository.updateErrorMessage(notification, message);
    }

    private void resetNotificationError(Notification notification, NotificationMessage message) {
        log.info("Notification for resetting error message {}", message);
        message.setErrorMessage(null);
        notificationRepository.updateErrorMessage(notification, message);
    }

}

