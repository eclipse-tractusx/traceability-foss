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


import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.traceability.assets.domain.base.IrsRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Payload;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.PolicyResponse;
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
    private IrsRepository irsRepository;

    @BeforeEach
    public void testSetup() {
        policyService = new PolicyServiceImpl(irsRepository);
    }


    @Test
    void testGetPolicyByID() {


        // GIVEN
        String policyId = "policy123";
        OffsetDateTime createdOn = OffsetDateTime.parse("2023-07-03T16:01:05.309Z");
        List<PolicyResponse> acceptedPolicies = List.of(
                PolicyResponse.builder()
                        .validUntil(OffsetDateTime.now())
                        .payload(
                                Payload.builder()
                                        .policyId(policyId)
                                        .policy(
                                                Policy.builder()
                                                        .createdOn(createdOn)
                                                        .build())
                                        .build())
                        .build());

        // WHEN
        when(irsRepository.getPolicies()).thenReturn(acceptedPolicies);
        List<PolicyResponse> allPolicies = policyService.getAllPolicies();

        // THEN
        assertNotNull(allPolicies);
        assertEquals(policyId, allPolicies.get(0).payload().policyId());
        assertEquals(createdOn, allPolicies.get(0).payload().policy().getCreatedOn());
    }

}
