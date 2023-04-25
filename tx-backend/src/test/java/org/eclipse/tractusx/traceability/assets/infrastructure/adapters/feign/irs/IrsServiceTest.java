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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.JobResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.StartJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.StartJobResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IrsServiceTest {
    @InjectMocks
    private IrsService irsService;

    @Mock
    private IRSApiClient irsClient;

    @Mock
    private BpnRepository bpnRepository;
    @Mock
    private AssetsConverter assetsConverter;

    @ParameterizedTest
    @MethodSource("provideDirections")
    void testFindAssetsDownward_completedJob_returnsConvertedAssets(Direction direction) {

        // Given
        StartJobResponse startJobResponse = mock(StartJobResponse.class);
        when(irsClient.registerJob(any(StartJobRequest.class))).thenReturn(startJobResponse);
        JobResponse jobResponse = mock(JobResponse.class);
        when(irsClient.getJobDetails(startJobResponse.id())).thenReturn(jobResponse);
        JobStatus jobStatus = mock(JobStatus.class);
        when(jobResponse.jobStatus()).thenReturn(jobStatus);
        when(jobStatus.lastModifiedOn()).thenReturn(new Date());
        when(jobStatus.startedOn()).thenReturn(new Date());
        when(jobResponse.isCompleted()).thenReturn(true);
        Asset asset = mock(Asset.class);
        List<Asset> expectedAssets = List.of(asset);
        when(assetsConverter.convertAssets(jobResponse)).thenReturn(expectedAssets);

        // When
        List<Asset> result = irsService.findAssets("1", direction, Aspect.downwardAspects());

        // Then
        assertThat(result).isEqualTo(expectedAssets);

    }

    private static Stream<Arguments> provideDirections() {
        return Stream.of(
                Arguments.of(Direction.DOWNWARD),
                Arguments.of(Direction.UPWARD)
        );
    };

    @ParameterizedTest
    @MethodSource("provideDirections")
    void testFindAssetsDownward_uncompletedJob_returnsEmptyListOfAssets(Direction direction) {

        // Given
        StartJobResponse startJobResponse = mock(StartJobResponse.class);
        when(irsClient.registerJob(any(StartJobRequest.class))).thenReturn(startJobResponse);
        JobResponse jobResponse = mock(JobResponse.class);
        when(irsClient.getJobDetails(startJobResponse.id())).thenReturn(jobResponse);
        JobStatus jobStatus = mock(JobStatus.class);
        when(jobResponse.jobStatus()).thenReturn(jobStatus);
        when(jobStatus.lastModifiedOn()).thenReturn(new Date());
        when(jobStatus.startedOn()).thenReturn(new Date());
        when(jobResponse.isCompleted()).thenReturn(false);

        // When
        List<Asset> result = irsService.findAssets("1", direction, Aspect.downwardAspects());

        // Then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);
        Mockito.verify(assetsConverter, never()).convertAssets(any(JobResponse.class));

    }

    @Test
    void testFindAssetsUpward_completedJob_returnsConvertedAssets() {

        // Given
        StartJobResponse startJobResponse = mock(StartJobResponse.class);
        when(irsClient.registerJob(any(StartJobRequest.class))).thenReturn(startJobResponse);
        JobResponse jobResponse = mock(JobResponse.class);
        when(irsClient.getJobDetails(startJobResponse.id())).thenReturn(jobResponse);
        JobStatus jobStatus = mock(JobStatus.class);
        when(jobResponse.jobStatus()).thenReturn(jobStatus);
        when(jobStatus.lastModifiedOn()).thenReturn(new Date());
        when(jobStatus.startedOn()).thenReturn(new Date());
        when(jobResponse.isCompleted()).thenReturn(true);
        Asset asset = mock(Asset.class);
        List<Asset> expectedAssets = List.of(asset);
        when(assetsConverter.convertAssets(jobResponse)).thenReturn(expectedAssets);

        // When
        List<Asset> result = irsService.findAssets("1", Direction.UPWARD, Aspect.upwardAspects());

        // Then
        assertThat(result).isEqualTo(expectedAssets);

    }

    @Test
    void testFindAssetsUpward_uncompletedJob_returnsEmptyListOfAssets() {

        // Given
        StartJobResponse startJobResponse = mock(StartJobResponse.class);
        when(irsClient.registerJob(any(StartJobRequest.class))).thenReturn(startJobResponse);
        JobResponse jobResponse = mock(JobResponse.class);
        when(irsClient.getJobDetails(startJobResponse.id())).thenReturn(jobResponse);
        JobStatus jobStatus = mock(JobStatus.class);
        when(jobResponse.jobStatus()).thenReturn(jobStatus);
        when(jobStatus.lastModifiedOn()).thenReturn(new Date());
        when(jobStatus.startedOn()).thenReturn(new Date());
        when(jobResponse.isCompleted()).thenReturn(false);

        // When
        List<Asset> result = irsService.findAssets("1", Direction.UPWARD, Aspect.upwardAspects());

        // Then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);
        Mockito.verify(assetsConverter, never()).convertAssets(any(JobResponse.class));

    }


}
