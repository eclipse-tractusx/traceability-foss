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

import assets.importpoc.ErrorResponse;
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
import jakarta.ws.rs.QueryParam;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.alert.mapper.AlertResponseMapper;
import org.eclipse.tractusx.traceability.qualitynotification.application.base.mapper.QualityNotificationFieldMapper;
import org.eclipse.tractusx.traceability.qualitynotification.application.base.service.QualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
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
import qualitynotification.alert.response.AlertResponse;
import qualitynotification.base.request.CloseQualityNotificationRequest;
import qualitynotification.base.request.QualityNotificationStatusRequest;
import qualitynotification.base.request.StartQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationRequest;
import qualitynotification.base.response.QualityNotificationIdResponse;

import java.util.List;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;
import static org.eclipse.tractusx.traceability.qualitynotification.application.validation.UpdateQualityNotificationValidator.validate;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.StartQualityNotification.from;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus.from;


@RestController
@RequestMapping(value = "/alerts", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Alerts")
@Validated
@Slf4j
public class AlertController {

    private final QualityNotificationService alertService;

    // TODO move to QualityNotificationService
    private final BaseRequestFieldMapper fieldMapper;

    public AlertController(
            @Qualifier("notificationServiceImpl") QualityNotificationService alertService,
            QualityNotificationFieldMapper fieldMapper) {
        this.alertService = alertService;
        this.fieldMapper = fieldMapper;
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
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public QualityNotificationIdResponse alertAssets(@RequestBody @Valid StartQualityNotificationRequest request) {
        StartQualityNotificationRequest cleanStartQualityNotificationRequest = sanitize(request);
        log.info(API_LOG_START + " with params: {}", cleanStartQualityNotificationRequest);
        return new QualityNotificationIdResponse(alertService.start(from(cleanStartQualityNotificationRequest)).value());
    }

    @Operation(operationId = "filterAlerts",
            summary = "Filter alerts defined by the request body",
            tags = {"Alerts"},
            description = "The endpoint returns alerts as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "AlertData", implementation = AlertResponse.class, additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE),
            schema = @Schema(implementation = AlertResponse.class)
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
    @PostMapping("/filter")
    public PageResult<AlertResponse> getAlerts(@Valid @RequestBody PageableFilterRequest pageableFilterRequest) {
        log.info(API_LOG_START);
        return AlertResponseMapper.fromAsPageResult(
                alertService.getNotifications(
                        OwnPageable.toPageable(pageableFilterRequest.getOwnPageable(), fieldMapper),
                        pageableFilterRequest.getSearchCriteriaRequestParam().toSearchCriteria(fieldMapper)));
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
    public AlertResponse getAlert(@PathVariable("alertId") Long alertId) {
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
                    responseCode = "200",
                    description = "Ok."),
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
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveAlert(@PathVariable("alertId") Long alertId) {
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
                    responseCode = "200",
                    description = "Ok."),
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
    @PostMapping("/{alertId}/cancel")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAlert(@PathVariable("alertId") Long alertId) {
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
                    responseCode = "200",
                    description = "Ok."),
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
    @PostMapping("/{alertId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAlert(
            @PathVariable("alertId") @ApiParam Long alertId,
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
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_USER')")
    @PostMapping("/{alertId}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAlert(
            @PathVariable("alertId") Long alertId,
            @Valid @RequestBody UpdateQualityNotificationRequest updateAlertRequest) {
        UpdateQualityNotificationRequest cleanUpdateAlertRequest = sanitize(updateAlertRequest);
        validate(cleanUpdateAlertRequest);
        log.info(API_LOG_START + "/{}/update with params {}", alertId, cleanUpdateAlertRequest);
        alertService.update(alertId, from(cleanUpdateAlertRequest.getStatus()), cleanUpdateAlertRequest.getReason());
    }

    @Operation(operationId = "distinctFilterValues",
            summary = "getDistinctFilterValues",
            tags = {"Assets"},
            description = "The endpoint returns a distinct filter values for given fieldName.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns a distinct filter values for given fieldName.", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(
                    schema = @Schema(
                            description = "FilterValues",
                            implementation = String.class,
                            additionalProperties = Schema.AdditionalPropertiesValue.FALSE
                    ),
                    maxItems = Integer.MAX_VALUE,
                    minItems = 0)
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
    @GetMapping("distinctFilterValues")
    public List<String> distinctFilterValues(@QueryParam("fieldName") String fieldName, @QueryParam("size") Integer size, @QueryParam("startWith") String startWith, @QueryParam("channel") QualityNotificationSide channel) {
        return alertService.getDistinctFilterValues(fieldMapper.mapRequestFieldName(fieldName), startWith, size, channel);
    }
}

