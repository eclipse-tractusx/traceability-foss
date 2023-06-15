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

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationReasonResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationSeverityResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationSideResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationStatusResponse;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlertResponseTest {

    @Test
    void givenQualityNotification_whenFrom_thenConstructProperAlertResponse() {
        // given
        final QualityNotification notification = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACCEPTED, QualityNotificationStatus.CREATED);

        // when
        final AlertResponse result = AlertResponse.from(notification);

        // then
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", notification.getNotificationId().value())
                .hasFieldOrPropertyWithValue("status", QualityNotificationStatusResponse.ACCEPTED)
                .hasFieldOrPropertyWithValue("description", notification.getDescription())
                .hasFieldOrPropertyWithValue("createdBy", notification.getNotifications().stream()
                        .findFirst()
                        .map(QualityNotificationMessage::getSenderBpnNumber)
                        .orElse(null))
                .hasFieldOrPropertyWithValue("createdByName", notification.getNotifications().stream()
                        .findFirst()
                        .map(QualityNotificationMessage::getSenderManufacturerName)
                        .orElse(null))
                .hasFieldOrPropertyWithValue("createdDate", notification.getCreatedAt().toString())
                .hasFieldOrPropertyWithValue("assetIds", notification.getAssetIds())
                .hasFieldOrPropertyWithValue("channel", QualityNotificationSideResponse.SENDER)
                .hasFieldOrPropertyWithValue("reason", new QualityNotificationReasonResponse(
                        notification.getCloseReason(),
                        notification.getAcceptReason(),
                        notification.getDeclineReason()
                ))
                .hasFieldOrPropertyWithValue("sendTo", "recipientBPN")
                .hasFieldOrPropertyWithValue("sendToName", "receiverManufacturerName")
                .hasFieldOrPropertyWithValue("severity", QualityNotificationSeverityResponse.MINOR)
                .hasFieldOrPropertyWithValue("targetDate", null);
    }

    @Test
    void givenQualityNotificationPaged_whenFromAsPageResult_thenConstructProperAlertResponse() {
        // given
        final QualityNotification notification = InvestigationTestDataFactory.createInvestigationTestData(QualityNotificationStatus.ACCEPTED, QualityNotificationStatus.CREATED);
        final Integer page1 = 1;
        final Integer pageCount1 = 1;
        final Integer pageSize1 = 1;
        final Long totalItems1 = 1L;
        final PageResult<QualityNotification> pagedNotification = new PageResult<>(List.of(notification), page1, pageCount1, pageSize1, totalItems1);

        // when
        final PageResult<AlertResponse> result = AlertResponse.fromAsPageResult(pagedNotification);

        // then
        assertThat(result)
                .hasFieldOrPropertyWithValue("page", 1)
                .hasFieldOrPropertyWithValue("pageCount", 2)
                .hasFieldOrPropertyWithValue("pageSize", 1)
                .hasFieldOrPropertyWithValue("totalItems", 2L);
        assertThat(result.content()).hasSize(1)
                .first()
                .usingRecursiveComparison()
                .isEqualTo(AlertResponse.from(notification));
    }

}
