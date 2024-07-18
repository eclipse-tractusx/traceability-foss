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
package org.eclipse.tractusx.traceability.policies.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import policies.response.IrsPolicyResponse;
import policies.response.PolicyResponse;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.eclipse.tractusx.traceability.testdata.PolicyTestDataFactory.createIrsPolicyResponse;
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
    void testGetPolicy() {
        // GIVEN
        String policyId = "policy123";
        OffsetDateTime createdOn = OffsetDateTime.parse("2023-07-03T16:01:05.309Z");
        IrsPolicyResponse irsPolicyResponse = createIrsPolicyResponse(policyId, createdOn, "", "", "", "");
        Map<String, List<IrsPolicyResponse>> acceptedPolicies = Map.of("key", List.of(irsPolicyResponse));

        // WHEN
        when(policyRepository.getPolicies()).thenReturn(acceptedPolicies);
        List<PolicyResponse> allPolicies = policyService.getPolicies();

        // THEN
        assertNotNull(allPolicies);
        assertEquals(policyId, allPolicies.get(0).policyId());
        assertEquals(createdOn, allPolicies.get(0).createdOn());
    }
}
