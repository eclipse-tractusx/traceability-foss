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
package org.eclipse.tractusx.traceability.contracts.application.rest;

import contract.response.ContractResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.contracts.application.mapper.ContractResponseMapper;
import org.eclipse.tractusx.traceability.contracts.application.service.ContractServiceReadOnly;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
@Tag(name = "Contracts")
@RequestMapping(path = "/contracts", produces = "application/json", consumes = "application/json")
public class ContractsController {

    private final ContractServiceReadOnly contractServiceReadOnly;

    public ContractsController(@Qualifier("contractViewServiceImpl") ContractServiceReadOnly contractServiceReadOnly) {
        this.contractServiceReadOnly = contractServiceReadOnly;
    }

    @Operation(operationId = "contracts",
            summary = "All contract agreements for all assets",
            tags = {"Contracts"},
            description = "This endpoint returns all contract agreements for all assets in Trace-X",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(example = "{\"message\": \"Bad request.\"}"))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(example = "{\"message\": \"Authorization failed.\"}"))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(example = "{\"message\": \"Forbidden.\"}"))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(example = "{\"message\": \"Not found.\"}"))),
            @ApiResponse(
                    responseCode = "415", description = "Unsupported media type.",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(example = "{\"message\": \"Unsupported media type.\"}"))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(example = "{\"message\": \"Too many requests.\"}"))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(example = "{\"message\": \"Internal server error.\"}")))})
    @PostMapping
    public PageResult<ContractResponse> getContracts(@Valid @RequestBody PageableFilterRequest pageableFilterRequest) {
        PageResult<Contract> contracts = contractServiceReadOnly.getContracts(pageableFilterRequest);
        return ContractResponseMapper.from(contracts);
    }

}
