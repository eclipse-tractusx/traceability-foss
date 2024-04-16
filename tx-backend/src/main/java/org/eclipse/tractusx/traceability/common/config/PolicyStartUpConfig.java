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
import org.eclipse.tractusx.traceability.assets.domain.base.PolicyRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsPolicyResponse;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@ConfigurationPropertiesScan(basePackages = "org.eclipse.tractusx.traceability.*")
@EnableWebMvc
@EnableAsync(proxyTargetClass = true)
@EnableConfigurationProperties
@Slf4j
@EnableJpaRepositories(basePackages = "org.eclipse.tractusx.traceability.*")
@RequiredArgsConstructor

public class PolicyStartUpConfig {

    private final AcceptedPoliciesProvider.DefaultAcceptedPoliciesProvider defaultAcceptedPoliciesProvider;
    private final TraceabilityProperties traceabilityProperties;

    @Lazy
    private final PolicyRepository policyRepository;

    @PostConstruct
    @ConditionalOnProperty(name = "applicationConfig.registerDecentralRegistryPermissions.enabled", havingValue = "true")
    public void registerDecentralRegistryPermissions() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<AcceptedPolicy> acceptedPolicy = buildAcceptedPolicies();
        defaultAcceptedPoliciesProvider.addAcceptedPolicies(acceptedPolicy);
        log.info("Successfully added permission to irs client lib provider: {}", mapper.writeValueAsString(acceptedPolicy));
    }

    @NotNull
    private List<AcceptedPolicy> buildAcceptedPolicies() {
        List<AcceptedPolicy> acceptedPolicies = new ArrayList<>(createOwnAcceptedPolicies(traceabilityProperties.getValidUntil()));

        //add IRS policies
        try {
            acceptedPolicies.addAll(createIrsAcceptedPolicies());
        } catch (Exception exception) {
            log.error("Failed to create Irs Policies : ", exception);
        }
        return acceptedPolicies;
    }

    private List<AcceptedPolicy> createIrsAcceptedPolicies() {

        List<IrsPolicyResponse> irsPolicyResponse = policyRepository.getPolicies();
        List<AcceptedPolicy> irsPolicies = irsPolicyResponse.stream().map(response -> {
            Policy policy = new Policy(response.payload().policyId(), response.payload().policy().getCreatedOn(), response.validUntil(), response.payload().policy().getPermissions());
            return new AcceptedPolicy(policy, response.validUntil());
        }).toList();

        return new ArrayList<>(irsPolicies);
    }

    private List<AcceptedPolicy> createOwnAcceptedPolicies(OffsetDateTime offsetDateTime) {
        List<Constraint> andConstraintList = new ArrayList<>();
        List<Constraint> orConstraintList = new ArrayList<>();
        orConstraintList.add(new Constraint(traceabilityProperties.getLeftOperand(), new Operator(OperatorType.fromValue(traceabilityProperties.getOperatorType())), traceabilityProperties.getRightOperand()));

        List<Permission> permissions = List.of(
                new Permission(
                        PolicyType.USE,
                        new Constraints(
                                andConstraintList,
                                orConstraintList)

                ));

        Policy ownPolicy = new Policy(UUID.randomUUID().toString(), OffsetDateTime.now(), offsetDateTime, permissions);
        return List.of(new AcceptedPolicy(ownPolicy, offsetDateTime));
    }
}
