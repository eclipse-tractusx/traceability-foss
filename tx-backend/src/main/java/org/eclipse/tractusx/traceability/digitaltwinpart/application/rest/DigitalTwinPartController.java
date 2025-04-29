package org.eclipse.tractusx.traceability.digitaltwinpart.application.rest;

import assets.importpoc.ErrorResponse;
import assets.response.asbuilt.AssetAsBuiltResponse;
import digitaltwinpart.DigitalTwinPartDetailRequest;
import digitaltwinpart.DigitalTwinPartDetailResponse;
import digitaltwinpart.DigitalTwinPartRequest;
import digitaltwinpart.DigitalTwinPartResponse;
import digitaltwinpart.SearchableDigitalTwinPartRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaMapper;
import org.eclipse.tractusx.traceability.common.security.apikey.ApiKeyEnabled;
import org.eclipse.tractusx.traceability.digitaltwinpart.application.mapper.DigitalTwinPartDetailResponseMapper;
import org.eclipse.tractusx.traceability.digitaltwinpart.application.mapper.DigitalTwinPartFieldMapper;
import org.eclipse.tractusx.traceability.digitaltwinpart.application.mapper.DigitalTwinPartResponseMapper;
import org.eclipse.tractusx.traceability.digitaltwinpart.application.service.DigitalTwinPartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Tag(name = "DigitalTwinPart")
@RequestMapping(path = "/administration", produces = "application/json", consumes = "application/json")
public class DigitalTwinPartController {

    private final DigitalTwinPartService digitalTwinPartService;
    private final BaseRequestFieldMapper fieldMapper;

    public DigitalTwinPartController(DigitalTwinPartService digitalTwinPartService,
                                     DigitalTwinPartFieldMapper fieldMapper) {
        this.digitalTwinPartService = digitalTwinPartService;
        this.fieldMapper = fieldMapper;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(operationId = "findAll",
            summary = "Retrieves digital twin part information",
            tags = {"DigitalTwinPart"},
            description = "The endpoint retrieves the digital twin part information.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for digital twin part", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(
                    schema = @Schema(implementation = DigitalTwinPartResponse.class),
                    arraySchema = @Schema(
                            description = "Assets",
                            implementation = AssetAsBuiltResponse.class,
                            additionalProperties = Schema.AdditionalPropertiesValue.FALSE
                    ),
                    maxItems = Integer.MAX_VALUE,
                    minItems = 0)
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
    @PostMapping("/digitalTwinPart")
    @ApiKeyEnabled
    public PageResult<DigitalTwinPartResponse> findBy(@RequestBody DigitalTwinPartRequest digitalTwinPartRequest) {
        OwnPageable ownPageable = OwnPageable.builder().page(digitalTwinPartRequest.getPage()).size(digitalTwinPartRequest.getSize()).sort(digitalTwinPartRequest.getSort()).build();
        SearchCriteria searchCriteria = SearchCriteriaMapper.toSearchCriteria(fieldMapper, digitalTwinPartRequest.getFilters());
        return DigitalTwinPartResponseMapper.fromAsPageResult(
                digitalTwinPartService.findAllBy(
                        OwnPageable.toPageable(ownPageable, fieldMapper),
                        searchCriteria));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(operationId = "findAll",
            summary = "Retrieves digital twin part information",
            tags = {"DigitalTwinPart"},
            description = "The endpoint retrieves the digital twin part information detail.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the result found for digital twin part detail", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(
                    schema = @Schema(implementation = DigitalTwinPartDetailResponse.class),
                    arraySchema = @Schema(
                            description = "Assets",
                            implementation = AssetAsBuiltResponse.class,
                            additionalProperties = Schema.AdditionalPropertiesValue.FALSE
                    ),
                    maxItems = Integer.MAX_VALUE,
                    minItems = 0)
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
    @PostMapping("/digitalTwinPart/detail")
    @ApiKeyEnabled
    public ResponseEntity<DigitalTwinPartDetailResponse> findDetail(@RequestBody DigitalTwinPartDetailRequest digitalTwinPartDetailRequest) {
        return ResponseEntity.ok(DigitalTwinPartDetailResponseMapper.from(
                digitalTwinPartService.findDetail(digitalTwinPartDetailRequest)));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(
            operationId = "searchable-digital-twin-values",
            summary = "Get searchable values for a fieldName",
            tags = { "DigitalTwinPart" },
            description = "The endpoint returns searchable values for a given field name and optional filter criteria.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list of matching field values.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = String.class),
                                    maxItems = Integer.MAX_VALUE,
                                    minItems = 0
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/digitalTwinPart/searchable-values")
    @ApiKeyEnabled
    public ResponseEntity<List<String>> getSearchableValues(@RequestBody @Valid SearchableDigitalTwinPartRequest request) {
        return ResponseEntity.ok(digitalTwinPartService.getSearchableValues(
                fieldMapper.mapRequestFieldName(request.getFieldName()),
                request.getStartWith(),
                request.getSize()));
    }
}

