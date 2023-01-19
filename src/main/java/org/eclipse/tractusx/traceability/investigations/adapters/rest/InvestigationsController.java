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

package org.eclipse.tractusx.traceability.investigations.adapters.rest;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.CloseInvestigationRequest;
import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.InvestigationData;
import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.StartInvestigationRequest;
import org.eclipse.tractusx.traceability.investigations.adapters.rest.model.StartInvestigationResponse;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.service.InvestigationsPublisherService;
import org.eclipse.tractusx.traceability.investigations.domain.service.InvestigationsReadService;
import org.eclipse.tractusx.traceability.common.config.FeatureFlags;
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

@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
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
	public void closeInvestigation(@PathVariable Long investigationId, @Valid @RequestBody CloseInvestigationRequest closeInvestigationRequest) {
		investigationsPublisherService.closeInvestigation(traceabilityProperties.getBpn(), investigationId, closeInvestigationRequest.reason());
	}
}

