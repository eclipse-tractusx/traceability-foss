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
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.domain.metrics.RegistryLookupMeterRegistry;
import org.eclipse.tractusx.traceability.assets.domain.metrics.RegistryLookupMetric;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/metrics", produces = "application/json", consumes = "application/json")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
@Tag(name = "Registry")
@RequiredArgsConstructor
public class RegistryLookupMetricsController {

	private final RegistryLookupMeterRegistry registryLookupMeterRegistry;

	@Operation(operationId = "metrics",
		summary = "Gets Metrics",
		tags = {"Registry"},
            description = "The endpoint gets metrics for database.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns the paged result found for Asset", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(arraySchema = @Schema(description = "RegistryLookupMetric", implementation = RegistryLookupMetric.class), maxItems = Integer.MAX_VALUE)
    )),
            @ApiResponse(responseCode = "401", description = "Authorization failed."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")})
    @GetMapping("/registry-lookup")
    public @Size(max = 1000)
    @ArraySchema(arraySchema = @Schema(description = "RegistryLookupMetric", implementation = RegistryLookupMetric.class), maxItems = Integer.MAX_VALUE)
    PageResult<RegistryLookupMetric> metrics(OwnPageable ownPageable) {
            return registryLookupMeterRegistry.getMetrics(OwnPageable.toPageable(ownPageable));
    }
}
