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


import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationContent;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SecurityUtils {

    public static String sanitize(String unSanitizedInput) {
        if (unSanitizedInput != null) {
            return unSanitizedInput.replaceAll("\r\n|\r|\n", " ");
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


    public static EDCNotification sanitizeEDCNotification(EDCNotification edcNotification) {
        if (edcNotification != null) {
            EDCNotificationHeader cleanEDCNotificationHeader = sanitizeEDCNotificationHeader(edcNotification);
            EDCNotificationContent cleanEDCNotificationContent = sanitizeEDCNotificationContent(edcNotification);
            return new EDCNotification(cleanEDCNotificationHeader, cleanEDCNotificationContent);
        }
        return null;
    }

    public static EDCNotificationHeader sanitizeEDCNotificationHeader(EDCNotification edcNotification) {
        String cleanRecipientBPN = sanitize(edcNotification.getRecipientBPN());
        String cleanNotificationId = sanitize(edcNotification.getNotificationId());
        String cleanSenderBPN = sanitize(edcNotification.getSenderBPN());
        String cleanSenderAddress = sanitize(edcNotification.getSenderAddress());
        String cleanTargetDate = sanitize(Objects.requireNonNull(edcNotification.getTargetDate()).toString());
        String cleanStatus = edcNotification.convertNotificationStatus().name();
        String cleanClassification = edcNotification.convertNotificationType().getValue();
        String cleanSeverity = sanitize(edcNotification.getSeverity());
        String cleanMessageId = sanitize(edcNotification.getMessageId());
        String cleanRelatedNotificationId = sanitize(edcNotification.getRelatedNotificationId());
        return new EDCNotificationHeader(cleanNotificationId, cleanSenderBPN, cleanSenderAddress, cleanRecipientBPN, cleanClassification, cleanSeverity, cleanRelatedNotificationId, cleanStatus, cleanTargetDate, cleanMessageId);
    }

    public static EDCNotificationContent sanitizeEDCNotificationContent(EDCNotification edcNotification) {
        String cleanInformation = sanitize(edcNotification.getInformation());
        List<String> StringListOfAffectedItems = new ArrayList<>();
        List<QualityNotificationAffectedPart> ListOfAffectedItems = edcNotification.getListOfAffectedItems();
        for (QualityNotificationAffectedPart qualityNotificationAffectedPart : ListOfAffectedItems) {
            String assetId = qualityNotificationAffectedPart.assetId();
            StringListOfAffectedItems.add(assetId);
        }
        List<String> cleanStringListOfAffectedItems = sanitize(StringListOfAffectedItems);
        return new EDCNotificationContent(cleanInformation, cleanStringListOfAffectedItems);
    }
}
