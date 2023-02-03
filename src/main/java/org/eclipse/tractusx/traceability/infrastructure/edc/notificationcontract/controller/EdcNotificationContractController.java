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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller;

import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.EdcNotificationContractService;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.CreateNotificationContractRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.CreateNotificationContractResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/edc/notification")
@PreAuthorize("hasAnyRole('ADMIN')")
public class EdcNotificationContractController {

	private final EdcNotificationContractService edcNotificationContractService;

	public EdcNotificationContractController(EdcNotificationContractService edcNotificationContractService) {
		this.edcNotificationContractService = edcNotificationContractService;
	}

	@PostMapping("/contract")
	@ResponseStatus(HttpStatus.CREATED)
	public CreateNotificationContractResponse createNotificationContract(@Valid @RequestBody CreateNotificationContractRequest request) {
		return edcNotificationContractService.handle(request);
	}
}
