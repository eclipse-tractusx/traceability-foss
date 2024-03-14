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

package org.eclipse.tractusx.traceability.qualitynotification.application.base.mapper;

import lombok.experimental.UtilityClass;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import qualitynotification.base.response.QualityNotificationMessageResponse;
import qualitynotification.base.response.QualityNotificationSeverityResponse;
import qualitynotification.base.response.QualityNotificationSideResponse;
import qualitynotification.base.response.QualityNotificationStatusResponse;

import java.util.List;

@UtilityClass
public class QualityNotificationMapper {

    public static QualityNotificationSeverityResponse from(QualityNotificationSeverity qualityNotificationSeverity) {
        return QualityNotificationSeverityResponse.fromString(qualityNotificationSeverity.getRealName());
    }

    public static QualityNotificationSideResponse from(QualityNotificationSide side) {
        return QualityNotificationSideResponse.valueOf(side.name());
    }

    public static QualityNotificationStatusResponse from(QualityNotificationStatus qualityNotificationStatus) {
        return QualityNotificationStatusResponse.fromStringValue(qualityNotificationStatus.name());
    }

    public static List<QualityNotificationMessageResponse> fromNotifications(List<QualityNotificationMessage> notificationMessages) {
        return notificationMessages.stream().map(QualityNotificationMapper::fromNotification).toList();
    }

    public static QualityNotificationMessageResponse fromNotification(QualityNotificationMessage notificationMessage) {
        return QualityNotificationMessageResponse
                .builder()
                .id(notificationMessage.getId())
                .edcNotificationId(notificationMessage.getEdcNotificationId())
                .contractAgreementId(notificationMessage.getContractAgreementId())
                .notificationReferenceId(notificationMessage.getNotificationReferenceId())
                .messageId(notificationMessage.getMessageId())
                .updated(notificationMessage.getUpdated())
                .sendToName(notificationMessage.getSendToName())
                .status(fromStatus(notificationMessage.getNotificationStatus()))
                .targetDate(notificationMessage.getTargetDate())
                .created(notificationMessage.getCreated())
                .errorMessage(notificationMessage.getErrorMessage())
                .build();
    }

    public static QualityNotificationStatusResponse fromStatus(QualityNotificationStatus qualityNotificationStatus) {
        return QualityNotificationStatusResponse.fromStringValue(qualityNotificationStatus.name());
    }
}
