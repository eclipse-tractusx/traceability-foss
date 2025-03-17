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
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterOrderRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsBatchResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.IrsResponseAssetMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_BUILT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;

@Slf4j
@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {
    @InjectMocks
    private OrderRepositoryImpl orderRepositoryImpl;

    @Mock
    TraceabilityProperties traceabilityProperties;

    @Mock
    AssetCallbackRepository assetAsBuiltCallbackRepository;

    @Mock
    AssetCallbackRepository assetAsPlannedCallbackRepository;

    @Mock
    private BpnRepository bpnRepository;

    @Mock
    private OrderClient orderClient;

    @Mock
    private IrsResponseAssetMapper assetMapperFactory;

    @Mock
    private JobClient jobClient;

    @Mock
    private ObjectMapper objectMapper;

    private String orderId;
    private String batchId;

    @BeforeEach
    void setUp(){
        // Generate UUIDs for orderId and batchId
        orderId = UUID.randomUUID().toString();
        batchId = UUID.randomUUID().toString();


        // Spy on the actual orderRepositoryImpl with all dependencies
        orderRepositoryImpl = spy(new OrderRepositoryImpl(orderClient, traceabilityProperties,
                assetAsBuiltCallbackRepository, assetAsPlannedCallbackRepository,
                assetMapperFactory, objectMapper, jobClient));    }

    @ParameterizedTest
    @MethodSource("provideDirections")
    void testFindAssets_completedJob_returnsConvertedAssets(Direction direction) {
        // Given
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("test"));

        // When
        orderRepositoryImpl.createOrderToResolveAssets(List.of("1"), direction, Aspect.downwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT);

        // Then
        verify(orderClient, times(1)).registerOrder(any(RegisterOrderRequest.class));
    }

    private static Stream<Arguments> provideDirections() {
        return Stream.of(
                Arguments.of(Direction.DOWNWARD),
                Arguments.of(Direction.UPWARD)
        );
    }


    @Test
    void testHandleOrderFinishedCallback_withCompletedJob_shouldProcessIt() {
        //Mock
        IrsBatchResponse batchResponse = mock(IrsBatchResponse.class);
        IrsBatchResponse.JobRecord completedJob = mock(IrsBatchResponse.JobRecord.class);
        JobStatus completedJobStatus = mock(JobStatus.class);
        IRSResponse completedJobDetailResponse = mock(IRSResponse.class);
        AssetBase assetBase = mock(AssetBase.class);

        //Given
        ProcessingState batchState = ProcessingState.PARTIAL;
        ProcessingState orderState = ProcessingState.PROCESSING;

        when(orderClient.getBatchByOrder(orderId, batchId)).thenReturn(batchResponse);
        when(batchResponse.jobs()).thenReturn(List.of(completedJob));
        when(completedJob.id()).thenReturn("job-1");
        when(completedJobStatus.state()).thenReturn("COMPLETED");
        when(completedJobDetailResponse.jobStatus()).thenReturn(completedJobStatus);
        when(jobClient.getIrsJobDetailResponse("job-1")).thenReturn(completedJobDetailResponse);
        when(assetBase.getBomLifecycle()).thenReturn(AS_BUILT);
        when(assetBase.getSemanticDataModel()).thenReturn(SemanticDataModel.TOMBSTONEASBUILT);
        when(assetMapperFactory.toAssetBaseList(completedJobDetailResponse)).thenReturn(List.of(assetBase));

        //When
        orderRepositoryImpl.handleOrderFinishedCallback(orderId, batchId, orderState, batchState);

        //Then
        verify(jobClient, times(1)).getIrsJobDetailResponse("job-1");
        verify(assetMapperFactory, times(1)).toAssetBaseList(any());
        verify(orderRepositoryImpl, times(1)).saveOrUpdateAssets(any(), any());
    }

    @Test
    void testHandleOrderFinishedCallback_withNotCompletedJob_shouldIgnoreIt() {
        //Mock
        IrsBatchResponse batchResponse = mock(IrsBatchResponse.class);
        IrsBatchResponse.JobRecord cancelledJob = mock(IrsBatchResponse.JobRecord.class);
        JobStatus errorJobStatus = mock(JobStatus.class);
        IRSResponse errorJobDetailResponse = mock(IRSResponse.class);

        //Given
        ProcessingState batchState = ProcessingState.PARTIAL;
        ProcessingState orderState = ProcessingState.PROCESSING;

        when(orderClient.getBatchByOrder(orderId, batchId)).thenReturn(batchResponse);
        when(batchResponse.jobs()).thenReturn(List.of(cancelledJob));

        when(cancelledJob.id()).thenReturn("job-2");
        when(errorJobStatus.state()).thenReturn("ERROR");
        when(errorJobDetailResponse.jobStatus()).thenReturn(errorJobStatus);
        when(jobClient.getIrsJobDetailResponse("job-2")).thenReturn(errorJobDetailResponse);

        //When
        orderRepositoryImpl.handleOrderFinishedCallback(orderId, batchId, orderState, batchState);

        //Then
        verify(jobClient, times(1)).getIrsJobDetailResponse("job-2");
        verify(assetMapperFactory, times(0)).toAssetBaseList(any()); // Should not process ERROR jobs
        verify(orderRepositoryImpl, times(0)).saveOrUpdateAssets(any(), any()); // Should not save ERROR jobs
    }

    @Test
    void testHandleOrderFinishedCallback_withCanceledJob_shouldIgnoreIt() {
        //Mock
        IrsBatchResponse batchResponse = mock(IrsBatchResponse.class);
        IrsBatchResponse.JobRecord cancelledJob = mock(IrsBatchResponse.JobRecord.class);
        JobStatus errorJobStatus = mock(JobStatus.class);
        IRSResponse errorJobDetailResponse = mock(IRSResponse.class);

        //Given
        ProcessingState batchState = ProcessingState.PARTIAL;
        ProcessingState orderState = ProcessingState.PROCESSING;

        when(orderClient.getBatchByOrder(orderId, batchId)).thenReturn(batchResponse);
        when(batchResponse.jobs()).thenReturn(List.of(cancelledJob));

        when(cancelledJob.id()).thenReturn("job-3");
        when(errorJobStatus.state()).thenReturn("CANCELED");
        when(errorJobDetailResponse.jobStatus()).thenReturn(errorJobStatus);
        when(jobClient.getIrsJobDetailResponse("job-3")).thenReturn(errorJobDetailResponse);

        //When
        orderRepositoryImpl.handleOrderFinishedCallback(orderId, batchId, orderState, batchState);

        //Then
        verify(jobClient, times(1)).getIrsJobDetailResponse("job-3");
        verify(assetMapperFactory, times(0)).toAssetBaseList(any()); // Should not process ERROR jobs
        verify(orderRepositoryImpl, times(0)).saveOrUpdateAssets(any(), any()); // Should not save ERROR jobs
    }

    @Test
    void testHandleOrderFinishedCallback_shouldSkipWhenJobDetailResponseIsNull() {

        // Mock batch response and job record
        IrsBatchResponse batchResponse = mock(IrsBatchResponse.class);
        IrsBatchResponse.JobRecord jobRecord = mock(IrsBatchResponse.JobRecord.class);
        when(batchResponse.jobs()).thenReturn(List.of(jobRecord));

        // Ensure job ID is consistent across stubbing and assertions
        String jobId = "job-4";
        when(jobRecord.id()).thenReturn(jobId);
        when(orderClient.getBatchByOrder(orderId, batchId)).thenReturn(batchResponse);

        // Fix: Make sure the stub matches the actual argument
        when(jobClient.getIrsJobDetailResponse(jobId)).thenReturn(null);

        // When
        orderRepositoryImpl.handleOrderFinishedCallback(orderId, batchId, ProcessingState.PROCESSING, ProcessingState.PARTIAL);

        // Then
        verify(jobClient, times(1)).getIrsJobDetailResponse(jobId);
        verify(assetMapperFactory, times(0)).toAssetBaseList(any()); // Should not process ERROR jobs
        verify(orderRepositoryImpl, times(0)).saveOrUpdateAssets(any(), any()); // Should not save ERROR jobs
    }

    @Test
    void testHandleOrderFinishedCallback_shouldSkipWhenJobStatusIsNull() {
        //Mock
        IrsBatchResponse batchResponse = mock(IrsBatchResponse.class);
        IrsBatchResponse.JobRecord cancelledJob = mock(IrsBatchResponse.JobRecord.class);
        JobStatus errorJobStatus = mock(JobStatus.class);
        IRSResponse errorJobDetailResponse = mock(IRSResponse.class);

        //Given
        ProcessingState batchState = ProcessingState.PARTIAL;
        ProcessingState orderState = ProcessingState.PROCESSING;

        when(orderClient.getBatchByOrder(orderId, batchId)).thenReturn(batchResponse);
        when(batchResponse.jobs()).thenReturn(List.of(cancelledJob));

        when(cancelledJob.id()).thenReturn("job-2");
        when(errorJobStatus.state()).thenReturn(null);
        when(errorJobDetailResponse.jobStatus()).thenReturn(errorJobStatus);
        when(jobClient.getIrsJobDetailResponse("job-2")).thenReturn(errorJobDetailResponse);

        //When
        orderRepositoryImpl.handleOrderFinishedCallback(orderId, batchId, orderState, batchState);

        //Then
        verify(jobClient, times(1)).getIrsJobDetailResponse("job-2");
        verify(assetMapperFactory, times(0)).toAssetBaseList(any()); // Should not process ERROR jobs
        verify(orderRepositoryImpl, times(0)).saveOrUpdateAssets(any(), any()); // Should not save ERROR jobs
    }

}



