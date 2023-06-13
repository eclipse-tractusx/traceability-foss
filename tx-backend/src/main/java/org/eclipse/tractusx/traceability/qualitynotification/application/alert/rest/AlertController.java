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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.config.FeatureFlags;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.application.alert.response.AlertResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.alert.service.AlertService;
import org.eclipse.tractusx.traceability.qualitynotification.application.request.CloseQualityNotificationRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.request.QualityNotificationStatusRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.request.StartQualityNotificationRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.request.UpdateQualityNotificationRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.response.QualityNotificationIdResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
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

import static org.eclipse.tractusx.traceability.qualitynotification.application.validation.UpdateQualityNotificationValidator.validate;

@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
@RestController
@RequestMapping(value = "/alerts", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Alerts")
@Validated
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AlertService alertService;

    private static final String API_LOG_START = "Received API call on /alerts";

    @Operation(operationId = "alertAssets",
            summary = "Start alert by part ids",
            tags = {"Alerts"},
            description = "The endpoint starts alert based on part ids provided.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QualityNotificationIdResponse alertAssets(@RequestBody @Valid StartQualityNotificationRequest request) {
        log.info(API_LOG_START + " with params: {}", request);
        return new QualityNotificationIdResponse(alertService.start(
                request.getPartIds(), request.getDescription(), request.getTargetDate(), request.getSeverity().toDomain()).value());
    }

    @Operation(operationId = "getCreatedAlerts",
            summary = "Gets created alerts",
            tags = {"Alerts"},
            description = "The endpoint returns created alerts as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "AlertData", implementation = AlertResponse.class, additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
    )),
            @ApiResponse(responseCode = "401", description = "Authorization failed.", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden.", content = @Content())})
    @GetMapping("/created")
    public PageResult<AlertResponse> getCreatedAlerts(Pageable pageable) {
        log.info(API_LOG_START + "/created");
        return AlertResponse.fromAsPageResult(alertService.getCreated(pageable));
    }

    @Operation(operationId = "getReceivedAlerts",
            summary = "Gets received alerts",
            tags = {"Alerts"},
            description = "The endpoint returns received alerts as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "AlertData", implementation = AlertResponse.class, additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
    )),
            @ApiResponse(responseCode = "401", description = "Authorization failed.", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden.", content = @Content())})
    @GetMapping("/received")
    public PageResult<AlertResponse> getReceivedAlerts(Pageable pageable) {
        log.info(API_LOG_START + "/received");
        return AlertResponse.fromAsPageResult(alertService.getReceived(pageable));
    }

    @Operation(operationId = "getAlert",
            summary = "Gets Alert by id",
            tags = {"Alerts"},
            description = "The endpoint returns alert by id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("/{alertId}")
    public AlertResponse getAlert(@PathVariable Long alertId) {
        log.info(API_LOG_START + "/{}", alertId);
        return AlertResponse.from(alertService.find(alertId));
    }

    @Operation(operationId = "approveAlert",
            summary = "Approves alert by id",
            tags = {"Alerts"},
            description = "The endpoint approves alert by id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PostMapping("/{alertId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveAlert(@PathVariable Long alertId) {
        log.info(API_LOG_START + "/{}/approve", alertId);
        alertService.approve(alertId);
    }

    @Operation(operationId = "cancelAlert",
            summary = "Cancels alert by id",
            tags = {"Alerts"},
            description = "The endpoint cancels alert by id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PostMapping("/{alertId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAlert(@PathVariable Long alertId) {
        log.info(API_LOG_START + "/{}/cancel", alertId);
        alertService.cancel(alertId);
    }

    @Operation(operationId = "closeAlert",
            summary = "Close alert by id",
            tags = {"Alerts"},
            description = "The endpoint closes alert by id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    @PostMapping("/{alertId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAlert(
            @PathVariable Long alertId,
            @Valid @RequestBody CloseQualityNotificationRequest closeAlertRequest) {
        log.info(API_LOG_START + "/{}/close with params {}", alertId, closeAlertRequest);
        alertService.update(alertId, QualityNotificationStatusRequest.toDomain(QualityNotificationStatusRequest.CLOSED), closeAlertRequest.getReason());
    }

    @Operation(operationId = "updateAlert",
            summary = "Update alert by id",
            tags = {"Alerts"},
            description = "The endpoint updates alert by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR')")
    @PostMapping("/{alertId}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAlert(
            @PathVariable Long alertId,
            @Valid @RequestBody UpdateQualityNotificationRequest updateAlertRequest) {
        validate(updateAlertRequest);
        log.info(API_LOG_START + "/{}/update with params {}", alertId, updateAlertRequest);
        alertService.update(alertId, updateAlertRequest.getStatus().toDomain(), updateAlertRequest.getReason());
    }
}

