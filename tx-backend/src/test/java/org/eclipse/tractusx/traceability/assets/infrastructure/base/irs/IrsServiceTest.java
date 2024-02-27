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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.irs.edc.client.policy.Operator;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Payload;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.PolicyResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.MapperFactory;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IrsServiceTest {
    private IrsService irsService;

    @Mock
    TraceabilityProperties traceabilityProperties;

    ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    AssetCallbackRepository assetAsBuiltCallbackRepository;

    @Mock
    AssetCallbackRepository assetAsPlannedCallbackRepository;

    @Mock
    private BpnRepository bpnRepository;

    @Mock
    private IrsClient irsClient;

    @Mock
    private MapperFactory mapperFactory;


    @BeforeEach
    void setUp() {
        irsService = new IrsService(irsClient, bpnRepository, traceabilityProperties, objectMapper, assetAsBuiltCallbackRepository, assetAsPlannedCallbackRepository, mapperFactory);
    }


    @Test
    void givenNoPolicyExist_whenCreateIrsPolicyIfMissing_thenCreateIt() {
        // given
        when(irsClient.getPolicies()).thenReturn(Collections.emptyList());
        when(traceabilityProperties.getRightOperand()).thenReturn("test");

        // when
        irsService.createIrsPolicyIfMissing();

        // then
        verify(irsClient, times(1))
                .registerPolicy();
    }

    @Test
    void givenPolicyExist_whenCreateIrsPolicyIfMissing_thenDoNotCreateIt() {
        // given
        OffsetDateTime validUntil = OffsetDateTime.parse("2023-07-03T16:01:05.309Z");
        OffsetDateTime createdOn = OffsetDateTime.now();
        Policy policy = new Policy("1", createdOn, validUntil, List.of(new Permission(PolicyType.USE, new Constraints(List.of(), List.of(new Constraint("leftOperand1", new Operator(OperatorType.EQ), "test"))))));
        Payload payload = new Payload(null, "1", policy);
        final PolicyResponse existingPolicy = new PolicyResponse(validUntil, payload);
        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));
        when(traceabilityProperties.getRightOperand()).thenReturn("test");
        when(traceabilityProperties.getValidUntil()).thenReturn(OffsetDateTime.parse("2023-07-02T16:01:05.309Z"));

        // when
        irsService.createIrsPolicyIfMissing();

        // then
        verifyNoMoreInteractions(irsClient);
    }

    @Test
    void givenOutdatedPolicyExist_whenCreateIrsPolicyIfMissing_thenUpdateIt() {
        // given

        OffsetDateTime validUntil = OffsetDateTime.parse("2023-07-03T16:01:05.309Z");
        OffsetDateTime createdOn = OffsetDateTime.now();
        Policy policy = new Policy("test", createdOn, validUntil, List.of(new Permission(PolicyType.USE, new Constraints(List.of(), List.of(new Constraint("leftOperand1", new Operator(OperatorType.EQ), "test"))))));
        Payload payload = new Payload(null, "test", policy);

        final PolicyResponse existingPolicy = new PolicyResponse(validUntil, payload);
        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));
        when(traceabilityProperties.getRightOperand()).thenReturn("test");
        when(traceabilityProperties.getValidUntil()).thenReturn(OffsetDateTime.parse("2023-07-04T16:01:05.309Z"));

        // when
        irsService.createIrsPolicyIfMissing();

        // then
        verify(irsClient, times(1)).deletePolicy();
        verify(irsClient, times(1)).registerPolicy();
    }

    @ParameterizedTest
    @MethodSource("provideDirections")
    void testFindAssets_completedJob_returnsConvertedAssets(Direction direction) {
        // Given
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("test"));

        // When
        irsService.createJobToResolveAssets("1", direction, Aspect.downwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT);

        // Then
        verify(irsClient, times(1)).registerJob(any(RegisterJobRequest.class));
    }

    private static Stream<Arguments> provideDirections() {
        return Stream.of(
                Arguments.of(Direction.DOWNWARD),
                Arguments.of(Direction.UPWARD)
        );
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

        final PolicyResponse existingPolicy = new PolicyResponse(validUntil, payload);


        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));

        //WHEN
        List<PolicyResponse> policyResponse = irsClient.getPolicies();

        //THEN
        assertThat(policyResponse).hasSize(1);
    }
}
