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
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.common.response.ErrorResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.base.service.QualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.mapper.InvestigationResponseMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import qualitynotification.base.request.CloseQualityNotificationRequest;
import qualitynotification.base.request.QualityNotificationStatusRequest;
import qualitynotification.base.request.StartQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationRequest;
import qualitynotification.base.response.QualityNotificationIdResponse;
import qualitynotification.investigation.response.InvestigationResponse;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;
import static org.eclipse.tractusx.traceability.qualitynotification.application.validation.UpdateQualityNotificationValidator.validate;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.exception.StartQualityNotificationDomain.from;
import static org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus.from;

@RestController
@RequestMapping(value = "/investigations", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Investigations", description = "Operations on Investigation Notification")
@Validated
@Slf4j
public class InvestigationsController {

    private final QualityNotificationService investigationService;

    public InvestigationsController(@Qualifier("investigationServiceImpl") QualityNotificationService investigationService) {
        this.investigationService = investigationService;
    }

    private static final String API_LOG_START = "Received API call on /investigations";

    @Operation(operationId = "investigateAssets",
            summary = "Start investigations by part ids",
            tags = {"Investigations"},
            description = "The endpoint starts investigations based on part ids provided.",
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
    public QualityNotificationIdResponse investigateAssets(@RequestBody @Valid StartQualityNotificationRequest request) {
        StartQualityNotificationRequest cleanRequest = sanitize(request);
        log.info(API_LOG_START + " with params: {}", cleanRequest);
        return new QualityNotificationIdResponse(investigationService.start(from(cleanRequest)).value());

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
    public PageResult<InvestigationResponse> getCreatedInvestigations(OwnPageable pageable, SearchCriteriaRequestParam searchCriteriaRequestParam) {
        log.info(API_LOG_START + "/created");
        return InvestigationResponseMapper.fromAsPageResult(investigationService.getCreated(OwnPageable.toPageable(pageable), searchCriteriaRequestParam.toSearchCriteria()));
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
    public PageResult<InvestigationResponse> getReceivedInvestigations(OwnPageable pageable, SearchCriteriaRequestParam searchCriteriaRequestParam) {
        log.info(API_LOG_START + "/received");
        return InvestigationResponseMapper.fromAsPageResult(investigationService.getReceived(OwnPageable.toPageable(pageable), searchCriteriaRequestParam.toSearchCriteria()));
    }

    @Operation(operationId = "getInvestigation",
            summary = "Gets investigations by id",
            tags = {"Investigations"},
            description = "The endpoint returns investigations as paged result by their id.",
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
    @PostMapping("/{investigationId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeInvestigation(@PathVariable Long investigationId, @Valid @RequestBody CloseQualityNotificationRequest closeInvestigationRequest) {
        CloseQualityNotificationRequest cleanCloseQualityNotificationRequest = sanitize(closeInvestigationRequest);
        log.info(API_LOG_START + "/{}/close with params {}", investigationId, cleanCloseQualityNotificationRequest);
        investigationService.update(investigationId, from(QualityNotificationStatusRequest.CLOSED), cleanCloseQualityNotificationRequest.getReason());
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
    @PostMapping("/{investigationId}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateInvestigation(@PathVariable Long investigationId, @Valid @RequestBody UpdateQualityNotificationRequest updateInvestigationRequest) {
        UpdateQualityNotificationRequest cleanUpdateQualityNotificationRequest = sanitize(updateInvestigationRequest);
        validate(cleanUpdateQualityNotificationRequest);
        log.info(API_LOG_START + "/{}/update with params {}", investigationId, cleanUpdateQualityNotificationRequest);
        investigationService.update(investigationId, from(cleanUpdateQualityNotificationRequest.getStatus()), cleanUpdateQualityNotificationRequest.getReason());
    }
}

