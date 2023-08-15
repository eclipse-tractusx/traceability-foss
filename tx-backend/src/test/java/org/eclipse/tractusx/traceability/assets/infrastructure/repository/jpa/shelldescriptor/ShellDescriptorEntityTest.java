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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.jpa.shelldescriptor;

import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.model.ShellDescriptorEntity;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ShellDescriptorEntityTest {

    @Test
    void givenEntity_whenToShellDescriptor_themConvertCorrectly() {
        // given
        final ZonedDateTime now = ZonedDateTime.now();
        final ShellDescriptorEntity entity = ShellDescriptorEntity.builder()
                .id(1L)
                .created(now)
                .updated(now)
                .shellDescriptorId("shellDescriptorId")
                .globalAssetId("globalAssetId")
                .idShort("idShort")
                .partInstanceId("partInstanceId")
                .manufacturerPartId("manufacturerPartId")
                .batchId("batchId")
                .manufacturerId("manufacturerId")
                .build();

        // when
        final ShellDescriptor result = entity.toShellDescriptor();

        // then
        assertThat(result)
                .hasFieldOrPropertyWithValue("shellDescriptorId", entity.getShellDescriptorId())
                .hasFieldOrPropertyWithValue("globalAssetId", entity.getGlobalAssetId())
                .hasFieldOrPropertyWithValue("idShort", entity.getIdShort())
                .hasFieldOrPropertyWithValue("partInstanceId", entity.getPartInstanceId())
                .hasFieldOrPropertyWithValue("manufacturerPartId", entity.getManufacturerPartId())
                .hasFieldOrPropertyWithValue("manufacturerId", entity.getManufacturerId())
                .hasFieldOrPropertyWithValue("batchId", entity.getBatchId());
    }

    @Test
    void givenDescriptor_whenNewEntityFrom_thenCreateNewEntity() {
        // given
        ShellDescriptor descriptor = ShellDescriptor.builder()
                .shellDescriptorId("shellDescriptorId")
                .globalAssetId("globalAssetId")
                .idShort("idShort")
                .partInstanceId("partInstanceId")
                .manufacturerPartId("manufacturerPartId")
                .manufacturerId("manufacturerId")
                .batchId("batchId")
                .build();

        // when
        ShellDescriptorEntity result = ShellDescriptorEntity.newEntityFrom(descriptor);

        // then
        assertThat(result).usingRecursiveComparison()
                .comparingOnlyFields(
                        "shellDescriptorId",
                        "globalAssetId",
                        "idShort",
                        "partInstanceId",
                        "manufacturerPartId",
                        "manufacturerId",
                        "batchId");
        assertThat(result).hasNoNullFieldsOrPropertiesExcept("id");
    }
}
