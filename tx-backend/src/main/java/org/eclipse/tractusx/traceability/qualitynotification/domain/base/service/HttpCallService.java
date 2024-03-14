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
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.BadRequestException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class HttpCallService {

    private final RestTemplate edcNotificationTemplate;
    private final InvestigationRepository investigationRepository;
    private final AlertRepository alertRepository;


    public void sendRequest(final EdcNotificationRequest request, QualityNotificationMessage message) {
        HttpEntity<String> entity = new HttpEntity<>(request.getBody(), request.getHeaders());
        try {
            var response = edcNotificationTemplate.exchange(request.getUrl(), HttpMethod.POST, entity, new ParameterizedTypeReference<>() {
            });
            log.info("Control plane responded with status: {}", response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BadRequestException(format("Control plane responded with: %s", response.getStatusCode()));
            } else {
                String edcNotificationId = message.getEdcNotificationId();
                if (message.getType().equals(QualityNotificationType.INVESTIGATION)) {
                    Optional<QualityNotification> optionalQualityNotificationById = investigationRepository.findByEdcNotificationId(edcNotificationId);
                    if (optionalQualityNotificationById.isPresent()) {
                        optionalQualityNotificationById.ifPresent(investigationRepository::updateQualityNotificationEntity);
                        log.info("Updated qualitynotification message as investigation with id {}.", optionalQualityNotificationById.get().getNotificationId().value());
                    }
                } else {
                    Optional<QualityNotification> optionalQualityNotificationById = alertRepository.findByEdcNotificationId(edcNotificationId);
                    if (optionalQualityNotificationById.isPresent()) {
                        optionalQualityNotificationById.ifPresent(alertRepository::updateQualityNotificationEntity);
                        log.info("Updated qualitynotification message as alert with id {}.", optionalQualityNotificationById.get().getNotificationId().value());
                    }
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw e;
        }
    }
}
