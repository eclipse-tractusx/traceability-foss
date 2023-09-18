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
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AssetAsBuiltRepositoryIT extends IntegrationTestSpecification {

    @Autowired
    AssetAsBuiltRepository assetAsBuiltRepository;

    @Autowired
    AssetsSupport assetsSupport;

    @ParameterizedTest
    @MethodSource("fieldNameTestProvider")
    void givenIdField_whenGetFieldValues_thenSorted(
            String fieldName,
            Long resultLimit,
            Integer expectedSize
    ) {
        // given
        assetsSupport.defaultAssetsStored();

        // when
        List<String> result = assetAsBuiltRepository.getFieldValues(fieldName, resultLimit);

        // then
        assertThat(result)
                .isSortedAccordingTo(String::compareTo)
                .hasSize(expectedSize);
    }

    private static Stream<Arguments> fieldNameTestProvider() {
        return Stream.of(
                Arguments.of("id", 10L, 10),
                Arguments.of("id", 200L, 13),
                Arguments.of("inInvestigation", 10L, 1),
                Arguments.of("owner", 10L, 2)
        );
    }
}
