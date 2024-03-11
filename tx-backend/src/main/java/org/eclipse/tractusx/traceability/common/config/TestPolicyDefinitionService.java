/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.common.config;

import lombok.Generated;
import org.eclipse.tractusx.irs.edc.client.EdcConfiguration;
import org.eclipse.tractusx.irs.edc.client.asset.model.OdrlContext;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcOperator;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcCreatePolicyDefinitionRequest;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicy;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicyPermission;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicyPermissionConstraint;
import org.eclipse.tractusx.irs.edc.client.policy.model.EdcPolicyPermissionConstraintExpression;
import org.eclipse.tractusx.irs.edc.client.policy.model.exception.CreateEdcPolicyDefinitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

public class TestPolicyDefinitionService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TestPolicyDefinitionService.class);
    private static final String USE_ACTION = "USE";
    private static final String POLICY_TYPE = "Policy";
    private static final String POLICY_DEFINITION_TYPE = "PolicyDefinitionRequestDto";
    private static final String ATOMIC_CONSTRAINT = "AtomicConstraint";
    private static final String CONSTRAINT = "Constraint";
    private static final String OPERATOR_PREFIX = "odrl:";
    private final EdcConfiguration config;
    private final RestTemplate restTemplate;

    public String createAccessPolicy(String policyName) throws CreateEdcPolicyDefinitionException {
        String accessPolicyId = UUID.randomUUID().toString();
        return this.createAccessPolicy(policyName, accessPolicyId);
    }

    public String createAccessPolicy(String policyName, String policyId) throws CreateEdcPolicyDefinitionException {
        EdcCreatePolicyDefinitionRequest request = this.createPolicyDefinition(policyName, policyId);
        return this.createAccessPolicy(request);
    }

    public String createAccessPolicy(EdcCreatePolicyDefinitionRequest policyRequest) throws CreateEdcPolicyDefinitionException {
        ResponseEntity createPolicyDefinitionResponse;
        try {
            createPolicyDefinitionResponse = this.restTemplate.postForEntity(this.config.getControlplane().getEndpoint().getPolicyDefinition(), policyRequest, String.class, new Object[0]);
        } catch (HttpClientErrorException var4) {
            if (var4.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                log.info("Notification asset policy definition already exists in the EDC");
                return policyRequest.getPolicyDefinitionId();
            }
            log.error("Failed to create EDC notification asset policy. Reason: ", var4);
            throw new CreateEdcPolicyDefinitionException(var4);
        }

        HttpStatusCode responseCode = createPolicyDefinitionResponse.getStatusCode();
        if (responseCode.value() == HttpStatus.OK.value()) {
            return policyRequest.getPolicyDefinitionId();
        } else {
            throw new CreateEdcPolicyDefinitionException("Failed to create EDC policy definition for asset");
        }
    }

    public EdcCreatePolicyDefinitionRequest createPolicyDefinition(String policyName, String accessPolicyId) {
        EdcPolicyPermissionConstraintExpression constraint =
                EdcPolicyPermissionConstraintExpression.builder()
                        .leftOperand("PURPOSE")
                        .rightOperand(policyName)
                        .operator(new EdcOperator("odrl:eq"))
                        .type("Constraint")
                        .build();
        EdcPolicyPermissionConstraint edcPolicyPermissionConstraint = EdcPolicyPermissionConstraint.builder()
                .orExpressions(List.of(constraint))
                .type("AtomicConstraint").build();
        EdcPolicyPermission odrlPermissions = EdcPolicyPermission.builder().action("USE")
                .edcPolicyPermissionConstraints(edcPolicyPermissionConstraint).build();
        EdcPolicy edcPolicy = EdcPolicy.builder().odrlPermissions(List.of(odrlPermissions)).type("Policy").build();
        OdrlContext odrlContext = OdrlContext.builder().odrl("http://www.w3.org/ns/odrl/2/").build();
        return EdcCreatePolicyDefinitionRequest.builder().policyDefinitionId(accessPolicyId).policy(edcPolicy).odrlContext(odrlContext).type("PolicyDefinitionRequestDto").build();
    }

    @Generated
    public TestPolicyDefinitionService(EdcConfiguration config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }
}
