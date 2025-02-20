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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.irs.component.ProcessingError;
import org.eclipse.tractusx.irs.component.Tombstone;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Parameter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TombstoneMapperTest {

    private static Stream<Arguments> provideStringsForIsBlank() {
        return Stream.of(
                Arguments.of("id", "asBuilt", "upward"),
                Arguments.of("otherPartCatenaxId", "asBuilt", "upward")

        );
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    void test_MapTombstones_mapOwnPart(String assetCatenaxId, String bomLifeCycle, String direction) {
        //GIVEN
        String ownCatenaxId = "id";
        String bpn = "BPN";

        JobStatus jobStatus = new JobStatus(null, null, null, null, ownCatenaxId, new Parameter(direction, bomLifeCycle, bpn));
        Tombstone tombstone = Tombstone.builder()
                .catenaXId(assetCatenaxId).businessPartnerNumber(bpn).processingError(ProcessingError.builder().withErrorDetail("didn't work :(").build()).build();

        ObjectMapper objectMapper = new ObjectMapper();

        //WHEN
        List<AssetBase> assetBases = TombstoneMapper.mapTombstones(jobStatus, List.of(tombstone), objectMapper,bpn);

        //THEN
        assertNotNull(assetBases.get(0).getTombstone());
    }
    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    void test_MapTombstones_mapOtherPart(String assetCatenaxId, String bomLifeCycle, String direction) {
        //GIVEN
        String ownCatenaxId = "id";
        String ownPartsBpn = "BPN";
        String otherPartsBpn = "XYZ";

        JobStatus jobStatus = new JobStatus(null, null, null, null, ownCatenaxId, new Parameter(direction, bomLifeCycle, otherPartsBpn));
        Tombstone tombstone = Tombstone.builder()
                .catenaXId(assetCatenaxId).businessPartnerNumber(ownPartsBpn).processingError(ProcessingError.builder().withErrorDetail("didn't work :(").build()).build();

        ObjectMapper objectMapper = new ObjectMapper();

        //WHEN
        List<AssetBase> assetBases = TombstoneMapper.mapTombstones(jobStatus, List.of(tombstone), objectMapper,otherPartsBpn);

        //THEN
        assertNotNull(assetBases.get(0).getTombstone());
    }
}
