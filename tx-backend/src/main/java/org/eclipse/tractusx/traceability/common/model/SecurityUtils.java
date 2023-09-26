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


import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationContent;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SecurityUtils {
    private static final List<String> unwantedStrings = Arrays.asList("\r\n", "\r", "\n");
    public static String sanitize(String unSanitizedInput) {
        if (unSanitizedInput != null) {
            return unSanitizedInput.replaceAll(unwantedStrings.toString(), " ");
        }
        return null;
    }

    public static List<String> sanitize(List<String> unSanitizedList) {
        if (unSanitizedList != null) {
            List<String> cleanListOfAffectedItems = new ArrayList<>();
            for (String affectedItem : unSanitizedList) {
                String cleanAffectedItem = sanitize(affectedItem);
                cleanListOfAffectedItems.add(cleanAffectedItem);
            }
            return cleanListOfAffectedItems;
        }
        return null;
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
        String cleanTargetDate = sanitize(Objects.requireNonNull(edcNotificationHeader.targetDate()));
        String cleanStatus = edcNotificationHeader.status();
        String cleanClassification = edcNotificationHeader.classification();
        String cleanSeverity = sanitize(edcNotificationHeader.severity());
        String cleanMessageId = sanitize(edcNotificationHeader.messageId());
        String cleanRelatedNotificationId = sanitize(edcNotificationHeader.relatedNotificationId());
        return new EDCNotificationHeader(cleanNotificationId, cleanSenderBPN, cleanSenderAddress, cleanRecipientBPN, cleanClassification, cleanSeverity, cleanRelatedNotificationId, cleanStatus, cleanTargetDate, cleanMessageId);
    }

    private static EDCNotificationContent sanitize(EDCNotificationContent edcNotificationContent) {
        String cleanInformation = sanitize(edcNotificationContent.information());
        List<String> cleanStringListOfAffectedItems = sanitize(edcNotificationContent.listOfAffectedItems());
        return new EDCNotificationContent(cleanInformation, cleanStringListOfAffectedItems);
    }
}
