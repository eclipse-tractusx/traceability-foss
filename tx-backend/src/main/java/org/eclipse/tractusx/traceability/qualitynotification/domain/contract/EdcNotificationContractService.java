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
package org.eclipse.tractusx.traceability.qualitynotification.domain.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.qualitynotification.application.contract.model.CreateNotificationContractException;
import org.eclipse.tractusx.traceability.qualitynotification.application.contract.model.CreateNotificationContractRequest;
import org.eclipse.tractusx.traceability.qualitynotification.application.contract.model.CreateNotificationContractResponse;
import org.eclipse.tractusx.traceability.qualitynotification.application.contract.model.NotificationMethod;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.asset.model.CreateEdcAssetException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.asset.service.EdcNotificationAssetService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.contract.model.CreateEdcContractDefinitionException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.policy.model.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.contract.policy.service.EdcPolicyDefinitionService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EdcNotificationContractService {

    private final EdcNotificationAssetService edcNotificationAssetService;
    private final EdcPolicyDefinitionService edcPolicyDefinitionService;
    private final EdcContractDefinitionService edcContractDefinitionService;

    public CreateNotificationContractResponse handle(CreateNotificationContractRequest request) {

        NotificationMethod notificationMethod = request.notificationMethod();

        log.info("Creating EDC asset notification contract for {} method and {} notification type", notificationMethod.getValue(), request.notificationType().getValue());

        String notificationAssetId = "";
        try {
            notificationAssetId = edcNotificationAssetService.createNotificationAsset(notificationMethod, request.notificationType());
        } catch (CreateEdcAssetException e) {
            throw new CreateNotificationContractException(e);
        } catch (JsonProcessingException e2) {
            log.error(e2.toString());
        }


        String accessPolicyId = "";
        try {
            accessPolicyId = edcPolicyDefinitionService.createAccessPolicy();
        } catch (CreateEdcPolicyDefinitionException e) {
            revertNotificationAsset(notificationAssetId);
            throw new CreateNotificationContractException(e);
        } catch (JsonProcessingException e2) {
            log.error(e2.toString());
        }

        String contractDefinitionId = "";
        try {
            contractDefinitionId = edcContractDefinitionService.createContractDefinition(notificationAssetId, accessPolicyId);
        } catch (CreateEdcContractDefinitionException e) {
            revertAccessPolicy(accessPolicyId);
            revertNotificationAsset(notificationAssetId);

            throw new CreateNotificationContractException(e);
        } catch (JsonProcessingException e2) {
            log.error(e2.toString());
        }

        log.info("Created notification contract for {} notification asset id, access policy id {} and contract definition id {}", notificationAssetId, accessPolicyId, contractDefinitionId);

        return new CreateNotificationContractResponse(
                notificationAssetId,
                accessPolicyId,
                contractDefinitionId
        );
    }

    private void revertAccessPolicy(String accessPolicyId) {
        log.info("Removing {} access policy", accessPolicyId);

        edcPolicyDefinitionService.deleteAccessPolicy(accessPolicyId);
    }

    private void revertNotificationAsset(String notificationAssetId) {
        log.info("Removing {} notification asset", notificationAssetId);

        edcNotificationAssetService.deleteNotificationAsset(notificationAssetId);
    }
}
