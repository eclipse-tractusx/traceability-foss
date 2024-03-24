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

package org.eclipse.tractusx.traceability.qualitynotification.application.notification.rest;

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.notification.mapper.QualityNotificationFieldMapper;
import org.eclipse.tractusx.traceability.qualitynotification.application.service.QualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.application.notification.mapper.NotificationResponseMapper;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import qualitynotification.base.request.CloseQualityNotificationRequest;
import qualitynotification.base.request.QualityNotificationStatusRequest;
import qualitynotification.base.request.StartQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationRequest;
import qualitynotification.base.response.QualityNotificationIdResponse;
import qualitynotification.base.response.QualityNotificationResponse;

import java.util.List;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;
import static org.eclipse.tractusx.traceability.qualitynotification.application.validation.UpdateQualityNotificationValidator.validate;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.StartQualityNotification.from;

@RestController
@RequestMapping(value = "/notifications", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Notifications")
@Validated
@Slf4j
public class NotificationController {

    private final QualityNotificationService notificationService;

    private final BaseRequestFieldMapper fieldMapper;

    public NotificationController(
            @Qualifier("notificationServiceImpl") QualityNotificationService notificationService,
            QualityNotificationFieldMapper fieldMapper) {
        this.notificationService = notificationService;
        this.fieldMapper = fieldMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public QualityNotificationIdResponse alertAssets(@RequestBody @Valid StartQualityNotificationRequest request) {
        StartQualityNotificationRequest cleanStartQualityNotificationRequest = sanitize(request);
        log.info("Received API call on /notifications" + " with params: {}", cleanStartQualityNotificationRequest);
        return new QualityNotificationIdResponse(notificationService.start(from(cleanStartQualityNotificationRequest)).value());
    }

    @PostMapping("/filter")
    public PageResult<QualityNotificationResponse> getAlerts(@Valid @RequestBody PageableFilterRequest pageableFilterRequest) {
        log.info("Received API call on /notifications/filter");
        return NotificationResponseMapper.fromAsPageResult(
                notificationService.getNotifications(
                        OwnPageable.toPageable(pageableFilterRequest.getOwnPageable(), fieldMapper),
                        pageableFilterRequest.getSearchCriteriaRequestParam().toSearchCriteria(fieldMapper)));
    }

    @GetMapping("/{notificationId}")
    public QualityNotificationResponse getAlert(@PathVariable("notificationId") Long notificationId) {
        log.info("Received API call on /notifications/" + "/{}", notificationId);
        return NotificationResponseMapper.from(notificationService.find(notificationId));
    }

    @PostMapping("/{notificationId}/approve")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveAlert(@PathVariable("notificationId") Long notificationId) {
        log.info("Received API call on /notifications/" + "/{}/approve", notificationId);
        notificationService.approve(notificationId);
    }

    @PostMapping("/{notificationId}/cancel")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAlert(@PathVariable("notificationId") Long notificationId) {
        log.info("Received API call on /notifications/" + "/{}/cancel", notificationId);
        notificationService.cancel(notificationId);
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR')")
    @PostMapping("/{notificationId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAlert(
            @PathVariable("notificationId") @ApiParam Long notificationId,
            @Valid @RequestBody CloseQualityNotificationRequest closeAlertRequest) {
        CloseQualityNotificationRequest cleanCloseAlertRequest = sanitize(closeAlertRequest);
        log.info("Received API call on /notifications/" + "/{}/close with params {}", notificationId, cleanCloseAlertRequest);
        notificationService.update(notificationId, QualityNotificationStatus.from(QualityNotificationStatusRequest.CLOSED), cleanCloseAlertRequest.getReason());
    }

    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_USER')")
    @PostMapping("/{notificationId}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAlert(
            @PathVariable("notificationId") Long notificationId,
            @Valid @RequestBody UpdateQualityNotificationRequest updateAlertRequest) {
        UpdateQualityNotificationRequest cleanUpdateAlertRequest = sanitize(updateAlertRequest);
        validate(cleanUpdateAlertRequest);
        log.info("Received API call on /notifications/" + "/{}/update with params {}", notificationId, cleanUpdateAlertRequest);
        notificationService.update(notificationId, QualityNotificationStatus.from(cleanUpdateAlertRequest.getStatus()), cleanUpdateAlertRequest.getReason());
    }

    @GetMapping("distinctFilterValues")
    public List<String> distinctFilterValues(@QueryParam("fieldName") String fieldName, @QueryParam("size") Integer size, @QueryParam("startWith") String startWith, @QueryParam("channel") QualityNotificationSide channel) {
        return notificationService.getDistinctFilterValues(fieldMapper.mapRequestFieldName(fieldName), startWith, size, channel);
    }
}
