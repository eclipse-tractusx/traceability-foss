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

package org.eclipse.tractusx.traceability.qualitynotification.application.alert.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationReasonResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationSeverityResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationSideResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationStatusResponse;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSeverity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@SuperBuilder
@ArraySchema(arraySchema = @Schema(description = "Alerts"), maxItems = Integer.MAX_VALUE)
public class AlertResponse extends QualityNotificationResponse {


    public static AlertResponse from(QualityNotification qualityNotification) {
        return AlertResponse
                .builder()
                .id(qualityNotification.getNotificationId().value())
                .status(QualityNotificationStatusResponse.from(qualityNotification.getNotificationStatus()))
                .description(qualityNotification.getDescription())
                .createdBy(getSenderBPN(qualityNotification.getNotifications()))
                .createdByName(getSenderName(qualityNotification.getNotifications()))
                .createdDate(qualityNotification.getCreatedAt().toString())
                .assetIds(Collections.unmodifiableList(qualityNotification.getAssetIds()))
                .channel(QualityNotificationSideResponse.from(qualityNotification.getNotificationSide()))
                .reason(new QualityNotificationReasonResponse(
                        qualityNotification.getCloseReason(),
                        qualityNotification.getAcceptReason(),
                        qualityNotification.getDeclineReason()
                ))
                .sendTo(getReceiverBPN(qualityNotification.getNotifications()))
                .sendToName(getReceiverName(qualityNotification.getNotifications()))
                .severity(QualityNotificationSeverityResponse.from(qualityNotification.getNotifications().stream().findFirst().map(QualityNotificationMessage::getSeverity).orElse(QualityNotificationSeverity.MINOR)))
                .targetDate(qualityNotification.getNotifications().stream().findFirst().map(QualityNotificationMessage::getTargetDate).map(Instant::toString).orElse(null)).build();
    }

    public static PageResult<AlertResponse> fromAsPageResult(PageResult<QualityNotification> qualityNotificationPageResult) {
        List<AlertResponse> investigationResponses = qualityNotificationPageResult.content().stream().map(AlertResponse::from).toList();
        int pageNumber = qualityNotificationPageResult.page();
        int pageSize = qualityNotificationPageResult.pageSize();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<AlertResponse> investigationDataPage = new PageImpl<>(investigationResponses, pageable, qualityNotificationPageResult.totalItems());
        return new PageResult<>(investigationDataPage);
    }

    private static String getSenderBPN(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getSenderBpnNumber)
                .orElse(null);
    }

    private static String getReceiverBPN(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getReceiverBpnNumber)
                .orElse(null);
    }

    private static String getSenderName(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getSenderManufacturerName)
                .orElse(null);
    }

    private static String getReceiverName(Collection<QualityNotificationMessage> notifications) {
        return notifications.stream()
                .findFirst()
                .map(QualityNotificationMessage::getReceiverManufacturerName)
                .orElse(null);
    }
}
