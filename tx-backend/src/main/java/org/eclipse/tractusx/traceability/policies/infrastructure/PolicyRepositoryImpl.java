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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.policy.AcceptedPoliciesProvider;
import org.eclipse.tractusx.irs.edc.client.policy.AcceptedPolicy;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.policies.domain.PolicyRepository;
import org.springframework.stereotype.Service;
import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;
import policies.response.ConstraintResponse;
import policies.response.ConstraintsResponse;
import policies.response.CreatePolicyResponse;
import policies.response.IrsPolicyResponse;
import policies.response.PermissionResponse;
import policies.response.PolicyResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static policies.response.PolicyResponse.toDomain;

@Slf4j
@Service
@AllArgsConstructor
public class PolicyRepositoryImpl implements PolicyRepository {

    private final PolicyClient policyClient;
    private final TraceabilityProperties traceabilityProperties;
    private AcceptedPoliciesProvider.DefaultAcceptedPoliciesProvider defaultAcceptedPoliciesProvider;

    @Override
    public Map<String, List<IrsPolicyResponse>> getPolicies() {
        return policyClient.getPolicies();
    }

    @Override
    public Optional<PolicyResponse> getNewestPolicyByOwnBpn() {
        return policyClient.getNewestPolicyByOwnBpn();
    }

    @Override
    public Map<String, Optional<IrsPolicyResponse>> getPolicy(String policyId) {
        Map<String, Optional<IrsPolicyResponse>> result = new HashMap<>();

        getPolicies().forEach((key, value) -> {
            Optional<IrsPolicyResponse> policyResponse = value.stream()
                    .filter(irsPolicyResponse -> irsPolicyResponse.payload().policyId().equals(policyId))
                    .findFirst();
            if (policyResponse.isPresent()) {
                result.put(key, policyResponse);
            }
        });

        return result;
    }


    @Override
    public void createPolicyBasedOnAppConfig() {
        log.info("Check if irs policy exists");
        final Map<String, List<IrsPolicyResponse>> irsPolicies = this.policyClient.getPolicies();
        final List<String> irsPoliciesIds = irsPolicies.values().stream()
                .flatMap(List::stream)
                .map(irsPolicyResponse -> irsPolicyResponse.payload().policyId())
                .toList();
        log.info("Irs has following policies: {}", irsPoliciesIds);

        log.info("Required constraints - 2 -");
        log.info("First constraint requirements: leftOperand {} operator {} and rightOperand {}", traceabilityProperties.getLeftOperand(), traceabilityProperties.getOperatorType(), traceabilityProperties.getRightOperand());
        log.info("Second constraint requirements: leftOperand {} operator {} and rightOperand {}", traceabilityProperties.getLeftOperandSecond(), traceabilityProperties.getOperatorTypeSecond(), traceabilityProperties.getRightOperandSecond());

        IrsPolicyResponse matchingPolicy = findMatchingPolicy(irsPolicies);

        if (matchingPolicy == null) {
            createMissingPolicies();
        } else {
            checkAndUpdatePolicy(matchingPolicy);
        }
        updateAcceptedPoliciesProvider();
    }

    @Override
    public void deletePolicy(String policyId) {
        this.policyClient.deletePolicy(policyId);
        updateAcceptedPoliciesProvider();
    }

    @Override
    public void updatePolicy(UpdatePolicyRequest updatePolicyRequest) {
        this.policyClient.updatePolicy(updatePolicyRequest);
        updateAcceptedPoliciesProvider();
    }

    @Override
    public CreatePolicyResponse createPolicy(RegisterPolicyRequest registerPolicyRequest) {
        CreatePolicyResponse policy = this.policyClient.createPolicy(registerPolicyRequest);
        updateAcceptedPoliciesProvider();
        return policy;
    }


    private IrsPolicyResponse findMatchingPolicy(Map<String, List<IrsPolicyResponse>> irsPolicies) {
        return irsPolicies.values().stream()
                .flatMap(List::stream)
                .filter(this::checkConstraints)
                .findFirst()
                .orElse(null);
    }

    private boolean checkConstraints(IrsPolicyResponse irsPolicy) {
        boolean firstConstraintExists = checkConstraint(irsPolicy, traceabilityProperties.getRightOperand());
        boolean secondConstraintExists = checkConstraint(irsPolicy, traceabilityProperties.getRightOperandSecond());
        return firstConstraintExists && secondConstraintExists;
    }

    private boolean checkConstraint(IrsPolicyResponse irsPolicy, String rightOperand) {
        return emptyIfNull(irsPolicy.payload().policy().permissions()).stream()
                .flatMap(this::getConstraintsStream)
                .anyMatch(constraint -> constraint.rightOperand().equals(rightOperand));
    }

    private Stream<ConstraintResponse> getConstraintsStream(PermissionResponse permission) {
        ConstraintsResponse constraint = permission.constraint();
        if (constraint == null) {
            return Stream.empty();
        }
        return Stream.concat(
                emptyIfNull(constraint.and()).stream(),
                emptyIfNull(constraint.or()).stream()
        );
    }


    private void createMissingPolicies() {
        log.info("Irs policy does not exist creating {}", traceabilityProperties.getRightOperand());
        this.policyClient.createPolicyFromAppConfig();
    }

    private void checkAndUpdatePolicy(IrsPolicyResponse requiredPolicy) {
        if (isPolicyExpired(requiredPolicy)) {
            log.info("IRS Policy {} has outdated validity updating new ttl", traceabilityProperties.getRightOperand());
            this.policyClient.deletePolicy(traceabilityProperties.getRightOperand());
            this.policyClient.createPolicyFromAppConfig();
        }
    }

    private boolean isPolicyExpired(IrsPolicyResponse requiredPolicy) {
        return traceabilityProperties.getValidUntil().isAfter(requiredPolicy.validUntil());
    }

    private void updateAcceptedPoliciesProvider() {
        defaultAcceptedPoliciesProvider.removeAcceptedPolicies(defaultAcceptedPoliciesProvider.getAcceptedPolicies(null));
        // Flatten the map into a list of IrsPolicyResponse objects
        List<IrsPolicyResponse> irsPolicyResponses = getPolicies().values().stream()
                .flatMap(List::stream)
                .toList();

        // Map the IrsPolicyResponse objects to AcceptedPolicy objects
        List<AcceptedPolicy> irsPolicies = irsPolicyResponses.stream().map(response -> {
            PolicyResponse policy = new PolicyResponse(response.payload().policyId(), response.payload().policy().createdOn(), response.validUntil(), response.payload().policy().permissions(), null);
            return new AcceptedPolicy(toDomain(policy), response.validUntil());
        }).toList();
        defaultAcceptedPoliciesProvider.addAcceptedPolicies(irsPolicies);
    }

}
