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
import org.eclipse.tractusx.traceability.qualitynotification.application.notification.mapper.NotificationResponseMapper;
import org.eclipse.tractusx.traceability.qualitynotification.application.notification.mapper.QualityNotificationFieldMapper;
import org.eclipse.tractusx.traceability.qualitynotification.application.notification.service.QualityNotificationService;
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
import static org.eclipse.tractusx.traceability.qualitynotification.application.notification.validation.UpdateQualityNotificationValidator.validate;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.notification.model.StartQualityNotification.from;

@RestController
@RequestMapping(value = "/notifications", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Notifications")
@Validated
@Slf4j
public class NotificationController {

    private static final String RECEIVED_API_CALL_LOG = "Received API call on /notifications";
    private final QualityNotificationService notificationService;

    private final BaseRequestFieldMapper fieldMapper;

    public NotificationController(
            @Qualifier("notificationServiceImpl") QualityNotificationService notificationService,
            QualityNotificationFieldMapper fieldMapper) {
        this.notificationService = notificationService;
        this.fieldMapper = fieldMapper;
    }

    @Operation(operationId = "notifyAssets",
            summary = "Start notification by part ids",
            tags = {"Notifications"},
            description = "The endpoint starts notification based on part ids provided.",
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
        log.info(RECEIVED_API_CALL_LOG + " with params: {}", cleanStartQualityNotificationRequest);
        return new QualityNotificationIdResponse(notificationService.start(from(cleanStartQualityNotificationRequest)).value());
    }

    @Operation(operationId = "filterNotifications",
            summary = "Filter notifications defined by the request body",
            tags = {"Notifications"},
            description = "The endpoint returns notifications as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Notifications", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "AlertData", implementation = QualityNotificationResponse.class, additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE),
            schema = @Schema(implementation = QualityNotificationResponse.class)
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
    public PageResult<QualityNotificationResponse> getAlerts(@Valid @RequestBody PageableFilterRequest pageableFilterRequest) {
        log.info(RECEIVED_API_CALL_LOG + "/filter");

        return NotificationResponseMapper.fromAsPageResult(
                notificationService.getNotifications(
                        OwnPageable.toPageable(pageableFilterRequest.getOwnPageable(), fieldMapper),
                        pageableFilterRequest.getSearchCriteriaRequestParam().toSearchCriteria(fieldMapper)));
    }

    @Operation(operationId = "getNotification",
            summary = "Gets notification by id",
            tags = {"Notifications"},
            description = "The endpoint returns notification by id.",
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
    @GetMapping("/{notificationId}")
    public QualityNotificationResponse getAlert(@PathVariable("notificationId") Long notificationId) {
        log.info(RECEIVED_API_CALL_LOG + "/{}", notificationId);
        return NotificationResponseMapper.from(notificationService.find(notificationId));
    }

    @Operation(operationId = "approveNotification",
            summary = "Approves notification by id",
            tags = {"Notifications"},
            description = "The endpoint approves notification by id.",
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
    @PostMapping("/{notificationId}/approve")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveAlert(@PathVariable("notificationId") Long notificationId) {
        log.info(RECEIVED_API_CALL_LOG + "/{}/approve", notificationId);
        notificationService.approve(notificationId);
    }

    @Operation(operationId = "cancelNotification",
            summary = "Cancels notification by id",
            tags = {"Notifications"},
            description = "The endpoint cancels notification by id.",
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
    @PostMapping("/{notificationId}/cancel")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR', 'ROLE_USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelAlert(@PathVariable("notificationId") Long notificationId) {
        log.info(RECEIVED_API_CALL_LOG + "/{}/cancel", notificationId);
        notificationService.cancel(notificationId);
    }

    @Operation(operationId = "closeNotification",
            summary = "Close notification by id",
            tags = {"Notifications"},
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
    @PostMapping("/{notificationId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAlert(
            @PathVariable("notificationId") @ApiParam Long notificationId,
            @Valid @RequestBody CloseQualityNotificationRequest closeAlertRequest) {
        CloseQualityNotificationRequest cleanCloseAlertRequest = sanitize(closeAlertRequest);
        log.info(RECEIVED_API_CALL_LOG + "/{}/close with params {}", notificationId, cleanCloseAlertRequest);
        notificationService.update(notificationId, QualityNotificationStatus.from(QualityNotificationStatusRequest.CLOSED), cleanCloseAlertRequest.getReason());
    }

    @Operation(operationId = "updateNotification",
            summary = "Update notification by id",
            tags = {"Notifications"},
            description = "The endpoint updates notification by their id.",
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
    @PostMapping("/{notificationId}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAlert(
            @PathVariable("notificationId") Long notificationId,
            @Valid @RequestBody UpdateQualityNotificationRequest updateAlertRequest) {
        UpdateQualityNotificationRequest cleanUpdateAlertRequest = sanitize(updateAlertRequest);
        validate(cleanUpdateAlertRequest);
        log.info(RECEIVED_API_CALL_LOG + "/{}/update with params {}", notificationId, cleanUpdateAlertRequest);
        notificationService.update(notificationId, QualityNotificationStatus.from(cleanUpdateAlertRequest.getStatus()), cleanUpdateAlertRequest.getReason());
    }

    @Operation(operationId = "distinctFilterValues",
            summary = "getDistinctFilterValues",
            tags = {"Notifications"},
            description = "The endpoint returns a distinct filter values for given fieldName of notification.",
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
        return notificationService.getDistinctFilterValues(fieldMapper.mapRequestFieldName(fieldName), startWith, size, channel);
    }
}
