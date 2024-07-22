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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PolicyNotFoundException;
import org.eclipse.tractusx.traceability.notification.domain.contract.EdcNotificationContractService;
import org.eclipse.tractusx.traceability.policies.application.service.PolicyService;
import org.eclipse.tractusx.traceability.policies.domain.exception.PolicyNotValidException;
import org.springframework.stereotype.Service;
import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;
import policies.response.CreatePolicyResponse;
import policies.response.IrsPolicyResponse;
import policies.response.PolicyResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final EdcNotificationContractService edcNotificationContractService;

    @Override
    public Map<String, List<PolicyResponse>> getIrsPolicies() {
        Map<String, List<IrsPolicyResponse>> policies = policyRepository.getPolicies();
        return policies.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(irsPolicyResponse -> irsPolicyResponse.payload().policy())
                                .collect(Collectors.toList())
                ));
    }

    @Override
    public List<PolicyResponse> getPolicies() {
        Map<String, List<IrsPolicyResponse>> policies = policyRepository.getPolicies();
        return policies.values().stream()
                .flatMap(List::stream)
                .map(irsPolicyResponse -> irsPolicyResponse.payload().policy())
                .collect(Collectors.toList());
    }

    @Override
    public PolicyResponse getPolicy(String id) {
        Map<String, Optional<IrsPolicyResponse>> policies = policyRepository.getPolicy(id);

        return policies.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(irsPolicyResponse -> irsPolicyResponse.payload().policy())
                .findFirst()
                .orElseThrow(() -> new PolicyNotFoundException("Policy with id: %s not found.".formatted(id)));
    }

    @Override
    public CreatePolicyResponse createPolicy(RegisterPolicyRequest registerPolicyRequest) {
        if(registerPolicyRequest.validUntil().isAfter(Instant.now())){
            CreatePolicyResponse policy = policyRepository.createPolicy(registerPolicyRequest);
            edcNotificationContractService.updateNotificationContractDefinitions();
            return policy;    }
        throw new PolicyNotValidException("Policy is expired" +registerPolicyRequest);
    }

    @Override
    public void deletePolicy(String id) {
        policyRepository.deletePolicy(id);
        edcNotificationContractService.updateNotificationContractDefinitions();
    }

    @Override
    public void updatePolicy(UpdatePolicyRequest updatePolicyRequest) {
        if(updatePolicyRequest.validUntil().isAfter(Instant.now())){
        policyRepository.updatePolicy(updatePolicyRequest);
        edcNotificationContractService.updateNotificationContractDefinitions();
        return;
    }
        throw new PolicyNotValidException("Policy is expired" +updatePolicyRequest);
    }


}
