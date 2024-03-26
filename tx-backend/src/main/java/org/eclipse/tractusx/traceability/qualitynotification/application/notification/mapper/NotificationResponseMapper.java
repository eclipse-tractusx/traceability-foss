/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.qualitynotification.application.notification.mapper;

import lombok.experimental.UtilityClass;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import qualitynotification.base.response.QualityNotificationReasonResponse;
import qualitynotification.base.response.QualityNotificationResponse;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.eclipse.tractusx.traceability.qualitynotification.application.notification.mapper.QualityNotificationMapper.fromNotifications;

@UtilityClass
public class NotificationResponseMapper {

    public static QualityNotificationResponse from(QualityNotification qualityNotification) {
        return QualityNotificationResponse
                .builder()
                .id(qualityNotification.getNotificationId().value())
                .status(QualityNotificationMapper.from(qualityNotification.getNotificationStatus()))
                .description(qualityNotification.getDescription())
                .createdBy(getSenderBPN(qualityNotification.getNotifications()))
                .createdByName(getSenderName(qualityNotification.getNotifications()))
                .createdDate(qualityNotification.getCreatedAt().toString())
                .assetIds(Collections.unmodifiableList(qualityNotification.getAssetIds()))
                .channel(QualityNotificationMapper.from(qualityNotification.getNotificationSide()))
                .type(QualityNotificationMapper.from(qualityNotification.getNotificationType()))
                .reason(new QualityNotificationReasonResponse(
                        qualityNotification.getCloseReason(),
                        qualityNotification.getAcceptReason(),
                        qualityNotification.getDeclineReason()
                ))
                .sendTo(getReceiverBPN(qualityNotification.getNotifications()))
                .sendToName(getReceiverName(qualityNotification.getNotifications()))
                .severity(QualityNotificationMapper.from(qualityNotification.getNotifications().stream().findFirst().map(QualityNotificationMessage::getSeverity).orElse(QualityNotificationSeverity.MINOR)))
                .targetDate(qualityNotification.getNotifications().stream().findFirst().map(QualityNotificationMessage::getTargetDate).map(Instant::toString).orElse(null))
                .messages(fromNotifications(qualityNotification.getNotifications()))
                .build();
    }

    public static PageResult<QualityNotificationResponse> fromAsPageResult(PageResult<QualityNotification> qualityNotificationPageResult) {
        List<QualityNotificationResponse> investigationResponses = qualityNotificationPageResult.content().stream().map(NotificationResponseMapper::from).toList();
        int pageNumber = qualityNotificationPageResult.page();
        int pageSize = qualityNotificationPageResult.pageSize();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<QualityNotificationResponse> investigationDataPage = new PageImpl<>(investigationResponses, pageable, qualityNotificationPageResult.totalItems());
        return new PageResult<>(investigationDataPage);
    }

    private static String getSenderBPN(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getCreatedBy)
                .orElse(null);
    }

    private static String getReceiverBPN(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getSendTo)
                .orElse(null);
    }

    private static String getSenderName(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getCreatedByName)
                .orElse(null);
    }

    private static String getReceiverName(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getSendToName)
                .orElse(null);
    }
}