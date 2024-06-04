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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.traceability.policies.domain.PolicyRepository;
import policies.response.CreatePolicyResponse;
import policies.response.IrsPolicyResponse;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.stereotype.Service;
import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyRepositoryImpl implements PolicyRepository {

    private final PolicyClient policyClient;
    private final TraceabilityProperties traceabilityProperties;

    @Override
    public List<IrsPolicyResponse> getPolicies() {
        return policyClient.getPolicies();
    }

    @Override
    public Optional<IrsPolicyResponse> getPolicy(String policyId) {
        return getPolicies().stream().filter(irsPolicyResponse -> irsPolicyResponse.payload().policyId().equals(policyId)).findFirst();
    }


    @Override
    public void createPolicyBasedOnAppConfig() {
        log.info("Check if irs policy exists");
        final List<IrsPolicyResponse> irsPolicies = this.policyClient.getPolicies();
        final List<String> irsPoliciesIds = irsPolicies.stream().map(policyResponse -> policyResponse.payload().policyId()).toList();
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
    }

    @Override
    public void deletePolicy(String policyId) {
        this.policyClient.deletePolicy(policyId);
    }

    @Override
    public void updatePolicy(UpdatePolicyRequest updatePolicyRequest) {
        this.policyClient.updatePolicy(updatePolicyRequest);
    }

    @Override
    public CreatePolicyResponse createPolicy(RegisterPolicyRequest registerPolicyRequest) {
       return this.policyClient.createPolicy(registerPolicyRequest);
    }



    private IrsPolicyResponse findMatchingPolicy(List<IrsPolicyResponse> irsPolicies) {
        return irsPolicies.stream()
                .filter(irsPolicy -> {
                    // Logging all policy constraints
                    irsPolicy.payload().policy().getPermissions().forEach(permission -> {
                        Constraints constraint = permission.getConstraint();
                        if (constraint != null) {
                            constraint.getAnd().forEach(constraint1 -> log.info("From IRS Policy Response -> Leftoperand {} operator {} and rightOperand {}", constraint1.getLeftOperand(), constraint1.getOperator(), constraint1.getRightOperand()));
                        }
                    });

                    // Check if all constraints exist in the policy
                    boolean firstConstraintExists = emptyIfNull(irsPolicy.payload().policy().getPermissions()).stream()
                            .flatMap(permission -> {
                                Constraints constraint = permission.getConstraint();
                                return constraint != null ? emptyIfNull(constraint.getAnd()).stream() : Stream.empty();
                            })
                            .anyMatch(constraint -> constraint.getRightOperand().equals(traceabilityProperties.getRightOperand()))
                            || emptyIfNull(irsPolicy.payload().policy().getPermissions()).stream()
                            .flatMap(permission -> {
                                Constraints constraint = permission.getConstraint();
                                return constraint != null ? emptyIfNull(constraint.getOr()).stream() : Stream.empty();
                            })
                            .anyMatch(constraint -> constraint.getRightOperand().equals(traceabilityProperties.getRightOperand()));

                    boolean secondConstraintExists = emptyIfNull(irsPolicy.payload().policy().getPermissions()).stream()
                            .flatMap(permission -> {
                                Constraints constraint = permission.getConstraint();
                                return constraint != null ? emptyIfNull(constraint.getAnd()).stream() : Stream.empty();
                            })
                            .anyMatch(constraint -> constraint.getRightOperand().equals(traceabilityProperties.getRightOperandSecond()))
                            || emptyIfNull(irsPolicy.payload().policy().getPermissions()).stream()
                            .flatMap(permission -> {
                                Constraints constraint = permission.getConstraint();
                                return constraint != null ? emptyIfNull(constraint.getOr()).stream() : Stream.empty();
                            })
                            .anyMatch(constraint -> constraint.getRightOperand().equals(traceabilityProperties.getRightOperandSecond()));

                    // Return true if both constraints exist
                    return firstConstraintExists && secondConstraintExists;
                })
                .findFirst()
                .orElse(null);
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

}
