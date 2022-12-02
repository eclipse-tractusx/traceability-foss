/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.investigations.adapters.rest;

import net.catenax.traceability.common.model.PageResult;
import net.catenax.traceability.common.properties.TraceabilityProperties;
import net.catenax.traceability.investigations.adapters.rest.model.CloseInvestigationRequest;
import net.catenax.traceability.investigations.adapters.rest.model.InvestigationData;
import net.catenax.traceability.investigations.adapters.rest.model.StartInvestigationRequest;
import net.catenax.traceability.investigations.adapters.rest.model.StartInvestigationResponse;
import net.catenax.traceability.investigations.domain.model.InvestigationId;
import net.catenax.traceability.investigations.domain.service.InvestigationsPublisherService;
import net.catenax.traceability.investigations.domain.service.InvestigationsReadService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static net.catenax.traceability.common.config.FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES;

@Profile(NOTIFICATIONS_ENABLED_PROFILES)
@RestController
@RequestMapping("/investigations")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER')")
public class InvestigationsController {

	private final InvestigationsReadService investigationsReadService;
	private final InvestigationsPublisherService investigationsPublisherService;

	private final TraceabilityProperties traceabilityProperties;

	public InvestigationsController(InvestigationsReadService investigationsReadService, InvestigationsPublisherService investigationsPublisherService, TraceabilityProperties traceabilityProperties) {
		this.investigationsReadService = investigationsReadService;
		this.investigationsPublisherService = investigationsPublisherService;
		this.traceabilityProperties = traceabilityProperties;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public StartInvestigationResponse investigateAssets(@RequestBody @Valid StartInvestigationRequest request) {
		InvestigationId investigationId = investigationsPublisherService.startInvestigation(traceabilityProperties.getBpn(), request.partIds(), request.description());

		return new StartInvestigationResponse(investigationId.value());
	}

	@GetMapping("/created")
	public PageResult<InvestigationData> getCreatedInvestigations(Pageable pageable) {
		return investigationsReadService.getCreatedInvestigations(pageable);
	}

	@GetMapping("/received")
	public PageResult<InvestigationData> getReceivedInvestigations(Pageable pageable) {
		return investigationsReadService.getReceivedInvestigations(pageable);
	}

	@GetMapping("/{investigationId}")
	public InvestigationData getInvestigation(@PathVariable Long investigationId) {
		return investigationsReadService.findInvestigation(investigationId);
	}

	@PostMapping("/{investigationId}/approve")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void approveInvestigation(@PathVariable Long investigationId) {
		investigationsPublisherService.approveInvestigation(traceabilityProperties.getBpn(), investigationId);
	}

	@PostMapping("/{investigationId}/cancel")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancelInvestigation(@PathVariable Long investigationId) {
		investigationsPublisherService.cancelInvestigation(traceabilityProperties.getBpn(), investigationId);
	}

	@PostMapping("/{investigationId}/close")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void closeInvestigation(@PathVariable Long investigationId, @RequestBody CloseInvestigationRequest closeInvestigationRequest) {
		investigationsPublisherService.closeInvestigation(traceabilityProperties.getBpn(), investigationId, closeInvestigationRequest.reason());
	}
}

