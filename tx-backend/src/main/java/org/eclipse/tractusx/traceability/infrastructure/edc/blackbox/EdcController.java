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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.config.FeatureFlags;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.NotificationType;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.validators.ValidEDCNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.service.AlertsReceiverService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service.InvestigationsReceiverService;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
@Hidden
@RestController
@Validated
@RequiredArgsConstructor
public class EdcController {

    private final InvestigationsReceiverService investigationsReceiverService;
    private final AlertsReceiverService alertsReceiverService;

    /**
     * Receiver API call for EDC Transfer
     */
    @PostMapping("/qualitynotifications/receive")
    public void qualityNotificationInvestigationReceive(final @ValidEDCNotification @Valid @RequestBody EDCNotification edcNotification) {
        log.info("EdcController [qualityNotificationReceive] notificationId:{}", edcNotification);
        validateIsQualityInvestigation(edcNotification);
        investigationsReceiverService.handleNotificationReceive(edcNotification);
    }

    /**
     * Update API call for EDC Transfer
     */
    @PostMapping("/qualitynotifications/update")
    public void qualityNotificationInvestigationUpdate(final @ValidEDCNotification @Valid @RequestBody EDCNotification edcNotification) {
        log.info("EdcController [qualityNotificationUpdate] notificationId:{}", edcNotification);
        validateIsQualityInvestigation(edcNotification);
        investigationsReceiverService.handleNotificationUpdate(edcNotification);
    }

    /**
     * Receiver API call for EDC Transfer
     */
    @PostMapping("/qualitynotifications/receive")
    public void qualityNotificationAlertReceive(final @ValidEDCNotification @Valid @RequestBody EDCNotification edcNotification) {
        log.info("EdcController [qualityNotificationReceive] notificationId:{}", edcNotification);
        validateIsAlert(edcNotification);
        alertsReceiverService.handleNotificationReceive(edcNotification);
    }

    /**
     * Update API call for EDC Transfer
     */
    @PostMapping("/qualitynotifications/update")
    public void qualityNotificationAlertUpdate(final @ValidEDCNotification @Valid @RequestBody EDCNotification edcNotification) {
        log.info("EdcController [qualityNotificationUpdate] notificationId:{}", edcNotification);
        validateIsAlert(edcNotification);
        alertsReceiverService.handleNotificationUpdate(edcNotification);
    }


    private void validateIsQualityInvestigation(EDCNotification edcNotification) {
        NotificationType notificationType = edcNotification.convertNotificationType();
        if (!notificationType.equals(NotificationType.QMINVESTIGATION)) {
            throw new InvestigationIllegalUpdate("Received %s classified edc notification which is not an investigation".formatted(notificationType));
        }
    }

    private void validateIsAlert(EDCNotification edcNotification) {
        NotificationType notificationType = edcNotification.convertNotificationType();
        if (!notificationType.equals(NotificationType.QMALERT)) {
            throw new InvestigationIllegalUpdate("Received %s classified edc notification which is not an alert".formatted(notificationType));
        }
    }
}

