/********************************************************************************
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.policy.AcceptedPoliciesProvider;
import org.eclipse.tractusx.irs.edc.client.policy.AcceptedPolicy;
import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.irs.edc.client.policy.Operator;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;
import org.eclipse.tractusx.traceability.policies.domain.PolicyRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import policies.response.ConstraintResponse;
import policies.response.IrsPolicyResponse;
import policies.response.PermissionResponse;
import policies.response.PolicyResponse;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.eclipse.tractusx.traceability.common.config.ApplicationProfiles.NOT_INTEGRATION_TESTS;

@EnableAsync(proxyTargetClass = true)
@RequiredArgsConstructor
@Component
@Slf4j
public class PolicyStartUpConfig {

    private final AcceptedPoliciesProvider.DefaultAcceptedPoliciesProvider defaultAcceptedPoliciesProvider;

    @Lazy
    private final PolicyRepository policyRepository;

    @Value("${traceability.register-decentral-registry-permissions}")
    private boolean registerDecentralRegistryPermissions;

    @PostConstruct
    public void registerDecentralRegistryPermissions() throws JsonProcessingException {
        if (!registerDecentralRegistryPermissions) {
            log.info("registerDecentralRegistryPermissions disabled");
            return;
        }
        log.info("registerDecentralRegistryPermissions");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        List<AcceptedPolicy> acceptedPolicy = buildAcceptedPolicies();
        defaultAcceptedPoliciesProvider.addAcceptedPolicies(acceptedPolicy);
        log.info("Successfully added permission to irs client lib provider: {}", mapper.writeValueAsString(acceptedPolicy));
    }

    @NotNull
    public List<AcceptedPolicy> buildAcceptedPolicies() {
        List<AcceptedPolicy> acceptedPolicies = new ArrayList<>(createOwnAcceptedPolicies());
        try {
            acceptedPolicies.addAll(createIrsAcceptedPolicies());
        } catch (Exception exception) {
            log.error("Failed to create Irs Policies : ", exception);
        }
        return acceptedPolicies;
    }

    private List<AcceptedPolicy> createIrsAcceptedPolicies() {
        Map<String, List<IrsPolicyResponse>> policiesMap = policyRepository.getPolicies();

        List<IrsPolicyResponse> irsPolicyResponses = policiesMap.values().stream()
                .flatMap(List::stream)
                .toList();

        List<AcceptedPolicy> irsPolicies = irsPolicyResponses.stream().map(response -> {
            Policy policy = new Policy(response.payload().policyId(), response.payload().policy().getCreatedOn(), response.validUntil(), response.payload().policy().getPermissions());
            return new AcceptedPolicy(policy, response.validUntil());
        }).toList();

        return new ArrayList<>(irsPolicies);
    }


    private List<AcceptedPolicy> createOwnAcceptedPolicies() {

        List<PolicyResponse> policyResponses = policyRepository
                .getLatestPoliciesByApplicationBPNOrDefaultPolicy();

        List<AcceptedPolicy> acceptedPolicies = new ArrayList<>();

        for (PolicyResponse policyResponse : policyResponses) {
            List<PermissionResponse> permissionsResponse = policyResponse.permissions();

            List<Constraint> andConstraintList = new ArrayList<>();
            List<Constraint> orConstraintList = new ArrayList<>();

            for (ConstraintResponse andConstraint : permissionsResponse.get(0).constraints().and()) {
                andConstraintList.add(
                        new Constraint(
                                andConstraint.leftOperand(),
                                new Operator(OperatorType.fromValue(andConstraint.operatorTypeResponse().getCode())),
                                andConstraint.rightOperand()
                        )
                );
            }

            for (ConstraintResponse orConstraint : permissionsResponse.get(0).constraints().or()) {
                orConstraintList.add(
                        new Constraint(
                                orConstraint.leftOperand(),
                                new Operator(OperatorType.fromValue(orConstraint.operatorTypeResponse().getCode())),
                                orConstraint.rightOperand()
                        )
                );
            }

            List<Permission> permissions = List.of(
                    new Permission(
                            PolicyType.USE,
                            new Constraints(
                                    andConstraintList,
                                    orConstraintList
                            )
                    )
            );

            Policy ownPolicy = new Policy(
                    policyResponse.policyId(),
                    OffsetDateTime.now(),
                    policyResponse.validUntil(),
                    permissions
            );

            acceptedPolicies.add(new AcceptedPolicy(ownPolicy, policyResponse.validUntil()));
        }

        return acceptedPolicies;
    }

}
