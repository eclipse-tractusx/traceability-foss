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

package org.eclipse.tractusx.traceability.investigations.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.tractusx.traceability.common.config.FeatureFlags;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.*;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.Severity;
import org.eclipse.tractusx.traceability.investigations.domain.service.InvestigationsPublisherService;
import org.eclipse.tractusx.traceability.investigations.domain.service.InvestigationsReadService;
import org.eclipse.tractusx.traceability.investigations.domain.service.InvestigationsReceiverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;

import static org.eclipse.tractusx.traceability.investigations.adapters.rest.validation.UpdateInvestigationValidator.validate;

@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
@RestController
@RequestMapping(value = "/investigations", consumes = "application/json", produces = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Investigations")
public class InvestigationsController {

    private final InvestigationsReadService investigationsReadService;
    private final InvestigationsPublisherService investigationsPublisherService;
    private final InvestigationsReceiverService investigationsReceiverService;
    private final TraceabilityProperties traceabilityProperties;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String API_LOG_START = "Received API call on /investigations";

    public InvestigationsController(InvestigationsReadService investigationsReadService,
                                    InvestigationsPublisherService investigationsPublisherService,
                                    InvestigationsReceiverService investigationsReceiverService,
                                    TraceabilityProperties traceabilityProperties) {
        this.investigationsReadService = investigationsReadService;
        this.investigationsPublisherService = investigationsPublisherService;
        this.investigationsReceiverService = investigationsReceiverService;
        this.traceabilityProperties = traceabilityProperties;
    }

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
        InvestigationId investigationId =
                investigationsPublisherService.startInvestigation(
                        traceabilityProperties.getBpn(), request.partIds(), request.description(), request.targetDate(), Severity.valueOf(request.severity()));
        logger.info(API_LOG_START + " with params: {}", request);
        return new StartInvestigationResponse(investigationId.value());
    }

    @Operation(operationId = "getCreatedInvestigations",
            summary = "Gets created investigations",
            tags = {"Investigations"},
            description = "The endpoint returns created investigations as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("/created")
    public PageResult<InvestigationData> getCreatedInvestigations(Pageable pageable) {
        logger.info(API_LOG_START + "/created with params: {}", pageable);
        return investigationsReadService.getCreatedInvestigations(pageable);
    }

    @Operation(operationId = "getReceivedInvestigations",
            summary = "Gets received investigations",
            tags = {"Investigations"},
            description = "The endpoint returns received investigations as paged result.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("/received")
    public PageResult<InvestigationData> getReceivedInvestigations(Pageable pageable) {
        logger.info(API_LOG_START + "/received with params: {}", pageable);
        return investigationsReadService.getReceivedInvestigations(pageable);
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
    public InvestigationData getInvestigation(@PathVariable Long investigationId) {
        logger.info(API_LOG_START + "/{}", investigationId);
        return investigationsReadService.findInvestigation(investigationId);
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
        logger.info(API_LOG_START + "/{}/approve", investigationId);
        investigationsPublisherService.sendInvestigation(traceabilityProperties.getBpn(), investigationId);
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
        logger.info(API_LOG_START + "/{}/cancel", investigationId);
        investigationsPublisherService.cancelInvestigation(traceabilityProperties.getBpn(), investigationId);
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
        logger.info(API_LOG_START + "/{}/close with params {}", investigationId, closeInvestigationRequest);
        investigationsPublisherService.closeInvestigation(traceabilityProperties.getBpn(), investigationId, closeInvestigationRequest.reason());
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
        logger.info(API_LOG_START + "/{}/update with params {}", investigationId, updateInvestigationRequest);
        investigationsPublisherService.updateInvestigationPublisher(traceabilityProperties.getBpn(), investigationId, updateInvestigationRequest.status(), updateInvestigationRequest.reason());
    }
}

