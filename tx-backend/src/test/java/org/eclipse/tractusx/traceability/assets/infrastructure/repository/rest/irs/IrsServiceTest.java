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

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.IRSApiClient;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.IrsService;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.config.IrsPolicyConfig;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Parameter;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.PolicyResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.RegisterJobResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Shell;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.LinkedItem;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Relationship;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.ManufacturingInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.MeasurementUnit;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.PartTypeInformation;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.Quantity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.Site;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.ValidityPeriod;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.IrsPolicy;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.date.DateUtil.toOffsetDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    private IrsPolicyConfig irsPolicyConfig;


    @Test
    void givenNoPolicyExist_whenCreateIrsPolicyIfMissing_thenCreateIt() {
        // given
        final IrsPolicy policyToCreate = IrsPolicy.builder()
                .policyId("test")
                .ttl("2023-07-03T16:01:05.309Z")
                .build();
        when(irsClient.getPolicies()).thenReturn(List.of());
        when(irsPolicyConfig.getPolicies()).thenReturn(List.of(policyToCreate));

        // when
        irsService.createIrsPolicyIfMissing();

        // then
        verify(irsClient, times(1))
                .registerPolicy(any());
    }

    @Test
    void givenPolicyExist_whenCreateIrsPolicyIfMissing_thenDoNotCreateIt() {
        // given
        final IrsPolicy policyToCreate = IrsPolicy.builder()
                .policyId("test")
                .ttl("2023-07-03T16:01:05.309Z")
                .build();
        final PolicyResponse existingPolicy = new PolicyResponse("test", OffsetDateTime.parse("2023-07-03T16:01:05.309Z"), OffsetDateTime.now(), List.of());
        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));
        when(irsPolicyConfig.getPolicies()).thenReturn(List.of(policyToCreate));

        // when
        irsService.createIrsPolicyIfMissing();

        // then
        verifyNoMoreInteractions(irsClient);
    }

    @Test
    void givenOutdatedPolicyExist_whenCreateIrsPolicyIfMissing_thenUpdateIt() {
        // given
        final IrsPolicy policyToCreate = IrsPolicy.builder()
                .policyId("test")
                .ttl("2123-07-03T16:01:05.309Z")
                .build();
        final PolicyResponse existingPolicy = new PolicyResponse("test", OffsetDateTime.parse("2023-07-03T16:01:05.309Z"), OffsetDateTime.now(), List.of());
        when(irsClient.getPolicies()).thenReturn(List.of(existingPolicy));
        when(irsPolicyConfig.getPolicies()).thenReturn(List.of(policyToCreate));

        // when
        irsService.createIrsPolicyIfMissing();

        // then
        verify(irsClient, times(1)).deletePolicy("test");
        verify(irsClient, times(1)).registerPolicy(any());
    }

    @ParameterizedTest
    @MethodSource("provideDirections")
    void testFindAssets_completedJob_returnsConvertedAssets(Direction direction) {

        RegisterJobResponse jobId = new RegisterJobResponse("123");
        // Given
        when(irsClient.registerJob(any(RegisterJobRequest.class))).thenReturn(jobId);
        JobDetailResponse jobResponse = provideTestJobResponse(direction.name());
        when(irsClient.getJobDetails(jobId.id())).thenReturn(jobResponse);

        // When
        List<AssetBase> result = irsService.findAssets("1", direction, Aspect.downwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT);

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
        List<AssetBase> result = irsService.findAssets("1", direction, Aspect.downwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT);

        // Then
        assertThat(result).isEqualTo(Collections.EMPTY_LIST);

    }

    private JobDetailResponse provideTestJobResponse(String direction) {
        JobStatus jobStatus = new JobStatus(
                "id",
                "COMPLETED",
                new Date(),
                new Date(),
                "globalAsset123",
                new Parameter(direction, "asBuilt")
        );

        List<Shell> shells = Arrays.asList(
                new Shell("shell1", "Identification 1", "globalAssetId"),
                new Shell("shell2", "Identification 2", "globalAssetId")
        );

        ValidityPeriod validityPeriod = new ValidityPeriod(null, toOffsetDateTime(Instant.now()));
        Site site = new Site(toOffsetDateTime(Instant.now()), OffsetDateTime.now(), "function", "cxid");
        List<SemanticDataModel> semanticDataModels = Collections.singletonList(
                new SemanticDataModel(
                        "catenaXId123",
                        new PartTypeInformation("classification", "Name at Manufacturer", "Name at Customer",
                                "ManufacturerPartId123", "CustomerPartId123"),
                        new ManufacturingInformation("Country", toOffsetDateTime(Instant.now())),
                        Collections.emptyList(), validityPeriod, List.of(site), "urn:bamm:io.catenax.serial_part:1.0.0#SerialPart"

                )
        );
        List<Relationship> relationships;

        MeasurementUnit measurementUnit = new MeasurementUnit("uri", "unit:abc");
        Quantity quantity = new Quantity(1.5, measurementUnit);
        if (direction.equals(Direction.DOWNWARD.name())) {
            relationships = Arrays.asList(
                    new Relationship("catenaXId123", new LinkedItem("childCatenaXId123", new Date(), new Date(), validityPeriod, quantity), Aspect.SINGLE_LEVEL_BOM_AS_BUILT),
                    new Relationship("catenaXId456", new LinkedItem("childCatenaXId456", new Date(), new Date(), validityPeriod, quantity), Aspect.SINGLE_LEVEL_BOM_AS_BUILT)
            );
        } else {
            relationships = Arrays.asList(
                    new Relationship("catenaXId123", new LinkedItem("childCatenaXId123", new Date(), new Date(), validityPeriod, quantity), Aspect.SINGLE_LEVEL_USAGE_AS_BUILT),
                    new Relationship("catenaXId456", new LinkedItem("childCatenaXId456", new Date(), new Date(), validityPeriod, quantity), Aspect.SINGLE_LEVEL_USAGE_AS_BUILT)
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
