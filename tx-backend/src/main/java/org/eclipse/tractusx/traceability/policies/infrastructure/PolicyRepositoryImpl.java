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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.policy.AcceptedPoliciesProvider;
import org.eclipse.tractusx.irs.edc.client.policy.AcceptedPolicy;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.traceability.policies.domain.PolicyRepository;
import org.springframework.stereotype.Service;
import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;
import policies.response.CreatePolicyResponse;
import policies.response.IrsPolicyResponse;
import policies.response.PolicyResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class PolicyRepositoryImpl implements PolicyRepository {

    private final PolicyClient policyClient;
    private AcceptedPoliciesProvider.DefaultAcceptedPoliciesProvider defaultAcceptedPoliciesProvider;
    private final ObjectMapper objectMapper;

    @Override
    public Map<String, List<IrsPolicyResponse>> getPolicies() {
        return policyClient.getPolicies();
    }

    @Override
    public List<PolicyResponse> getLatestPoliciesByApplicationBPNOrDefaultPolicy() {
        return policyClient.getPoliciesByApplicationBPNOrDefault();
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


    private void updateAcceptedPoliciesProvider() {
        defaultAcceptedPoliciesProvider.removeAcceptedPolicies(defaultAcceptedPoliciesProvider.getAcceptedPolicies(null));
        // Flatten the map into a list of IrsPolicyResponse objects
        List<IrsPolicyResponse> irsPolicyResponses = getPolicies().values().stream()
                .flatMap(List::stream)
                .toList();

        // Map the IrsPolicyResponse objects to AcceptedPolicy objects
        List<AcceptedPolicy> irsPolicies = irsPolicyResponses.stream().map(response -> {
            Policy policy = new Policy(response.payload().policyId(), response.payload().policy().getCreatedOn(), response.validUntil(), response.payload().policy().getPermissions());
            return new AcceptedPolicy(policy, response.validUntil());
        }).toList();

        defaultAcceptedPoliciesProvider.addAcceptedPolicies(irsPolicies);
        try {
            String irsPoliciesString = objectMapper.writeValueAsString(irsPolicies);
            log.info("Adding the following policies to the client library validator {}", irsPoliciesString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
