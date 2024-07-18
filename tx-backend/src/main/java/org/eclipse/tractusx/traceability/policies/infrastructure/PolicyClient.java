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
package org.eclipse.tractusx.traceability.policies.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.irs.edc.client.policy.Operator;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import policies.request.Context;
import policies.request.Payload;
import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;
import policies.response.CreatePolicyResponse;
import policies.response.IrsPolicyResponse;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.IRS_ADMIN_TEMPLATE;

@Slf4j
@Component
public class PolicyClient {
    private final RestTemplate irsAdminTemplate;
    private final TraceabilityProperties traceabilityProperties;

    @Value("${traceability.irsPoliciesPath}")
    String policiesPath = null;

    public PolicyClient(@Qualifier(IRS_ADMIN_TEMPLATE) RestTemplate irsAdminTemplate,
                        TraceabilityProperties traceabilityProperties) {
        this.irsAdminTemplate = irsAdminTemplate;
        this.traceabilityProperties = traceabilityProperties;
    }

    public Map<String, List<IrsPolicyResponse>> getPolicies() {
        Map<String, List<IrsPolicyResponse>> body = irsAdminTemplate.exchange(policiesPath, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, List<IrsPolicyResponse>>>() {
        }).getBody();

        if (body != null) {
            body.forEach((key, valueList) -> {
                log.info("Key: {}", StringUtils.normalizeSpace(key));
                valueList.forEach(value -> log.info("Policy: {}", StringUtils.normalizeSpace(value.toString())));
            });
        } else {
            log.info("No policies retrieved from IRS Policy Store");
        }
        return body;
    }

    public void deletePolicy(String policyId) {
        irsAdminTemplate.exchange(policiesPath + "/" + policyId, HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {
        });
    }

    public void updatePolicy(UpdatePolicyRequest updatePolicyRequest) {
        irsAdminTemplate.exchange(policiesPath, HttpMethod.PUT, new HttpEntity<>(updatePolicyRequest), new ParameterizedTypeReference<>() {
        });
    }

    public CreatePolicyResponse createPolicy(RegisterPolicyRequest registerPolicyRequest) {
        return irsAdminTemplate.exchange(policiesPath, HttpMethod.POST, new HttpEntity<>(registerPolicyRequest), CreatePolicyResponse.class).getBody();
    }

    public void createPolicyFromAppConfig() {
        OffsetDateTime validUntil = traceabilityProperties.getValidUntil();
        Context context = Context.getDefault();
        String policyId = UUID.randomUUID().toString();

        Constraint constraint = new Constraint(traceabilityProperties.getLeftOperand(), new Operator(OperatorType.EQ), traceabilityProperties.getRightOperand());
        Constraint constraintSecond = new Constraint(traceabilityProperties.getLeftOperandSecond(), new Operator(OperatorType.EQ), traceabilityProperties.getRightOperandSecond());

        Constraints constraints = Constraints.builder()
                .and(List.of(constraint, constraintSecond))
                .build();

        Permission permission = Permission.builder()
                .action(PolicyType.USE)
                .constraint(constraints)
                .build();

        Policy policy = new Policy(policyId, Instant.now().atOffset(ZoneOffset.UTC), validUntil, List.of(permission));

        Payload payload = new Payload(context, policyId, policy);
        RegisterPolicyRequest registerPolicyRequest = new RegisterPolicyRequest(validUntil.toInstant(), traceabilityProperties.getBpn().value(), payload);
        irsAdminTemplate.exchange(policiesPath, HttpMethod.POST, new HttpEntity<>(registerPolicyRequest), Void.class);
    }
}
