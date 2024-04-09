/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.common.model;


import lombok.experimental.UtilityClass;
import notification.request.CloseNotificationRequest;
import notification.request.StartNotificationRequest;
import notification.request.UpdateNotificationStatusTransitionRequest;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotificationContent;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotificationHeader;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@UtilityClass
public class SecurityUtils {


    private static final String UNWANTED_REGEX = "\r\n|\r|\n";

    public static String sanitize(String unSanitizedInput) {
        if (unSanitizedInput != null) {
            return unSanitizedInput.replaceAll(UNWANTED_REGEX, " ");
        }
        return null;
    }

    public static List<String> sanitize(List<String> unSanitizedList) {
        if (!unSanitizedList.isEmpty()) {
            return unSanitizedList.stream()
                    .map(SecurityUtils::sanitize)
                    .toList();
        }
        return Collections.emptyList();
    }

    public static StartNotificationRequest sanitize(StartNotificationRequest request) {
        String cleanDescription = sanitize(request.getDescription());
        String cleanReceiverBpn = sanitize(request.getReceiverBpn());
        List<String> cleanPartIds = sanitize(request.getPartIds());
        return StartNotificationRequest.builder()
                .title(request.getTitle())
                .description(cleanDescription)
                .targetDate(request.getTargetDate())
                .severity(request.getSeverity())
                .isAsBuilt(request.isAsBuilt())
                .receiverBpn(cleanReceiverBpn)
                .type(request.getType())
                .partIds(cleanPartIds)
                .build();
    }


    public static CloseNotificationRequest sanitize(CloseNotificationRequest closeInvestigationRequest) {
        String cleanReason = sanitize(closeInvestigationRequest.getReason());
        return CloseNotificationRequest.builder().reason(cleanReason).build();
    }

    public static UpdateNotificationStatusTransitionRequest sanitize(UpdateNotificationStatusTransitionRequest updateInvestigationRequest) {
        String cleanReason = sanitize(updateInvestigationRequest.getReason());
        return UpdateNotificationStatusTransitionRequest.builder().status(updateInvestigationRequest.getStatus()).reason(cleanReason).build();
    }

    public static EDCNotification sanitize(EDCNotification edcNotification) {
        if (edcNotification != null) {
            EDCNotificationHeader cleanEDCNotificationHeader = sanitize(edcNotification.header());
            EDCNotificationContent cleanEDCNotificationContent = sanitize(edcNotification.content());
            return new EDCNotification(cleanEDCNotificationHeader, cleanEDCNotificationContent);
        }
        return null;
    }

    private static EDCNotificationHeader sanitize(EDCNotificationHeader edcNotificationHeader) {
        String cleanRecipientBPN = sanitize(edcNotificationHeader.recipientBPN());
        String cleanNotificationId = sanitize(edcNotificationHeader.notificationId());
        String cleanSenderBPN = sanitize(edcNotificationHeader.senderBPN());
        String cleanSenderAddress = sanitize(edcNotificationHeader.senderAddress());
        String cleanTargetDate = sanitize(edcNotificationHeader.targetDate());
        String cleanStatus = edcNotificationHeader.status();
        String cleanClassification = edcNotificationHeader.classification();
        String cleanSeverity = sanitize(edcNotificationHeader.severity());
        String cleanMessageId = sanitize(edcNotificationHeader.messageId());
        String cleanRelatedNotificationId = sanitize(edcNotificationHeader.relatedNotificationId());
        return new EDCNotificationHeader(cleanNotificationId, cleanSenderBPN, cleanSenderAddress, cleanRecipientBPN, cleanClassification, cleanSeverity, cleanRelatedNotificationId, cleanStatus, cleanTargetDate, cleanMessageId);
    }

    private static EDCNotificationContent sanitize(EDCNotificationContent edcNotificationContent) {
        String cleanInformation = sanitize(edcNotificationContent.information());
        List<String> cleanStringListOfAffectedItems = sanitize(emptyIfNull(edcNotificationContent.listOfAffectedItems()));
        return new EDCNotificationContent(cleanInformation, cleanStringListOfAffectedItems);
    }
}
