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

package org.eclipse.tractusx.traceability.notification.application.alert.rest;

import org.eclipse.tractusx.traceability.notification.application.notification.service.NotificationService;
import org.eclipse.tractusx.traceability.notification.application.notification.rest.NotificationController;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationId;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import notification.request.CloseNotificationRequest;
import notification.request.NotificationSeverityRequest;
import notification.request.NotificationTypeRequest;
import notification.request.StartNotificationRequest;
import notification.request.UpdateNotificationStatusTransitionRequest;
import notification.request.UpdateNotificationStatusRequest;
import notification.response.NotificationIdResponse;

import notification.response.NotificationResponse;
import notification.response.NotificationSeverityResponse;
import notification.response.NotificationSideResponse;
import notification.response.NotificationStatusResponse;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.notification.domain.notification.model.StartNotification.from;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;


    @InjectMocks
    private NotificationController controller;

    @Test
    void givenRequestBody_whenAlertAssets_thenResponse() {
        // given
        final List<String> partIds = List.of("partId1", "partId2");
        final Instant targetDate = Instant.parse("2099-03-11T22:44:06.333826952Z");
        final NotificationId notificationId = new NotificationId(666L);
        final StartNotificationRequest request = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description("description")
                .targetDate(targetDate)
                .type(NotificationTypeRequest.ALERT)
                .severity(NotificationSeverityRequest.MINOR)
                .receiverBpn("BPN00001")
                .build();
        when(notificationService.start(Mockito.eq(from(request)))).thenReturn(notificationId);

        // when
        final NotificationIdResponse result = controller.createNotification(request);

        // then
        assertThat(result).hasFieldOrPropertyWithValue("id", notificationId.value());
    }

    @Test
    void givenRequest_whenGetAlert_thenProperResponse() {
        // given
        final Long request = 69L;
        final Notification notification = InvestigationTestDataFactory.createInvestigationTestData(
                NotificationStatus.ACCEPTED,
                "bpn"
        );
        when(notificationService.find(request)).thenReturn(notification);

        // when
        final NotificationResponse result = controller.getNotificationById(request);

        // then
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", notification.getNotificationId().value())
                .hasFieldOrPropertyWithValue("status", NotificationStatusResponse.ACCEPTED)
                .hasFieldOrPropertyWithValue("description", notification.getDescription())
                .hasFieldOrPropertyWithValue("createdBy", notification.getNotifications().stream()
                        .findFirst()
                        .map(NotificationMessage::getSentBy)
                        .orElse(null))
                .hasFieldOrPropertyWithValue("createdByName", notification.getNotifications().stream()
                        .findFirst()
                        .map(NotificationMessage::getSentByName)
                        .orElse(null))
                .hasFieldOrPropertyWithValue("createdDate", notification.getCreatedAt().toString())
                .hasFieldOrPropertyWithValue("assetIds", notification.getAffectedPartIds())
                .hasFieldOrPropertyWithValue("channel", NotificationSideResponse.SENDER)
                .hasFieldOrPropertyWithValue("sendTo", "recipientBPN")
                .hasFieldOrPropertyWithValue("sendToName", "receiverManufacturerName")
                .hasFieldOrPropertyWithValue("severity", NotificationSeverityResponse.MINOR);
    }

    @Test
    void givenRequest_whenApproveAlert_thenProcessCorrectly() {
        // given
        final Long request = 1L;

        // when
        controller.approveNotificationById(request);

        // then
        verify(notificationService, times(1)).approve(request);
    }

    @Test
    void givenRequest_whenCancelAlert_thenProcessCorrectly() {
        // given
        final Long request = 1L;

        // when
        controller.cancelNotificationById(request);

        // then
        verify(notificationService, times(1)).cancel(request);
    }

    @Test
    void givenRequest_whenCloseAlert_thenProcessCorrectly() {
        // given
        final Long param = 1L;

        CloseNotificationRequest request = CloseNotificationRequest.builder().reason("just because").build();

        // when
        controller.closeNotificationById(param, request);

        // then
        verify(notificationService, times(1)).updateStatusTransition(param, NotificationStatus.CLOSED, "just because");
    }

    @Test
    void givenRequest_whenUpdateAlert_thenProcessCorrectly() {
        // given
        final Long param = 1L;


        UpdateNotificationStatusTransitionRequest request =
                UpdateNotificationStatusTransitionRequest.builder()
                        .status(UpdateNotificationStatusRequest.ACCEPTED)
                        .reason("just because I say so")
                        .build();


        // when
        controller.updateNotificationStatusById(param, request);

        // then
        verify(notificationService, times(1)).updateStatusTransition(param, NotificationStatus.ACCEPTED, "just because I say so");
    }

}
