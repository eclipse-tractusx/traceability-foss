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

package org.eclipse.tractusx.traceability.assets.application.importpoc.rest;


import assets.importpoc.ErrorResponse;
import assets.importpoc.ImportReportResponse;
import assets.importpoc.ImportResponse;
import assets.importpoc.ImportStateMessage;
import assets.importpoc.ValidationResponse;
import assets.importpoc.request.RegisterAssetRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.application.importpoc.ImportService;
import org.eclipse.tractusx.traceability.assets.application.importpoc.PublishService;
import org.eclipse.tractusx.traceability.assets.application.importpoc.mapper.ImportJobResponseMapper;
import org.eclipse.tractusx.traceability.assets.application.importpoc.validation.JsonFileValidator;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.ImportException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportJob;
import org.eclipse.tractusx.traceability.configuration.application.service.ConfigurationService;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
@Tag(name = "AssetsImport")
@RequestMapping(path = "/assets")
public class ImportController {

    private final ImportService importService;
    private final JsonFileValidator jsonFileValidator;
    private final PublishService publishService;
    private final ConfigurationService configurationService;

    @Operation(operationId = "importJson",
            summary = "asset upload",
            tags = {"AssetsImport"},
            description = "This endpoint stores assets in the application. Those can be later published in the Catena-X network.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImportResponse.class))),
            @ApiResponse(
                    responseCode = "204",
                    description = "No Content.",
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

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImportResponse> importJson(@RequestPart("file") MultipartFile file) {
        log.info("Received request to import assets.");
        ImportJob importJob = importService.createJob();

        List<String> jsonSchemaErrors = jsonFileValidator.isValid(file);
        ValidationResponse validationResponse = new ValidationResponse(jsonSchemaErrors);

        if (!jsonSchemaErrors.isEmpty()) {
            log.warn("Asset import request cannot be processed. Errors: {}", validationResponse);
            importService.cancelJob(importJob);
            return ResponseEntity
                    .badRequest()
                    .body(new ImportResponse(importJob.getId().toString(), validationResponse));
        }


        Map<AssetBase, Boolean> resultMap;
        try {
            resultMap = importService.importAssets(file, importJob);
        } catch (ImportException e) {
            log.info("Could not import data", e);
            importService.cancelJob(importJob);
            List<String> validationErrors = new ArrayList<>();
            validationErrors.add(e.getMessage());
            ValidationResponse importErrorResponse = new ValidationResponse(validationErrors);
            return ResponseEntity
                    .badRequest()
                    .body(new ImportResponse(importJob.getId().toString(), importErrorResponse));
        }

        List<ImportStateMessage> importStateMessages = resultMap.entrySet().stream()
                .map(assetBaseSet -> new ImportStateMessage(
                        assetBaseSet.getKey().getId(),
                        assetBaseSet.getValue())
                ).toList();

        log.info("Successfully imported {} assets.", importStateMessages.size());
        importService.completeJob(importJob);
        ImportResponse importResponse = new ImportResponse(importJob.getId().toString(), importStateMessages);

        return ResponseEntity.ok(importResponse);
    }

    @Operation(operationId = "importReport",
            summary = "report of the imported assets",
            tags = {"ImportReport"},
            description = "This endpoint returns information about the imported assets to Trace-X.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ImportReportResponse.class))),
            @ApiResponse(
                    responseCode = "204",
                    description = "No Content.",
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

    @GetMapping(value = "/import/report/{importJobId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImportReportResponse> getImportReport(@PathVariable("importJobId") String importJobId) {
        ImportJob importJob = importService.getImportJob(importJobId);
        ImportReportResponse importReportResponse = ImportJobResponseMapper.from(importJob);
        return ResponseEntity.status(HttpStatus.OK).body(importReportResponse);
    }

    @Operation(operationId = "publishAssets",
            summary = "asset publish",
            tags = {"AssetsPublish"},
            description = "This endpoint publishes assets to the Catena-X network.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema())),
            @ApiResponse(
                    responseCode = "204",
                    description = "No Content.",
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

    @PostMapping(value = "/publish", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerAssetsForPublishing(@RequestBody RegisterAssetRequest registerAssetRequest, @RequestParam(name = "triggerSynchronizeAssets", defaultValue = "true") boolean triggerSynchronizeAssets) {
        OrderConfiguration latestOrderConfiguration = configurationService.getLatestOrderConfiguration();
        publishService.publishAssets(registerAssetRequest.policyId(), registerAssetRequest.assetIds(), triggerSynchronizeAssets, latestOrderConfiguration);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
