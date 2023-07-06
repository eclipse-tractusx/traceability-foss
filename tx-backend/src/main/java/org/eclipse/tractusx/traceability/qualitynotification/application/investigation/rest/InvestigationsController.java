/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.qualitynotification.application.investigation.rest;


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
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.response.ErrorResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.mapper.InvestigationResponseMapper;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.service.InvestigationService;
import org.eclipse.tractusx.traceability.qualitynotification.application.request.CloseQualityNotificationRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.request.QualityNotificationStatusRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.request.StartQualityNotificationRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.request.UpdateQualityNotificationRequest;
import org.springframework.context.annotation.Profile;
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
import qualitynotification.base.response.QualityNotificationIdResponse;
import qualitynotification.investigation.response.InvestigationResponse;

import static org.eclipse.tractusx.traceability.qualitynotification.application.validation.UpdateQualityNotificationValidator.validate;

@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
@RestController
@RequestMapping(value = "/investigations", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Investigations", description = "Operations on Investigation Notification")
@Validated
@RequiredArgsConstructor
@Slf4j
public class InvestigationsController {

    private final InvestigationService investigationService;

    private static final String API_LOG_START = "Received API call on /investigations";

    @Operation(operationId = "investigateAssets",
            summary = "Start investigations by part ids",
            tags = {"Investigations"},
            description = "The endpoint starts investigations based on part ids provided.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created."),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
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
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QualityNotificationIdResponse investigateAssets(@RequestBody @Valid StartQualityNotificationRequest request) {
        log.info(API_LOG_START + " with params: {}", request);
        return new QualityNotificationIdResponse(investigationService.start(
                request.getPartIds(), request.getDescription(), request.getTargetDate(), request.getSeverity().toDomain()).value());
    }

    @Operation(operationId = "getCreatedInvestigations",
            summary = "Gets created investigations",
            tags = {"Investigations"},
            description = "The endpoint returns created investigations as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "InvestigationData", implementation = InvestigationResponse.class, additionalProperties = Schema.AdditionalPropertiesValue.FALSE), minItems = 0, maxItems = Integer.MAX_VALUE)
    )),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
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
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/created")
    public PageResult<InvestigationResponse> getCreatedInvestigations(OwnPageable pageable) {
        log.info(API_LOG_START + "/created");
        return InvestigationResponseMapper.fromAsPageResult(investigationService.getCreated(OwnPageable.toPageable(pageable)));
    }

    @Operation(operationId = "getReceivedInvestigations",
            summary = "Gets received investigations",
            tags = {"Investigations"},
            description = "The endpoint returns received investigations as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "InvestigationData", implementation = InvestigationResponse.class), minItems = 0, maxItems = Integer.MAX_VALUE)
    )),
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
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/received")
    public PageResult<InvestigationResponse> getReceivedInvestigations(OwnPageable pageable) {
        log.info(API_LOG_START + "/received");
        return InvestigationResponseMapper.fromAsPageResult(investigationService.getReceived(OwnPageable.toPageable(pageable)));
    }

    @Operation(operationId = "getInvestigation",
            summary = "Gets investigations by id",
            tags = {"Investigations"},
            description = "The endpoint returns investigations as paged result by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK."),
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
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/{investigationId}")
    public InvestigationResponse getInvestigation(@PathVariable Long investigationId) {
        log.info(API_LOG_START + "/{}", investigationId);
        return InvestigationResponseMapper.from(investigationService.find(investigationId));
    }

    @Operation(operationId = "approveInvestigation",
            summary = "Approves investigations by id",
            tags = {"Investigations"},
            description = "The endpoint approves investigations by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No content.",
                    content = @Content()),
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
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/{investigationId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveInvestigation(@PathVariable Long investigationId) {
        log.info(API_LOG_START + "/{}/approve", investigationId);
        investigationService.approve(investigationId);
    }

    @Operation(operationId = "cancelInvestigation",
            summary = "Cancles investigations by id",
            tags = {"Investigations"},
            description = "The endpoint cancles investigations by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No content."),
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
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/{investigationId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelInvestigation(@PathVariable Long investigationId) {
        log.info(API_LOG_START + "/{}/cancel", investigationId);
        investigationService.cancel(investigationId);
    }

    @Operation(operationId = "closeInvestigation",
            summary = "Close investigations by id",
            tags = {"Investigations"},
            description = "The endpoint closes investigations by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No content.",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
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
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    @PostMapping("/{investigationId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeInvestigation(@PathVariable Long investigationId, @Valid @RequestBody CloseQualityNotificationRequest closeInvestigationRequest) {
        log.info(API_LOG_START + "/{}/close with params {}", investigationId, closeInvestigationRequest);
        investigationService.update(investigationId, QualityNotificationStatusRequest.toDomain(QualityNotificationStatusRequest.CLOSED), closeInvestigationRequest.getReason());
    }

    @Operation(operationId = "updateInvestigation",
            summary = "Update investigations by id",
            tags = {"Investigations"},
            description = "The endpoint updates investigations by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "No content.",
                    content = @Content()),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
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
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR')")
    @PostMapping("/{investigationId}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateInvestigation(@PathVariable Long investigationId, @Valid @RequestBody UpdateQualityNotificationRequest updateInvestigationRequest) {
        validate(updateInvestigationRequest);
        log.info(API_LOG_START + "/{}/update with params {}", investigationId, updateInvestigationRequest);
        investigationService.update(investigationId, updateInvestigationRequest.getStatus().toDomain(), updateInvestigationRequest.getReason());
    }
}

