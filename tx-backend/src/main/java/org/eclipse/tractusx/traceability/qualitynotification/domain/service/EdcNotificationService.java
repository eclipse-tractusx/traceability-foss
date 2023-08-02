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

package org.eclipse.tractusx.traceability.qualitynotification.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.service.DiscoveryService;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.InvestigationsEDCFacade;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.NoCatalogItemException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.repository.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class EdcNotificationService {

    private final InvestigationsEDCFacade edcFacade;
    private final InvestigationRepository investigationRepository;
    private final AlertRepository alertRepository;
    private final DiscoveryService discoveryService;


    @Async(value = AssetsAsyncConfig.UPDATE_NOTIFICATION_EXECUTOR)
    public void asyncNotificationExecutor(QualityNotificationMessage notification) {
        log.info("::asyncNotificationExecutor::notification {}", notification);
        Discovery discovery = discoveryService.getDiscoveryByBPN(notification.getReceiverBpnNumber());
        String senderEdcUrl = discovery.getSenderUrl();

        if (notification.getType().equals(QualityNotificationType.ALERT)) {
            log.info("::asyncNotificationExecutor::isQualityAlert");
            emptyIfNull(discovery.getReceiverUrls())
                    .forEach(receiverUrl -> handleSendingAlert(notification, senderEdcUrl, receiverUrl));
        }

        if (notification.getType().equals(QualityNotificationType.INVESTIGATION)) {
            log.info("::asyncNotificationExecutor::isQualityInvestigation");
            emptyIfNull(discovery.getReceiverUrls())
                    .forEach(receiverUrl -> handleSendingInvestigation(notification, senderEdcUrl, receiverUrl));
        }


    }

    private void handleSendingAlert(QualityNotificationMessage notification, String senderEdcUrl, String receiverUrl) {
        try{
        edcFacade.startEDCTransfer(notification, receiverUrl, senderEdcUrl);
        alertRepository.updateQualityNotificationMessageEntity(notification);
        } catch (NoCatalogItemException e) {
            log.warn("Could not send alert to {} no catalog item found.", receiverUrl);
        }
    }

    private void handleSendingInvestigation(QualityNotificationMessage notification, String senderEdcUrl, String receiverUrl) {
        try{
            edcFacade.startEDCTransfer(notification, receiverUrl, senderEdcUrl);
            alertRepository.updateQualityNotificationMessageEntity(notification);
        } catch (NoCatalogItemException e) {
            log.warn("Could not send investigation to {} no catalog item found.", receiverUrl);
        }
    }
}
