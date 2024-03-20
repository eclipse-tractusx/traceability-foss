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
import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.irs.edc.client.policy.Operator;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;
import org.eclipse.tractusx.traceability.assets.domain.base.PolicyRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsPolicyResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyServiceImplTest {
    @InjectMocks
    private PolicyServiceImpl policyService;
    @Mock
    private PolicyRepository policyRepository;


    @Test
    void testGetPolicyByID() {


        // GIVEN
        String policyId = "policy123";
        OffsetDateTime createdOn = OffsetDateTime.parse("2023-07-03T16:01:05.309Z");
        List<IrsPolicyResponse> acceptedPolicies = List.of(
                IrsPolicyResponse.builder()
                        .validUntil(OffsetDateTime.now())
                        .payload(
                                Payload.builder()
                                        .policyId(policyId)
                                        .policy(
                                                Policy.builder()
                                                        .createdOn(createdOn)
                                                        .permissions(List.of(
                                                                Permission.builder()
                                                                        .action(PolicyType.USE)
                                                                        .constraint(Constraints.builder()
                                                                                .and(List.of(new Constraint("", new Operator(OperatorType.EQ), "")))
                                                                                .or(List.of(new Constraint("", new Operator(OperatorType.EQ), "")))
                                                                                .build())
                                                                        .build()))
                                                        .build())
                                        .build())
                        .build());

        // WHEN
        when(policyRepository.getPolicies()).thenReturn(acceptedPolicies);
        List<PolicyResponse> allPolicies = policyService.getAllPolicies();

        // THEN
        assertNotNull(allPolicies);
        assertEquals(policyId, allPolicies.get(0).policyId());
        assertEquals(createdOn, allPolicies.get(0).createdOn());
    }

}
