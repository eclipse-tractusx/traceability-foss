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

package org.eclipse.tractusx.traceability.notification.application.alert.response;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.notification.application.notification.mapper.NotificationResponseMapper;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.junit.jupiter.api.Test;
import notification.response.NotificationResponse;
import notification.response.NotificationSeverityResponse;
import notification.response.NotificationSideResponse;
import notification.response.NotificationStatusResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlertResponseTest {

    @Test
    void givenNotification_whenFrom_thenConstructProperAlertResponse() {
        // given
        final Notification notification = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.ACCEPTED, NotificationStatus.CREATED);

        // when
        final NotificationResponse result = NotificationResponseMapper.from(notification);

        // then
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", notification.getNotificationId().value())
                .hasFieldOrPropertyWithValue("status", NotificationStatusResponse.ACCEPTED)
                .hasFieldOrPropertyWithValue("description", notification.getDescription())
                .hasFieldOrPropertyWithValue("createdBy", notification.getBpn())
                .hasFieldOrPropertyWithValue("createdByName", notification.getNotifications().stream()
                        .findFirst()
                        .map(NotificationMessage::getSentByName)
                        .orElse(null))
                .hasFieldOrPropertyWithValue("createdDate", notification.getCreatedAt().toString())
                .hasFieldOrPropertyWithValue("assetIds", notification.getAffectedPartIds())
                .hasFieldOrPropertyWithValue("channel", NotificationSideResponse.SENDER)
                .hasFieldOrPropertyWithValue("sendTo", "TESTBPN")
                .hasFieldOrPropertyWithValue("sendToName", "receiverManufacturerName")
                .hasFieldOrPropertyWithValue("severity", NotificationSeverityResponse.MINOR)
                .hasFieldOrPropertyWithValue("targetDate", null);
    }

    @Test
    void givenNotificationPaged_whenFromAsPageResult_thenConstructProperAlertResponse() {
        // given
        final Notification notification = InvestigationTestDataFactory.createInvestigationTestData(NotificationStatus.ACCEPTED, NotificationStatus.CREATED);
        final Integer page1 = 1;
        final Integer pageCount1 = 1;
        final Integer pageSize1 = 1;
        final Long totalItems1 = 1L;
        final PageResult<Notification> pagedNotification = new PageResult<>(List.of(notification), page1, pageCount1, pageSize1, totalItems1);

        // when
        final PageResult<NotificationResponse> result = NotificationResponseMapper.fromAsPageResult(pagedNotification);

        // then
        assertThat(result)
                .hasFieldOrPropertyWithValue("page", 1)
                .hasFieldOrPropertyWithValue("pageCount", 2)
                .hasFieldOrPropertyWithValue("pageSize", 1)
                .hasFieldOrPropertyWithValue("totalItems", 2L);
        assertThat(result.content()).hasSize(1)
                .first()
                .usingRecursiveComparison()
                .ignoringFields("updatedDate")
                .isEqualTo(NotificationResponseMapper.from(notification));
    }

}
