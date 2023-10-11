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

package org.eclipse.tractusx.traceability.qualitynotification.application.alert.rest;

import org.eclipse.tractusx.traceability.qualitynotification.application.base.service.QualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import qualitynotification.alert.response.AlertResponse;
import qualitynotification.base.request.CloseQualityNotificationRequest;
import qualitynotification.base.request.QualityNotificationSeverityRequest;
import qualitynotification.base.request.StartQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationStatusRequest;
import qualitynotification.base.response.QualityNotificationIdResponse;
import qualitynotification.base.response.QualityNotificationReasonResponse;
import qualitynotification.base.response.QualityNotificationSeverityResponse;
import qualitynotification.base.response.QualityNotificationSideResponse;
import qualitynotification.base.response.QualityNotificationStatusResponse;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.exception.StartQualityNotificationDomain.from;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @Mock
    private QualityNotificationService alertService;


    @InjectMocks
    private AlertController controller;

    @Test
    void givenRequestBody_whenAlertAssets_thenResponse() {
        // given
        final List<String> partIds = List.of("partId1", "partId2");
        final Instant targetDate = Instant.parse("2099-03-11T22:44:06.333826952Z");
        final QualityNotificationId notificationId = new QualityNotificationId(666L);
        final StartQualityNotificationRequest request = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description("description")
                .targetDate(targetDate)
                .severity(QualityNotificationSeverityRequest.MINOR)
                .receiverBpn("BPN00001")
                .build();
        when(alertService.start(Mockito.eq(from(request)))).thenReturn(notificationId);

        // when
        final QualityNotificationIdResponse result = controller.alertAssets(request);

        // then
        assertThat(result).hasFieldOrPropertyWithValue("id", notificationId.value());
    }

    @Test
    void givenRequest_whenGetAlert_thenProperResponse() {
        // given
        final Long request = 69L;
        final QualityNotification notification = InvestigationTestDataFactory.createInvestigationTestData(
                QualityNotificationStatus.ACCEPTED,
                "bpn"
        );
        when(alertService.find(request)).thenReturn(notification);

        // when
        final AlertResponse result = controller.getAlert(request);

        // then
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", notification.getNotificationId().value())
                .hasFieldOrPropertyWithValue("status", QualityNotificationStatusResponse.ACCEPTED)
                .hasFieldOrPropertyWithValue("description", notification.getDescription())
                .hasFieldOrPropertyWithValue("createdBy", notification.getNotifications().stream()
                        .findFirst()
                        .map(QualityNotificationMessage::getCreatedBy)
                        .orElse(null))
                .hasFieldOrPropertyWithValue("createdByName", notification.getNotifications().stream()
                        .findFirst()
                        .map(QualityNotificationMessage::getCreatedByName)
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
                .hasFieldOrPropertyWithValue("severity", QualityNotificationSeverityResponse.MINOR);
    }

    @Test
    void givenRequest_whenApproveAlert_thenProcessCorrectly() {
        // given
        final Long request = 1L;

        // when
        controller.approveAlert(request);

        // then
        verify(alertService, times(1)).approve(request);
    }

    @Test
    void givenRequest_whenCancelAlert_thenProcessCorrectly() {
        // given
        final Long request = 1L;

        // when
        controller.cancelAlert(request);

        // then
        verify(alertService, times(1)).cancel(request);
    }

    @Test
    void givenRequest_whenCloseAlert_thenProcessCorrectly() {
        // given
        final Long param = 1L;
        final CloseQualityNotificationRequest request = new CloseQualityNotificationRequest();
        request.setReason("just because");

        // when
        controller.closeAlert(param, request);

        // then
        verify(alertService, times(1)).update(param, QualityNotificationStatus.CLOSED, "just because");
    }

    @Test
    void givenRequest_whenUpdateAlert_thenProcessCorrectly() {
        // given
        final Long param = 1L;
        final UpdateQualityNotificationRequest request = new UpdateQualityNotificationRequest();
        request.setReason("just because I say so");
        request.setStatus(UpdateQualityNotificationStatusRequest.ACCEPTED);

        // when
        controller.updateAlert(param, request);

        // then
        verify(alertService, times(1)).update(param, QualityNotificationStatus.ACCEPTED, "just because I say so");
    }

}
