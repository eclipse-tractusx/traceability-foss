/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "shell_descriptor")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ShellDescriptorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private ZonedDateTime created;
    private ZonedDateTime updated;
    private String shellDescriptorId;
    private String globalAssetId;
    private String idShort;
    private String partInstanceId;
    private String manufacturerPartId;
    private String batchId;
    private String manufacturerId;

    public static ShellDescriptorEntity newEntityFrom(final ShellDescriptor descriptor) {
        ZonedDateTime now = ZonedDateTime.now();
        return ShellDescriptorEntity.builder()
                .id(null)
                .created(now)
                .updated(now)
                .shellDescriptorId(descriptor.getShellDescriptorId())
                .globalAssetId(descriptor.getGlobalAssetId())
                .idShort(descriptor.getIdShort())
                .partInstanceId(descriptor.getPartInstanceId())
                .manufacturerPartId(descriptor.getManufacturerPartId())
                .batchId(descriptor.getBatchId())
                .manufacturerId(descriptor.getManufacturerId())
                .build();
    }

    public ShellDescriptor toShellDescriptor() {
        return ShellDescriptor.builder()
                .shellDescriptorId(shellDescriptorId)
                .globalAssetId(globalAssetId)
                .idShort(idShort)
                .manufacturerId(manufacturerId)
                .manufacturerPartId(manufacturerPartId)
                .batchId(batchId)
                .partInstanceId(partInstanceId)
                .build();
    }
}
