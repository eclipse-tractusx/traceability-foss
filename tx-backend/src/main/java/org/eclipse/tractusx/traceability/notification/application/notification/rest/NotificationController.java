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

package org.eclipse.tractusx.traceability.notification.application.notification.rest;

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
import lombok.extern.slf4j.Slf4j;
import notification.request.CloseNotificationRequest;
import notification.request.EditNotificationRequest;
import notification.request.NotificationRequest;
import notification.request.NotificationStatusRequest;
import notification.request.SearchableNotificationsRequest;
import notification.request.StartNotificationRequest;
import notification.request.UpdateNotificationStatusTransitionRequest;
import notification.response.NotificationIdResponse;
import notification.response.NotificationResponse;
import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaMapper;
import org.eclipse.tractusx.traceability.notification.application.notification.mapper.NotificationFieldMapper;
import org.eclipse.tractusx.traceability.notification.application.notification.mapper.NotificationResponseMapper;
import org.eclipse.tractusx.traceability.notification.application.notification.service.NotificationService;
import org.eclipse.tractusx.traceability.notification.base.mapper.NotificationSideTypeMapper;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.notification.model.EditNotification;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;
import static org.eclipse.tractusx.traceability.notification.application.notification.validation.UpdateNotificationValidator.validate;
import static org.eclipse.tractusx.traceability.notification.domain.notification.model.StartNotification.from;

@RestController
@RequestMapping(value = "/notifications", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Notifications")
@Validated
@Slf4j
public class NotificationController {

    private static final String RECEIVED_API_CALL_LOG = "Received API call on /notifications";
    private final NotificationService notificationService;

    private final BaseRequestFieldMapper fieldMapper;

    public NotificationController(
            @Qualifier("notificationServiceImpl") NotificationService notificationService,
            NotificationFieldMapper fieldMapper) {
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
    public NotificationIdResponse createNotification(@RequestBody @Valid StartNotificationRequest request) {
        StartNotificationRequest cleanStartNotificationRequest = sanitize(request);
        log.info(RECEIVED_API_CALL_LOG + " with params: {}", cleanStartNotificationRequest);
        return new NotificationIdResponse(notificationService.start(from(cleanStartNotificationRequest)).value());
    }

    @Operation(operationId = "filterNotifications",
            summary = "Filter notifications defined by the request body",
            tags = {"Notifications"},
            description = "The endpoint returns notifications as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Notifications", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "NotificationData", implementation = NotificationResponse.class, additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE),
            schema = @Schema(implementation = NotificationResponse.class)
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
    public PageResult<NotificationResponse> getNotifications(@Valid @RequestBody NotificationRequest notificationRequest) {
        log.info(RECEIVED_API_CALL_LOG + "/filter");

        return NotificationResponseMapper.fromAsPageResult(
                notificationService.getNotifications(
                        OwnPageable.toPageable(OwnPageable.builder()
                                .page(notificationRequest.getPage())
                                .size(notificationRequest.getSize())
                                .sort(notificationRequest.getSort())
                                .build(), fieldMapper),
                        SearchCriteriaMapper.
                                toSearchCriteria(
                                        fieldMapper,
                                        List.of(notificationRequest.getNotificationFilter())
                                )
                )
        );
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
    public NotificationResponse getNotificationById(@PathVariable("notificationId") Long notificationId) {
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
    public void approveNotificationById(@PathVariable("notificationId") Long notificationId) {
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
    public void cancelNotificationById(@PathVariable("notificationId") Long notificationId) {
        log.info(RECEIVED_API_CALL_LOG + "/{}/cancel", notificationId);
        notificationService.cancel(notificationId);
    }

    @Operation(operationId = "closeNotification",
            summary = "Close notification by id",
            tags = {"Notifications"},
            description = "The endpoint closes Notification by id.",
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
    public void closeNotificationById(
            @PathVariable("notificationId") @ApiParam Long notificationId,
            @Valid @RequestBody CloseNotificationRequest closeNotificationRequest) {
        CloseNotificationRequest cleanCloseNotificationRequest = sanitize(closeNotificationRequest);
        log.info(RECEIVED_API_CALL_LOG + "/{}/close with params {}", notificationId, cleanCloseNotificationRequest);
        notificationService.updateStatusTransition(notificationId, NotificationStatus.from(NotificationStatusRequest.CLOSED), cleanCloseNotificationRequest.getReason());
    }

    @Operation(operationId = "updateNotificationStatus",
            summary = "Update notification status by id",
            tags = {"Notifications"},
            description = "The endpoint updates the notification status by their id.",
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
    public void updateNotificationStatusById(
            @PathVariable("notificationId") Long notificationId,
            @Valid @RequestBody UpdateNotificationStatusTransitionRequest updateNotificationStatusTransitionRequest) {
        UpdateNotificationStatusTransitionRequest cleanUpdateNotificationStatusTransitionRequest = sanitize(updateNotificationStatusTransitionRequest);
        validate(cleanUpdateNotificationStatusTransitionRequest);
        log.info(RECEIVED_API_CALL_LOG + "/{}/update with params {}", notificationId, cleanUpdateNotificationStatusTransitionRequest);
        notificationService.updateStatusTransition(notificationId, NotificationStatus.from(cleanUpdateNotificationStatusTransitionRequest.getStatus()), cleanUpdateNotificationStatusTransitionRequest.getReason());
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
    @PutMapping("/{notificationId}/edit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editNotification(
            @PathVariable("notificationId") Long notificationId,
            @Valid @RequestBody EditNotificationRequest editNotificationRequest) {
        EditNotificationRequest cleanEditNotificationRequest = sanitize(editNotificationRequest);
        log.info(RECEIVED_API_CALL_LOG + "/{}/edit with params {}", notificationId, cleanEditNotificationRequest);
        notificationService.editNotification(EditNotification.from(cleanEditNotificationRequest, notificationId));
    }

    @Operation(operationId = "searchable-values",
            summary = "searchable-values",
            tags = {"Notifications"},
            description = "The endpoint returns searchable values for given fieldName.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns searchable values for given fieldName.", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(
                    schema = @Schema(
                            description = "SearchableValues",
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
    @PostMapping("searchable-values")
    public List<String> searchableValues(@Valid @RequestBody SearchableNotificationsRequest request) {
        return notificationService.getSearchableValues(fieldMapper.mapRequestFieldName(request.fieldName()),
                request.startsWith(), request.size(), NotificationSideTypeMapper.from(request.channel()));
    }
}
