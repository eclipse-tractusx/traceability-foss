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
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.request.CloseInvestigationRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.request.StartInvestigationRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.request.UpdateInvestigationRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.response.InvestigationDTO;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.response.StartInvestigationResponse;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service.InvestigationService;
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

import static org.eclipse.tractusx.traceability.qualitynotification.application.investigation.validation.UpdateInvestigationValidator.validate;

@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
@RestController
@RequestMapping(value = "/investigations", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Investigations")
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
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StartInvestigationResponse investigateAssets(@RequestBody @Valid StartInvestigationRequest request) {
        log.info(API_LOG_START + " with params: {}", request);
        return new StartInvestigationResponse(investigationService.startInvestigation(
                request.partIds(), request.description(), request.targetDate(), request.severity()).value());
    }

    @Operation(operationId = "getCreatedInvestigations",
            summary = "Gets created investigations",
            tags = {"Investigations"},
            description = "The endpoint returns created investigations as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "InvestigationData", implementation = InvestigationDTO.class), maxItems = Integer.MAX_VALUE)
    )),
            @ApiResponse(responseCode = "401", description = "Authorization failed.", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden.", content = @Content())})
    @GetMapping("/created")
    public PageResult<InvestigationDTO> getCreatedInvestigations(Pageable pageable) {
        log.info(API_LOG_START + "/created");
        return investigationService.getCreatedInvestigations(pageable);
    }

    @Operation(operationId = "getReceivedInvestigations",
            summary = "Gets received investigations",
            tags = {"Investigations"},
            description = "The endpoint returns received investigations as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "InvestigationData", implementation = InvestigationDTO.class), maxItems = Integer.MAX_VALUE)
    )),
            @ApiResponse(responseCode = "401", description = "Authorization failed.", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden.", content = @Content())})
    @GetMapping("/received")
    public PageResult<InvestigationDTO> getReceivedInvestigations(Pageable pageable) {
        log.info(API_LOG_START + "/received");
        return investigationService.getReceivedInvestigations(pageable);
    }

    @Operation(operationId = "getInvestigation",
            summary = "Gets investigations by id",
            tags = {"Investigations"},
            description = "The endpoint returns investigations as paged result by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("/{investigationId}")
    public InvestigationDTO getInvestigation(@PathVariable Long investigationId) {
        log.info(API_LOG_START + "/{}", investigationId);
        return investigationService.findInvestigation(investigationId);
    }

    @Operation(operationId = "approveInvestigation",
            summary = "Approves investigations by id",
            tags = {"Investigations"},
            description = "The endpoint approves investigations by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PostMapping("/{investigationId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveInvestigation(@PathVariable Long investigationId) {
        log.info(API_LOG_START + "/{}/approve", investigationId);
        investigationService.approveInvestigation(investigationId);
    }

    @Operation(operationId = "cancelInvestigation",
            summary = "Cancles investigations by id",
            tags = {"Investigations"},
            description = "The endpoint cancles investigations by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PostMapping("/{investigationId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelInvestigation(@PathVariable Long investigationId) {
        log.info(API_LOG_START + "/{}/cancel", investigationId);
        investigationService.cancelInvestigation(investigationId);
    }

    @Operation(operationId = "closeInvestigation",
            summary = "Close investigations by id",
            tags = {"Investigations"},
            description = "The endpoint closes investigations by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
    @PostMapping("/{investigationId}/close")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeInvestigation(@PathVariable Long investigationId, @Valid @RequestBody CloseInvestigationRequest closeInvestigationRequest) {
        log.info(API_LOG_START + "/{}/close with params {}", investigationId, closeInvestigationRequest);
        investigationService.updateInvestigation(investigationId, InvestigationStatus.CLOSED, closeInvestigationRequest.reason());
    }

    @Operation(operationId = "updateInvestigation",
            summary = "Update investigations by id",
            tags = {"Investigations"},
            description = "The endpoint updates investigations by their id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISOR')")
    @PostMapping("/{investigationId}/update")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateInvestigation(@PathVariable Long investigationId, @Valid @RequestBody UpdateInvestigationRequest updateInvestigationRequest) {
        validate(updateInvestigationRequest);
        log.info(API_LOG_START + "/{}/update with params {}", investigationId, updateInvestigationRequest);
        investigationService.updateInvestigation(investigationId, InvestigationStatus.fromStringValue(updateInvestigationRequest.status().name()), updateInvestigationRequest.reason());
    }
}

