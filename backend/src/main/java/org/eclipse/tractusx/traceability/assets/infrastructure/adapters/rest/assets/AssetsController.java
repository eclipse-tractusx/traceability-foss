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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.rest.assets;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.tractusx.traceability.assets.application.AssetFacade;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
@Tag(name = "Assets")
@RequestMapping(path = "/assets", produces = "application/json", consumes = "application/json")
public class AssetsController {

	private final AssetRepository assetRepository;
	private final AssetFacade assetFacade;

	public AssetsController(AssetRepository assetRepository, AssetFacade assetFacade) {
		this.assetRepository = assetRepository;
		this.assetFacade = assetFacade;
	}

	@Operation(operationId = "sync",
		summary = "Synchronizes assets from IRS",
		tags = {"Assets"},
		description = "The endpoint synchronizes the assets from irs.",
		security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
	@ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created."),
		@ApiResponse(responseCode = "401", description = "Authorization failed."),
		@ApiResponse(responseCode = "403", description = "Forbidden.")})
	@PostMapping("/sync")
	public void sync(@Valid @RequestBody SyncAssets syncAssets) {
		assetFacade.synchronizeAssetsAsync(syncAssets.globalAssetIds());
	}

	@Operation(operationId = "assets",
		summary = "Get assets by pagination",
		tags = {"Assets"},
		description = "The endpoint returns a paged result of assets.",
		security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found"),
		@ApiResponse(responseCode = "401", description = "Authorization failed."),
		@ApiResponse(responseCode = "403", description = "Forbidden.")})
	@GetMapping("")
	public PageResult<Asset> assets(Pageable pageable) {
		return assetRepository.getAssets(pageable);
	}


	@Operation(operationId = "supplierAssets",
		summary = "Get supplier assets by pagination",
		tags = {"Assets"},
		description = "The endpoint returns a paged result of supplier assets.",
		security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found"),
		@ApiResponse(responseCode = "401", description = "Authorization failed."),
		@ApiResponse(responseCode = "403", description = "Forbidden.")})
	@GetMapping("/supplier")
	public PageResult<Asset> supplierAssets(Pageable pageable) {
		return assetRepository.getSupplierAssets(pageable);
	}

	@Operation(operationId = "ownAssets",
		summary = "Get own assets by pagination",
		tags = {"Assets"},
		description = "The endpoint returns a paged result of own assets.",
		security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found"),
		@ApiResponse(responseCode = "401", description = "Authorization failed."),
		@ApiResponse(responseCode = "403", description = "Forbidden.")})
	@GetMapping("/my")
	public PageResult<Asset> ownAssets(Pageable pageable) {
		return assetRepository.getOwnAssets(pageable);
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
		return assetFacade.getAssetsCountryMap();
	}


	@Operation(operationId = "asset",
		summary = "Get asset by id",
		tags = {"Assets"},
		description = "The endpoint returns an asset filtered by id .",
		security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the assets found",
		content = {@Content(schema = @Schema(implementation = Asset.class))}),
		@ApiResponse(responseCode = "401", description = "Authorization failed."),
		@ApiResponse(responseCode = "403", description = "Forbidden.")})
	@GetMapping("/{assetId}")
	public Asset asset(@PathVariable String assetId) {
		return assetRepository.getAssetById(assetId);
	}


	@Operation(operationId = "asset",
		summary = "Get asset by child id",
		tags = {"Assets"},
		description = "The endpoint returns an asset filtered by child id.",
		security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the asset by childId",
		content = {@Content(schema = @Schema(implementation = Asset.class))}),
		@ApiResponse(responseCode = "401", description = "Authorization failed."),
		@ApiResponse(responseCode = "403", description = "Forbidden.")})
	@GetMapping("/{assetId}/children/{childId}")
	public Asset asset(@PathVariable String assetId, @PathVariable String childId) {
		return assetRepository.getAssetByChildId(assetId, childId);
	}

	@Operation(operationId = "updateAsset",
		summary = "Updates asset",
		tags = {"Assets"},
		description = "The endpoint updates asset by provided quality type.",
		security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the updated asset",
		content = {@Content(schema = @Schema(implementation = Asset.class))}),
		@ApiResponse(responseCode = "401", description = "Authorization failed."),
		@ApiResponse(responseCode = "403", description = "Forbidden.")})
	@PatchMapping("/{assetId}")
	public Asset updateAsset(@PathVariable String assetId, @Valid @RequestBody UpdateAsset updateAsset) {
		return assetFacade.updateAsset(assetId, updateAsset);
	}

	@Operation(operationId = "getDetailInformation",
		summary = "Searches for assets by ids.",
		tags = {"Assets"},
		description = "The endpoint searchs for assets by id and returns a list of them.",
		security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns list of found assets",
		content = {@Content(schema = @Schema(
			type = "array",
			implementation = Asset.class
		))}),
		@ApiResponse(responseCode = "401", description = "Authorization failed."),
		@ApiResponse(responseCode = "403", description = "Forbidden.")})
	@PostMapping("/detail-information")
	public List<Asset> getDetailInformation(@Valid @RequestBody GetDetailInformationRequest getDetailInformationRequest) {
		return assetRepository.getAssetsById(getDetailInformationRequest.assetIds());
	}
}
