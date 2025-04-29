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


import assets.request.PartChainIdentificationKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.aas.domain.repository.AASRepository;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterOrderRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.RegisterOrderResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.IrsResponseAssetMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.BatchEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderJPARepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.TriggerConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {
    @InjectMocks
    private OrderRepositoryImpl orderRepositoryImpl;

    @Mock
    TraceabilityProperties traceabilityProperties;

    @Mock
    AssetAsBuiltRepository assetAsBuiltRepository;

    @Mock
    AssetAsPlannedRepository assetAsPlannedRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private IrsResponseAssetMapper assetMapperFactory;

    @Mock
    private JobClient jobClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderJPARepository orderJPARepository;

    @Mock
    private OrderConfiguration orderConfiguration;

    @Mock
    private AASRepository aasRepository;

    @Mock
    private TriggerConfigurationRepository triggerConfigurationRepository;

    @Captor
    private ArgumentCaptor<OrderEntity> orderEntityArgumentCaptor;

    private String orderId;
    private String batchId;

    @BeforeEach
    void setUp() {
        // Generate UUIDs for orderId and batchId
        orderId = UUID.randomUUID().toString();
        batchId = UUID.randomUUID().toString();

        // Spy on the actual orderRepositoryImpl with all dependencies
        orderRepositoryImpl = spy(new OrderRepositoryImpl(orderClient, traceabilityProperties,
                assetAsBuiltRepository, assetAsPlannedRepository,
                assetMapperFactory, objectMapper, jobClient, orderJPARepository, aasRepository, triggerConfigurationRepository));
    }

    @ParameterizedTest
    @MethodSource("provideDirections")
    void testFindAssets_completedJob_returnsConvertedAssets(Direction direction) {
        // Given
        when(orderClient.registerOrder(any())).thenReturn(new RegisterOrderResponse("id"));
        when(orderConfiguration.getBatchSize()).thenReturn(50);
        when(orderConfiguration.getJobTimeoutMs()).thenReturn(5000);
        when(orderConfiguration.getTimeoutMs()).thenReturn(7000);

        // When
        orderRepositoryImpl.createOrderToResolveAssets(List.of(new PartChainIdentificationKey(null, null, null)), direction, Aspect.downwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT, orderConfiguration);

        // Then
        verify(orderClient).registerOrder(any(RegisterOrderRequest.class));
    }

    private static Stream<Arguments> provideDirections() {
        return Stream.of(
                Arguments.of(Direction.DOWNWARD),
                Arguments.of(Direction.UPWARD)
        );
    }

    @Test
    void testHandleOrderFinishedCallback_withCompletedJob_shouldProcessIt() {

        //Given
        ProcessingState batchState = ProcessingState.PARTIAL;
        ProcessingState orderState = ProcessingState.PROCESSING;

        BatchEntity batch = BatchEntity.builder().id(batchId).status(batchState).build();
        OrderEntity entity = OrderEntity.builder().id(orderId).batches(List.of(batch)).build();
        when(orderJPARepository.findById(eq(orderId))).thenReturn(Optional.of(entity));

        //When
        orderRepositoryImpl.handleOrderFinishedCallback(orderId, batchId, orderState, batchState);

        //Then
        verify(orderJPARepository).save(orderEntityArgumentCaptor.capture());

        OrderEntity orderEntity = orderEntityArgumentCaptor.getValue();
        assertThat(orderEntity.getStatus()).isEqualTo(ProcessingState.PROCESSING);
    }


}



