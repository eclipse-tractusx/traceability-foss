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

package org.eclipse.tractusx.traceability.integration.assets.asbuilt.infrastructure.repository;

import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AssetAsBuiltRepositoryIT extends IntegrationTestSpecification {

    @Autowired
    AssetAsBuiltRepository assetAsBuiltRepository;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    @ParameterizedTest
    @MethodSource("fieldNameTestProvider")
    void givenIdField_whenGetFieldValues_thenSorted(
            String fieldName,
            List<String> startsWith,
            Integer resultLimit,
            Integer expectedSize
    ) {
        // given
        assetsSupport.defaultAssetsStored();

        // when
        List<String> result = assetAsBuiltRepository.getFieldValues(fieldName, startsWith, resultLimit, null, List.of());

        // then
        assertThat(result)
                .isSortedAccordingTo(String::compareTo)
                .hasSize(expectedSize);
    }

    @Test
    void givenAssets_whenGetByImportStateIn_thenReturnProperAssets() {
        // given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity entityInSyncState = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").get();
        entityInSyncState.setImportState(ImportState.IN_SYNCHRONIZATION);
        AssetAsBuiltEntity entityTransientState = jpaAssetAsBuiltRepository.findById("urn:uuid:6dafbcec-2fce-4cbb-a5a9-b3b32aa5cffc").get();
        entityTransientState.setImportState(ImportState.TRANSIENT);
        jpaAssetAsBuiltRepository.saveAll(List.of(entityInSyncState, entityTransientState));

        // when
        List<AssetBase> result = assetAsBuiltRepository.findByImportStateIn(ImportState.TRANSIENT, ImportState.IN_SYNCHRONIZATION);

        // then
        assertThat(result).hasSize(2);
    }

    private static Stream<Arguments> fieldNameTestProvider() {
        return Stream.of(
                Arguments.of("id", List.of("urn:uuid:1"), 10, 3),
                Arguments.of("id", Collections.EMPTY_LIST, 10, 10),
                Arguments.of("id", Collections.EMPTY_LIST, 200, 13),
                Arguments.of("owner", Collections.EMPTY_LIST, 10, 2)
        );
    }
}
