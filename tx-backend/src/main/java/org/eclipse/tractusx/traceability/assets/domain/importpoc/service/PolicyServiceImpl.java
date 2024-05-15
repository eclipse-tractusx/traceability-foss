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
package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import assets.importpoc.PolicyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.application.importpoc.PolicyService;
import org.eclipse.tractusx.traceability.assets.domain.base.PolicyRepository;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PolicyNotFoundException;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsPolicyResponse;
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
    public List<PolicyResponse> getAllPolicies() {
        return IrsPolicyResponse.toResponse(getAcceptedPoliciesOrEmptyList());
    }

    @Override
    public PolicyResponse getPolicyById(String id) {
        return getAcceptedPoliciesOrEmptyList().stream()
                .filter(policy -> policy.payload().policy().getPolicyId().equals(id)).findFirst()
                .map(IrsPolicyResponse::toResponse)
                .orElseThrow(() -> new PolicyNotFoundException("Policy with id: %s not found.".formatted(id)));
    }

    @Override
    public Optional<PolicyResponse> getFirstPolicyMatchingApplicationConstraint() {
        Optional<String> policyId = getAllPolicies().stream()
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
        return policyId.map(this::getPolicyById);
    }


    @NotNull
    private List<IrsPolicyResponse> getAcceptedPoliciesOrEmptyList() {
        return emptyIfNull(policyRepository.getPolicies());
    }
}
