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

package org.eclipse.tractusx.traceability.assets.application.base.rest;

import assets.response.AssetAsBuiltResponse;
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
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.application.asbuilt.mapper.AssetAsBuiltResponseMapper;
import org.eclipse.tractusx.traceability.assets.application.asbuilt.service.AssetAsBuiltService;
import org.eclipse.tractusx.traceability.assets.application.base.request.GetDetailInformationRequest;
import org.eclipse.tractusx.traceability.assets.application.base.request.SyncAssetsRequest;
import org.eclipse.tractusx.traceability.assets.application.base.request.UpdateAssetRequest;
import org.eclipse.tractusx.traceability.assets.application.base.service.AssetBaseService;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.response.ErrorResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Assets")
@RequestMapping(path = "/assets", produces = "application/json", consumes = "application/json")
@RequiredArgsConstructor
public class AssetBaseController {

    @Qualifier("assetBaseServiceImpl")
    private final AssetBaseService assetBaseService;

    @Operation(operationId = "sync",
            summary = "Synchronizes assets from IRS",
            tags = {"Assets"},
            description = "The endpoint synchronizes the assets from irs.",
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
    @PostMapping("/sync")
    public void sync(@Valid @RequestBody SyncAssetsRequest syncAssetsRequest) {
        assetBaseService.synchronizeAssetsAsync(syncAssetsRequest.globalAssetIds());
    }


}
