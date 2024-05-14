/********************************************************************************
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs;

import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.irs.edc.client.policy.Operator;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsPolicyResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Payload;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyRepositoryImplTest {
    @InjectMocks
    private PolicyRepositoryImpl policyRepositoryImpl;

    @Mock
    TraceabilityProperties traceabilityProperties;


    @Mock
    private IrsClient irsClient;


    @Test
    void givenNoPolicyExist_whenCreateIrsPolicyIfMissing_thenCreateIt() {
        // given
        when(irsClient.getPolicies()).thenReturn(Collections.emptyList());
        when(traceabilityProperties.getRightOperand()).thenReturn("test");

        // when
        policyRepositoryImpl.createIrsPolicyIfMissing();

        // then
        verify(irsClient, times(1))
                .registerPolicy();
    }

    @Test
    void givenPolicyExist_whenCreateIrsPolicyIfMissing_thenDoNotCreateIt() {
        // given
        OffsetDateTime validUntil = OffsetDateTime.parse("2023-07-03T16:01:05.309Z");
        OffsetDateTime createdOn = OffsetDateTime.now();
        Constraint constraint = new Constraint("leftOperand1", new Operator(OperatorType.EQ), "test");
        Constraint constraintSecond = new Constraint("leftOperand2", new Operator(OperatorType.EQ), "test2");
        Policy policy = new Policy("1", createdOn, validUntil, List.of(new Permission(PolicyType.USE, new Constraints(List.of(constraint, constraintSecond), List.of()))));
        Payload payload = new Payload(null, "1", policy);
        final IrsPolicyResponse existingPolicy = new IrsPolicyResponse(validUntil, payload);
        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));
        when(traceabilityProperties.getRightOperand()).thenReturn("test");
        when(traceabilityProperties.getRightOperandSecond()).thenReturn("test2");
        when(traceabilityProperties.getValidUntil()).thenReturn(OffsetDateTime.parse("2023-07-02T16:01:05.309Z"));

        // when
        policyRepositoryImpl.createIrsPolicyIfMissing();

        // then
        verifyNoMoreInteractions(irsClient);
    }

    @Test
    void givenOutdatedPolicyExist_whenCreateIrsPolicyIfMissing_thenUpdateIt() {
        // given

        OffsetDateTime validUntil = OffsetDateTime.parse("2023-07-03T16:01:05.309Z");
        OffsetDateTime createdOn = OffsetDateTime.now();
        Constraint constraint = new Constraint("leftOperand1", new Operator(OperatorType.EQ), "test");
        Constraint constraintSecond = new Constraint("leftOperand2", new Operator(OperatorType.EQ), "test2");
        Policy policy = new Policy("test", createdOn, validUntil, List.of(new Permission(PolicyType.USE, new Constraints(List.of(constraint, constraintSecond), List.of()))));
        Payload payload = new Payload(null, "test", policy);

        final IrsPolicyResponse existingPolicy = new IrsPolicyResponse(validUntil, payload);
        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));
        when(traceabilityProperties.getRightOperand()).thenReturn("test");
        when(traceabilityProperties.getRightOperandSecond()).thenReturn("test2");

        when(traceabilityProperties.getValidUntil()).thenReturn(OffsetDateTime.parse("2023-07-04T16:01:05.309Z"));

        // when
        policyRepositoryImpl.createIrsPolicyIfMissing();

        // then
        verify(irsClient, times(1)).deletePolicy();
        verify(irsClient, times(1)).registerPolicy();
    }


    @Test
    void test_getPolicyConstraints() {
        //GIVEN

        OffsetDateTime validUntil = OffsetDateTime.now();
        OffsetDateTime createdOn = OffsetDateTime.now();
        List<Constraint> andConstraints = List.of(new Constraint("leftOperand", new Operator(OperatorType.EQ), "rightOperand"));
        List<Constraint> orConstraints = List.of(new Constraint("leftOperand", new Operator(OperatorType.EQ), "rightOperand"));
        Constraints constraints = new Constraints(andConstraints, orConstraints);

        Policy policy = new Policy("test", createdOn, validUntil, List.of(new Permission(PolicyType.USE, constraints)));
        Payload payload = new Payload(null, "test", policy);

        final IrsPolicyResponse existingPolicy = new IrsPolicyResponse(validUntil, payload);


        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));

        //WHEN
        List<IrsPolicyResponse> irsPolicyResponse = irsClient.getPolicies();

        //THEN
        assertThat(irsPolicyResponse).hasSize(1);
    }
}
