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
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsPolicyResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Payload;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.IrsResponseAssetMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobRepositoryImplTest {
    @InjectMocks
    private JobRepositoryImpl jobRepositoryImpl;

    @Mock
    TraceabilityProperties traceabilityProperties;

    @Mock
    AssetCallbackRepository assetAsBuiltCallbackRepository;

    @Mock
    AssetCallbackRepository assetAsPlannedCallbackRepository;

    @Mock
    private BpnRepository bpnRepository;

    @Mock
    private IrsClient irsClient;

    @Mock
    private IrsResponseAssetMapper assetMapperFactory;

    @ParameterizedTest
    @MethodSource("provideDirections")
    void testFindAssets_completedJob_returnsConvertedAssets(Direction direction) {
        // Given
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("test"));

        // When
        jobRepositoryImpl.createJobToResolveAssets("1", direction, Aspect.downwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT);

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

        final IrsPolicyResponse existingPolicy = new IrsPolicyResponse(validUntil, payload);


        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));

        //WHEN
        List<IrsPolicyResponse> irsPolicyResponse = irsClient.getPolicies();

        //THEN
        assertThat(irsPolicyResponse).hasSize(1);
    }
}
