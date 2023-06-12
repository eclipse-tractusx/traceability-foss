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

package org.eclipse.tractusx.traceability.assets.application.rest;

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
import org.eclipse.tractusx.traceability.assets.application.rest.request.GetDetailInformationRequest;
import org.eclipse.tractusx.traceability.assets.application.rest.request.SyncAssetsRequest;
import org.eclipse.tractusx.traceability.assets.application.rest.request.UpdateAssetRequest;
import org.eclipse.tractusx.traceability.assets.application.rest.response.AssetResponse;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Assets")
@RequestMapping(path = "/assets", produces = "application/json", consumes = "application/json")
@RequiredArgsConstructor
public class AssetsController {

    private final AssetService assetService;

    @Operation(operationId = "sync",
            summary = "Synchronizes assets from IRS",
            tags = {"Assets"},
            description = "The endpoint synchronizes the assets from irs.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PostMapping("/sync")
    public void sync(@Valid @RequestBody SyncAssetsRequest syncAssetsRequest) {
        assetService.synchronizeAssetsAsync(syncAssetsRequest.globalAssetIds());
    }

    @Operation(operationId = "assets",
            summary = "Get assets by pagination",
            tags = {"Assets"},
            description = "The endpoint returns a paged result of assets.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "Assets", implementation = AssetResponse.class), maxItems = Integer.MAX_VALUE)
    )),
            @ApiResponse(responseCode = "401", description = "Authorization failed.", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden.", content = @Content())})
    @GetMapping("")
    public PageResult<AssetResponse> assets(OwnPageable pageable, @QueryParam("owner") Owner owner) {
        return AssetResponse.from(assetService.getAssets(OwnPageable.toPageable(pageable), owner));
    }

    @Operation(operationId = "assetsCountryMap",
            summary = "Get map of assets",
            tags = {"Assets"},
            description = "The endpoint returns a map for assets consumed by the map.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the assets found"),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("/countries")
    public Map<String, Long> assetsCountryMap() {
        return assetService.getAssetsCountryMap();
    }


    @Operation(operationId = "asset",
            summary = "Get asset by id",
            tags = {"Assets"},
            description = "The endpoint returns an asset filtered by id .",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the assets found",
            content = {@Content(schema = @Schema(implementation = AssetResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("/{assetId}")
    public AssetResponse asset(@PathVariable String assetId) {
        return AssetResponse.from(assetService.getAssetById(assetId));
    }


    @Operation(operationId = "asset",
            summary = "Get asset by child id",
            tags = {"Assets"},
            description = "The endpoint returns an asset filtered by child id.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the asset by childId",
            content = {@Content(schema = @Schema(implementation = AssetResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("/{assetId}/children/{childId}")
    public AssetResponse asset(@PathVariable String assetId, @PathVariable String childId) {
        return AssetResponse.from(assetService.getAssetByChildId(assetId, childId));
    }

    @Operation(operationId = "updateAsset",
            summary = "Updates asset",
            tags = {"Assets"},
            description = "The endpoint updates asset by provided quality type.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the updated asset",
            content = {@Content(schema = @Schema(implementation = AssetResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @PatchMapping("/{assetId}")
    public AssetResponse updateAsset(@PathVariable String assetId, @Valid @RequestBody UpdateAssetRequest updateAssetRequest) {
        return AssetResponse.from(
                assetService.updateQualityType(assetId, updateAssetRequest.qualityType().toDomain())
        );
    }

    @Operation(operationId = "getDetailInformation",
            summary = "Searches for assets by ids.",
            tags = {"Assets"},
            description = "The endpoint searchs for assets by id and returns a list of them.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "Assets", implementation = AssetResponse.class), maxItems = Integer.MAX_VALUE)
    )),
            @ApiResponse(responseCode = "401", description = "Authorization failed.", content = @Content()),
            @ApiResponse(responseCode = "403", description = "Forbidden.", content = @Content())})
    @PostMapping("/detail-information")
    public List<AssetResponse> getDetailInformation(@Valid @RequestBody GetDetailInformationRequest getDetailInformationRequest) {
        return AssetResponse.from(
                assetService.getAssetsById(getDetailInformationRequest.assetIds())
        );
    }
}
