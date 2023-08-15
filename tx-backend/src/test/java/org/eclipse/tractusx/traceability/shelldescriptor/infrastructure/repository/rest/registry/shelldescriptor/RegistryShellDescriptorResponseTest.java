/********************************************************************************
 * Copyright (c) 2021,2022,2023
 *       2022: ZF Friedrichshafen AG
 *       2022: ISTOS GmbH
 *       2022,2023: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *       2022,2023: BOSCH AG
 * Copyright (c) 2021,2022,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0. *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor;

import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.component.assetadministrationshell.IdentifierKeyValuePair;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.model.RegistryShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.model.RegistryShellDescriptorResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegistryShellDescriptorResponseTest {

    @Test
    void givenRegistryShellDescriptorResponse_whenFromCollection_thenCorrectMapping() {
        // given
        final String globalAssetId = "GLOBAL_ASSET_ID";
        final String idShort = "ID_SHORT";
        final String identification = "IDENTIFICATION";
        final String keyIdentifier = "KEY_IDENTIFIER";
        final String valueIdentifier = "VALUE_IDENTIFIER";
        IdentifierKeyValuePair identifierKeyValuePair = IdentifierKeyValuePair.builder().name(keyIdentifier).value(valueIdentifier).build();
        final AssetAdministrationShellDescriptor assetAdministrationShellDescriptor = AssetAdministrationShellDescriptor.builder()
                .globalAssetId(globalAssetId)
                .idShort(idShort)
                .id(identification)
                .specificAssetIds(List.of(identifierKeyValuePair))
                .build();
        final RegistryShellDescriptor expectedDescriptor = RegistryShellDescriptor.from(assetAdministrationShellDescriptor);

        // when
        final RegistryShellDescriptorResponse result = RegistryShellDescriptorResponse.fromCollection(List.of(assetAdministrationShellDescriptor));

        // then
        assertThat(result.items())
                .hasSize(1)
                .containsExactly(expectedDescriptor);
        assertThat(result).isEqualTo(RegistryShellDescriptorResponse.builder().items(List.of(expectedDescriptor)).build());
        assertThat(result.toString()).hasToString("RegistryShellDescriptorResponse[" +
                "items=" + List.of(expectedDescriptor) + ']');
    }
}
