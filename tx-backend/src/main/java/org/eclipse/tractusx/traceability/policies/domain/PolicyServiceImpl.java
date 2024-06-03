/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.policies.domain;

import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;
import policies.response.CreatePolicyResponse;
import policies.response.PolicyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.policies.application.service.PolicyService;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PolicyNotFoundException;
import policies.response.IrsPolicyResponse;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final TraceabilityProperties traceabilityProperties;

    @Override
    public List<PolicyResponse> getPolicies() {
        return IrsPolicyResponse.toResponse(getAcceptedPoliciesOrEmptyList());
    }

    @Override
    public PolicyResponse getPolicy(String id) {
        return getAcceptedPoliciesOrEmptyList().stream()
                .filter(policy -> policy.payload().policy().getPolicyId().equals(id)).findFirst()
                .map(IrsPolicyResponse::toResponse)
                .orElseThrow(() -> new PolicyNotFoundException("Policy with id: %s not found.".formatted(id)));
    }

    @Override
    public Optional<PolicyResponse> getFirstPolicyMatchingApplicationConstraint() {
        Optional<String> policyId = getPolicies().stream()
                .flatMap(policyResponse -> policyResponse.permissions().stream()
                        .flatMap(permissionResponse -> Stream.concat(
                                        permissionResponse.constraints().and().stream(),
                                        permissionResponse.constraints().or().stream())
                                .map(constraintResponse -> new AbstractMap.SimpleEntry<>(policyResponse.policyId(), constraintResponse))))
                .filter(entry -> {
                    boolean hasFirstConstraint = entry.getValue().rightOperand().equalsIgnoreCase(traceabilityProperties.getRightOperand()) && entry.getValue().leftOperand().equalsIgnoreCase(traceabilityProperties.getLeftOperand());
                    boolean hasSecondConstraint = entry.getValue().rightOperand().equalsIgnoreCase(traceabilityProperties.getRightOperandSecond()) && entry.getValue().leftOperand().equalsIgnoreCase(traceabilityProperties.getLeftOperandSecond());
                    return hasFirstConstraint || hasSecondConstraint;
                })
                .map(Map.Entry::getKey)
                .findFirst();
        return policyId.map(this::getPolicy);
    }

    @Override
    public CreatePolicyResponse createPolicy(RegisterPolicyRequest registerPolicyRequest) {
        return policyRepository.createPolicy(registerPolicyRequest);
    }

    @Override
    public void deletePolicy(String id) {
        policyRepository.deletePolicy(id);
    }

    @Override
    public void updatePolicy(UpdatePolicyRequest updatePolicyRequest) {
        policyRepository.updatePolicy(updatePolicyRequest);
    }


    @NotNull
    private List<IrsPolicyResponse> getAcceptedPoliciesOrEmptyList() {
        return emptyIfNull(policyRepository.getPolicies());
    }
}
