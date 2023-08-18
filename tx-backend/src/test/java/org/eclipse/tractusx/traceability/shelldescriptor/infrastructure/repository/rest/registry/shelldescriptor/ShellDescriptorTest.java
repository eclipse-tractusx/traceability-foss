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

package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor;

import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShellDescriptorTest {

    @Test
    void testFromGlobalAssetId() {
        String globalAssetId = "ABC123";
        ShellDescriptor descriptor = ShellDescriptor.fromGlobalAssetId(globalAssetId);

        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getGlobalAssetId()).isEqualTo(globalAssetId);
    }

    @Test
    void testFromGlobalAssetIds() {
        List<String> globalAssetIds = Arrays.asList("ABC123", "DEF456", "GHI789");
        List<ShellDescriptor> descriptors = ShellDescriptor.fromGlobalAssetIds(globalAssetIds);

        assertThat(descriptors).isNotNull().hasSize(globalAssetIds.size());

        for (int i = 0; i < globalAssetIds.size(); i++) {
            assertThat(descriptors.get(i)).isNotNull();
            assertThat(descriptors.get(i).getGlobalAssetId()).isEqualTo(globalAssetIds.get(i));
        }
    }
}
