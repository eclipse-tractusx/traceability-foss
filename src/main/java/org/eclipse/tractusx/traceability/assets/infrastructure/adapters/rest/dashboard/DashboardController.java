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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.rest.dashboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.eclipse.tractusx.traceability.assets.domain.model.Dashboard;
import org.eclipse.tractusx.traceability.assets.domain.service.DashboardService;
import org.eclipse.tractusx.traceability.common.security.InjectedJwtAuthentication;
import org.eclipse.tractusx.traceability.common.security.JwtAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Dashboard")
@RequestMapping(path = "/dashboard", produces = "application/json")
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/")
	@Operation(operationId = "dashboard",
		summary = "Returns dashboard related data",
		tags = {"Dashboard"},
		description = "The endpoint can return limited data based on the user role",
		security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns dashboard data",
		content = {@Content(schema = @Schema(implementation = Dashboard.class))}),
		@ApiResponse(responseCode = "401", description = "Authorization failed."),
		@ApiResponse(responseCode = "403", description = "Forbidden.")})
	public Dashboard dashboard(@InjectedJwtAuthentication JwtAuthentication jwtAuthentication) {
		return dashboardService.getDashboard(jwtAuthentication);
	}
}
