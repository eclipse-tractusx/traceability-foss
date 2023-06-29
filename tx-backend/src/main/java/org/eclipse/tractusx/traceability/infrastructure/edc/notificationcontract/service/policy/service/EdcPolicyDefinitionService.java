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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model.CreateEdcAssetException;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.model.CreateEdcPolicyDefinitionException;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.model.EdcCreatePolicyDefinitionRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.model.EdcPolicy;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.model.EdcPolicyPermission;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.model.EdcPolicyPermissionAction;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.model.EdcPolicyPermissionConstraint;
import org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.policy.model.EdcPolicyPermissionConstraintExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.configuration.EdcRestTemplateConfiguration.EDC_REST_TEMPLATE;

@Slf4j
@Component
public class EdcPolicyDefinitionService {

    private static final String CREATE_POLICY_DEFINION_PATH = "/management/v2/policydefinitions";
    private static final String DATA_SPACE_CONNECTOR_PERMISSION = "dataspaceconnector:permission";
    public static final String DATA_SPACE_LITERAL_EXPRESSION = "dataspaceconnector:literalexpression";
    private static final String USE_ACTION = "USE";

    private static final List<EdcPolicyPermissionConstraint> DEFAULT_EDC_POLICY_PERMISSION_CONSTRAINTS = List.of(
            new EdcPolicyPermissionConstraint(
                    "AtomicConstraint",
                    new EdcPolicyPermissionConstraintExpression(DATA_SPACE_LITERAL_EXPRESSION, "idsc:PURPOSE"),
                    new EdcPolicyPermissionConstraintExpression(DATA_SPACE_LITERAL_EXPRESSION, "ID 3.0 Trace"),
                    "EQ"
            )
    );

    private final RestTemplate restTemplate;

    @Autowired
    public EdcPolicyDefinitionService(@Qualifier(EDC_REST_TEMPLATE) RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createAccessPolicy(String notificationAssetId) {
        EdcPolicyPermission edcPolicyPermission = new EdcPolicyPermission(
                DATA_SPACE_CONNECTOR_PERMISSION,
                new EdcPolicyPermissionAction(USE_ACTION),
                notificationAssetId,
                DEFAULT_EDC_POLICY_PERMISSION_CONSTRAINTS
        );

        EdcPolicy edcPolicy = new EdcPolicy(List.of(edcPolicyPermission));

        String accessPolicyId = UUID.randomUUID().toString();

        EdcCreatePolicyDefinitionRequest edcCreatePolicyDefinitionRequest = new EdcCreatePolicyDefinitionRequest(accessPolicyId, edcPolicy);

        final ResponseEntity<String> createPolicyDefinitionResponse;
        try {
            createPolicyDefinitionResponse = restTemplate.postForEntity(CREATE_POLICY_DEFINION_PATH, edcCreatePolicyDefinitionRequest, String.class);
        } catch (RestClientException e) {
            log.error("Failed to create EDC notification asset policy {} notification asset id. Reason: ", notificationAssetId, e);

            throw new CreateEdcPolicyDefinitionException(e);
        }

        HttpStatusCode responseCode = createPolicyDefinitionResponse.getStatusCode();

        if (responseCode.value() == 409) {
            log.info("{} notification asset policy definition already exists in the EDC", notificationAssetId);

            throw new CreateEdcPolicyDefinitionException("Notification asset policy definition already exists in the EDC");
        }

        if (responseCode.value() == 200) {
            return accessPolicyId;
        }

        log.error("Failed to create EDC notification policy definition for {} notification asset id. Body: {}, status: {}", notificationAssetId, createPolicyDefinitionResponse.getBody(), createPolicyDefinitionResponse.getStatusCode());

        throw new CreateEdcAssetException("Failed to create EDC notification policy definition for %s asset id".formatted(notificationAssetId));
    }

    public void deleteAccessPolicy(String accessPolicyId) {
        String deleteUri = UriComponentsBuilder.fromPath(CREATE_POLICY_DEFINION_PATH)
                .pathSegment("{accessPolicyId}")
                .buildAndExpand(accessPolicyId)
                .toUriString();

        try {
            restTemplate.delete(deleteUri);
        } catch (RestClientException e) {
            log.error("Failed to delete EDC notification asset policy {}. Reason: ", accessPolicyId, e);
        }
    }
}
