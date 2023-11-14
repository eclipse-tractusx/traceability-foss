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

import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.common.response.ErrorResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.alert.mapper.AlertResponseMapper;
import org.eclipse.tractusx.traceability.qualitynotification.application.base.service.QualityNotificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import qualitynotification.alert.response.AlertResponse;
import qualitynotification.base.request.CloseQualityNotificationRequest;
import qualitynotification.base.request.QualityNotificationStatusRequest;
import qualitynotification.base.request.StartQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationRequest;
import qualitynotification.base.response.QualityNotificationIdResponse;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;
import static org.eclipse.tractusx.traceability.qualitynotification.application.validation.UpdateQualityNotificationValidator.validate;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.exception.StartQualityNotificationDomain.from;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus.from;


@RestController
@RequestMapping(value = "/alerts", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Alerts")
@Validated
@Slf4j
public class AlertController {

    private final QualityNotificationService alertService;

    public AlertController(@Qualifier("alertServiceImpl") QualityNotificationService alertService) {
        this.alertService = alertService;
    }

    private static final String API_LOG_START = "Received API call on /alerts";

    @Operation(operationId = "alertAssets",
            summary = "Start alert by part ids",
            tags = {"Alerts"},
            description = "The endpoint starts alert based on part ids provided.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QualityNotificationIdResponse alertAssets(@RequestBody @Valid StartQualityNotificationRequest request) {
        StartQualityNotificationRequest cleanStartQualityNotificationRequest = sanitize(request);
        log.info(API_LOG_START + " with params: {}", cleanStartQualityNotificationRequest);
        return new QualityNotificationIdResponse(alertService.start(from(cleanStartQualityNotificationRequest)).value());
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
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/created")
    public PageResult<AlertResponse> getCreatedAlerts(OwnPageable pageable, SearchCriteriaRequestParam searchCriteriaRequestParam) {
        log.info(API_LOG_START + "/created");
        return AlertResponseMapper.fromAsPageResult(alertService.getCreated(OwnPageable.toPageable(pageable), searchCriteriaRequestParam.toSearchCriteria()));
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
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/received")
    public PageResult<AlertResponse> getReceivedAlerts(OwnPageable pageable, SearchCriteriaRequestParam searchCriteriaRequestParam) {
        log.info(API_LOG_START + "/received");
        return AlertResponseMapper.fromAsPageResult(alertService.getReceived(OwnPageable.toPageable(pageable), searchCriteriaRequestParam.toSearchCriteria()));
    }

    @Operation(operationId = "getAlert",
            summary = "Gets Alert by id",
            tags = {"Alerts"},
            description = "The endpoint returns alert by id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/{alertId}")
    public AlertResponse getAlert(@PathVariable Long alertId) {
        log.info(API_LOG_START + "/{}", alertId);
        return AlertResponseMapper.from(alertService.find(alertId));
    }

    @Operation(operationId = "approveAlert",
            summary = "Approves alert by id",
            tags = {"Alerts"},
            description = "The endpoint approves alert by id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No content."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No content.",
                    content = @Content()),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No content.",
                    content = @Content()),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    @PostMapping("/{alertId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAlert(
            @PathVariable @ApiParam Long alertId,
            @Valid @RequestBody CloseQualityNotificationRequest closeAlertRequest) {
        CloseQualityNotificationRequest cleanCloseAlertRequest = sanitize(closeAlertRequest);
        log.info(API_LOG_START + "/{}/close with params {}", alertId, cleanCloseAlertRequest);
        alertService.update(alertId, from(QualityNotificationStatusRequest.CLOSED), cleanCloseAlertRequest.getReason());
    }

    @Operation(operationId = "updateAlert",
            summary = "Update alert by id",
            tags = {"Alerts"},
            description = "The endpoint updates alert by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No content.",
                    content = @Content()),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR')")
    @PostMapping("/{alertId}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAlert(
            @PathVariable Long alertId,
            @Valid @RequestBody UpdateQualityNotificationRequest updateAlertRequest) {
        UpdateQualityNotificationRequest cleanUpdateAlertRequest = sanitize(updateAlertRequest);
        validate(cleanUpdateAlertRequest);
        log.info(API_LOG_START + "/{}/update with params {}", alertId, cleanUpdateAlertRequest);
        alertService.update(alertId, from(cleanUpdateAlertRequest.getStatus()), cleanUpdateAlertRequest.getReason());
    }
}
