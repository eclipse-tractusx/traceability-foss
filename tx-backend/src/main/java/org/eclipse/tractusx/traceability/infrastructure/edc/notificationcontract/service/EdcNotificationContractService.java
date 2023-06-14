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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service;

import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.CreateNotificationContractException;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.CreateNotificationContractRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.CreateNotificationContractResponse;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.controller.model.NotificationMethod;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.CreateEdcAssetException;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.service.EdcNotitifcationAssetService;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.model.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.model.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.service.EdcPolicyDefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EdcNotificationContractService {

	private static final Logger logger = LoggerFactory.getLogger(EdcNotificationContractService.class);

	private final EdcNotitifcationAssetService edcNotitifcationAssetService;
	private final EdcPolicyDefinitionService edcPolicyDefinitionService;
	private final EdcContractDefinitionService edcContractDefinitionService;

	public EdcNotificationContractService(EdcNotitifcationAssetService edcNotitifcationAssetService,
										  EdcPolicyDefinitionService edcPolicyDefinitionService,
										  EdcContractDefinitionService edcContractDefinitionService) {
		this.edcNotitifcationAssetService = edcNotitifcationAssetService;
		this.edcPolicyDefinitionService = edcPolicyDefinitionService;
		this.edcContractDefinitionService = edcContractDefinitionService;
	}

	public CreateNotificationContractResponse handle(CreateNotificationContractRequest request) {

		NotificationMethod notificationMethod = request.notificationMethod();

		logger.info("Creating EDC asset notification contract for {} method and {} notification type", notificationMethod.getValue(), request.notificationType().getValue());

		final String notificationAssetId;
		try {
			notificationAssetId = edcNotitifcationAssetService.createNotificationAsset(notificationMethod, request.notificationType());
		} catch (CreateEdcAssetException e) {
			throw new CreateNotificationContractException(e);
		}

		final String accessPolicyId;
		try {
			accessPolicyId = edcPolicyDefinitionService.createAccessPolicy(notificationAssetId);
		} catch (CreateEdcPolicyDefinitionException e) {
			revertNotificationAsset(notificationAssetId);

			throw new CreateNotificationContractException(e);
		}

		final String contractDefinitionId;
		try {
			contractDefinitionId = edcContractDefinitionService.createContractDefinition(notificationAssetId, accessPolicyId);
		} catch (CreateEdcContractDefinitionException e) {
			revertAccessPolicy(accessPolicyId);
			revertNotificationAsset(notificationAssetId);

			throw new CreateNotificationContractException(e);
		}

		logger.info("Created notification contract for {} notification asset id, access policy id {} and contract definition id {}", notificationAssetId, accessPolicyId, contractDefinitionId);

		return new CreateNotificationContractResponse(
			notificationAssetId,
			accessPolicyId,
			contractDefinitionId
		);
	}

	private void revertAccessPolicy(String accessPolicyId) {
		logger.info("Removing {} access policy", accessPolicyId);

		edcPolicyDefinitionService.deleteAccessPolicy(accessPolicyId);
	}

	private void revertNotificationAsset(String notificationAssetId) {
		logger.info("Removing {} notification asset", notificationAssetId);

		edcNotitifcationAssetService.deleteNotificationAsset(notificationAssetId);
	}
}
