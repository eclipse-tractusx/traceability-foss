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

import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor.RegistryShellDescriptor.AssetIdType.BATCH_ID;
import static org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor.RegistryShellDescriptor.AssetIdType.MANUFACTURER_ID;
import static org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor.RegistryShellDescriptor.AssetIdType.MANUFACTURER_PART_ID;
import static org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor.RegistryShellDescriptor.AssetIdType.PART_INSTANCE_ID;

public record RegistryShellDescriptor(
        GlobalAssetId globalAssetId,
        String identification,
        String idShort,
        List<SpecificAssetId> specificAssetIds
) {
    public ShellDescriptor toShellDescriptor() {
        String shellDescriptorId = identification();
        String globalAssetId = globalAssetId().value().stream()
                .findFirst()
                .orElse(null);
        Map<String, String> assetIdsMap = specificAssetIds().stream()
                .collect(Collectors.toMap(entry -> entry.key().toLowerCase(), SpecificAssetId::value));

        String manufacturerPartId = assetIdsMap.get(MANUFACTURER_PART_ID.asKey());
        String partInstanceId = assetIdsMap.get(PART_INSTANCE_ID.asKey());
        String manufacturerId = assetIdsMap.get(MANUFACTURER_ID.asKey());
        String batchId = assetIdsMap.get(BATCH_ID.asKey());

        return ShellDescriptor.builder()
                .shellDescriptorId(shellDescriptorId)
                .globalAssetId(globalAssetId)
                .idShort(idShort())
                .partInstanceId(partInstanceId)
                .manufacturerPartId(manufacturerPartId)
                .manufacturerId(manufacturerId)
                .batchId(batchId)
                .build();
    }

    public static RegistryShellDescriptor from(AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
        return new RegistryShellDescriptor(GlobalAssetId.from(assetAdministrationShellDescriptor.getGlobalAssetId()), assetAdministrationShellDescriptor.getIdentification(), assetAdministrationShellDescriptor.getIdShort(), SpecificAssetId.fromList(assetAdministrationShellDescriptor.getSpecificAssetIds()));
    }

    enum AssetIdType {
        MANUFACTURER_PART_ID("manufacturerPartId"),
        PART_INSTANCE_ID("partInstanceId"),
        MANUFACTURER_ID("manufacturerId"),
        BATCH_ID("batchId");

        private final String value;

        AssetIdType(String value) {
            this.value = value;
        }

        public String asKey() {
            return this.value.toLowerCase();
        }
    }
}
