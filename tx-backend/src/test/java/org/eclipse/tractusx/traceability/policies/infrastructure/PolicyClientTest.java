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

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyClientTest {

    @Mock
    TraceabilityProperties traceabilityProperties;

    @Mock
    RestTemplate irsAdminTemplate;

    @InjectMocks
    PolicyClient policyClient;

    @Test
    void test_CreatePolicyFromAppConfig() {
        //GIVEN
        ReflectionTestUtils.setField(policyClient, "policiesPath", "policiesPath");
        when(traceabilityProperties.getValidUntil()).thenReturn(OffsetDateTime.MAX);
        when(traceabilityProperties.getLeftOperand()).thenReturn("operandLeft");
        when(traceabilityProperties.getLeftOperandSecond()).thenReturn("operandLeftSecond");
        when(traceabilityProperties.getRightOperand()).thenReturn("operandRight");
        when(traceabilityProperties.getRightOperandSecond()).thenReturn("operandRightSecond");
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("bpn"));

        //WHEN
        policyClient.createPolicyFromAppConfig();

        //THEN
        verify(irsAdminTemplate, times(1)).exchange(eq("policiesPath"), any(), any(), eq(Void.class));
        verify(traceabilityProperties, times(1)).getValidUntil();
        verify(traceabilityProperties, times(1)).getLeftOperand();
        verify(traceabilityProperties, times(1)).getLeftOperandSecond();
        verify(traceabilityProperties, times(1)).getRightOperand();
        verify(traceabilityProperties, times(1)).getRightOperandSecond();
        verify(traceabilityProperties, times(1)).getBpn();
    }
}
