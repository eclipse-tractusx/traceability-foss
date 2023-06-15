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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.IRSApiClient;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.IrsService;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Parameter;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.RegisterJobResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Shell;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.LinkedItem;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Relationship;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.ManufacturingInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.PartTypeInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IrsServiceTest {
    @InjectMocks
    private IrsService irsService;

    @Mock
    private IRSApiClient irsClient;

    @Mock
    private BpnRepository bpnRepository;


    @ParameterizedTest
    @MethodSource("provideDirections")
    void testFindAssets_completedJob_returnsConvertedAssets(Direction direction) {

        RegisterJobResponse jobId = new RegisterJobResponse("123");
        // Given
        when(irsClient.registerJob(any(RegisterJobRequest.class))).thenReturn(jobId);
        JobDetailResponse jobResponse = provideTestJobResponse(direction.name());
        when(irsClient.getJobDetails(jobId.id())).thenReturn(jobResponse);

        // When
        List<Asset> result = irsService.findAssets("1", direction, Aspect.downwardAspects());

        // Then
        assertThat(result).hasSize(1);
        Owner expected;
        if (direction.equals(Direction.DOWNWARD)) {
            expected = Owner.SUPPLIER;
        } else {
            expected = Owner.CUSTOMER;
        }
        assertThat(result.get(0).getOwner()).isEqualTo(expected);
    }

    private static Stream<Arguments> provideDirections() {
        return Stream.of(
                Arguments.of(Direction.DOWNWARD),
                Arguments.of(Direction.UPWARD)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDirections")
    void testFindAssets_uncompletedJob_returnsEmptyListOfAssets(Direction direction) {

        // Given
        RegisterJobResponse startJobResponse = mock(RegisterJobResponse.class);
        when(irsClient.registerJob(any(RegisterJobRequest.class))).thenReturn(startJobResponse);
        JobDetailResponse jobResponse = mock(JobDetailResponse.class);
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

    }

    private JobDetailResponse provideTestJobResponse(String direction) {
        JobStatus jobStatus = new JobStatus(
                "COMPLETED",
                new Date(),
                new Date(),
                "globalAsset123",
                new Parameter(direction)
        );

        List<Shell> shells = Arrays.asList(
                new Shell("shell1", "Identification 1"),
                new Shell("shell2", "Identification 2")
        );

        List<SemanticDataModel> semanticDataModels = Collections.singletonList(
                new SemanticDataModel(
                        "catenaXId123",
                        new PartTypeInformation("Name at Manufacturer", "Name at Customer",
                                "ManufacturerPartId123", "CustomerPartId123"),
                        new ManufacturingInformation("Country", new Date()),
                        Collections.emptyList()
                )
        );
        List<Relationship> relationships;
        if (direction.equals(Direction.DOWNWARD.name())) {
            relationships = Arrays.asList(
                    new Relationship("catenaXId123", new LinkedItem("childCatenaXId123"), Aspect.ASSEMBLY_PART_RELATIONSHIP),
                    new Relationship("catenaXId456", new LinkedItem("childCatenaXId456"), Aspect.ASSEMBLY_PART_RELATIONSHIP)
            );
        } else {
            relationships = Arrays.asList(
                    new Relationship("catenaXId123", new LinkedItem("childCatenaXId123"), Aspect.SINGLE_LEVEL_USAGE_AS_BUILT),
                    new Relationship("catenaXId456", new LinkedItem("childCatenaXId456"), Aspect.SINGLE_LEVEL_USAGE_AS_BUILT)
            );
        }


        Map<String, String> bpns = new HashMap<>();
        bpns.put("key1", "value1");
        bpns.put("key2", "value2");

        return new JobDetailResponse(
                jobStatus,
                shells,
                semanticDataModels,
                relationships,
                bpns
        );

    }


}
