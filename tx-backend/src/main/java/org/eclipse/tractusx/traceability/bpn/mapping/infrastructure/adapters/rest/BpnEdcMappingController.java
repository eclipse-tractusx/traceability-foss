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

package org.eclipse.tractusx.traceability.bpn.mapping.infrastructure.adapters.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.model.BpnEdcMapping;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.service.BpnEdcMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
@Tag(name = "BpnEdcMapping")
@RequestMapping(path = "/bpn-config")
@Validated
public class BpnEdcMappingController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final BpnEdcMappingService service;

    public BpnEdcMappingController(BpnEdcMappingService service) {
        this.service = service;
    }

    @Operation(operationId = "getBpnEdcs",
            summary = "Get BPN EDC URL mappings",
            tags = {"BpnEdcMapping"},
            description = "The endpoint returns a result of BPN EDC URL mappings.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found"),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("")
    public List<BpnEdcMapping> getBpnEdcs() {
        return service.findAllBpnEdcMappings();
    }

    @Operation(operationId = "createBpnEdcUrlMappings",
            summary = "Creates BPN EDC URL mappings",
            tags = {"BpnEdcMapping"},
            description = "The endpoint creates BPN EDC URL mappings",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Created."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public List<BpnEdcMapping> createBpnEdcUrlMapping(@RequestBody @Valid List<BpnEdcMappingRequest> bpnEdcMappings) {
        logger.info("BpnEdcController [createBpnEdcUrlMappings]");
        return service.saveAllBpnEdcMappings(bpnEdcMappings);
    }

    @Operation(operationId = "updateBpnEdcMappings",
            summary = "Updates BPN EDC URL mappings",
            tags = {"BpnEdcMapping"},
            description = "The endpoint updates BPN EDC URL mappings",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Created."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PutMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public List<BpnEdcMapping> updateBpnEdcUrlMapping(@RequestBody @Valid List<BpnEdcMappingRequest> bpnEdcMappings) {
        logger.info("BpnEdcController [createBpnEdcUrlMappings]");
        return service.updateAllBpnEdcMappings(bpnEdcMappings);
    }

    @Operation(operationId = "deleteBpnEdcUrlMappings",
            summary = "Deletes BPN EDC URL mappings",
            tags = {"BpnEdcMapping"},
            description = "The endpoint deletes BPN EDC URL mappings",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deleted."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @DeleteMapping("/{bpn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBpnEdcUrlMapping(@PathVariable String bpn) {
        logger.info("BpnEdcController [deleteBpnEdcUrlMapping]");
        service.deleteBpnEdcMapping(bpn);
    }

}
