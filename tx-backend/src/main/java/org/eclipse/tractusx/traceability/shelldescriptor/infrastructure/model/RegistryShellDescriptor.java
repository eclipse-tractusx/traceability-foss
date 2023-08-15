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

package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.model;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;

import java.util.List;

@Slf4j
public record RegistryShellDescriptor(
        GlobalAssetId globalAssetId,
        String identification,
        String idShort,
        List<SpecificAssetId> specificAssetIds
) {
    public ShellDescriptor toShellDescriptor() {
        String globalAssetId = globalAssetId().value().stream()
                .findFirst()
                .orElse(null);
        log.info("toShellDescriptor: {}", specificAssetIds);

        return ShellDescriptor.builder()
                .globalAssetId(globalAssetId)
                .build();
    }

    public static RegistryShellDescriptor from(AssetAdministrationShellDescriptor assetAdministrationShellDescriptor) {
        return new RegistryShellDescriptor(GlobalAssetId.from(List.of(assetAdministrationShellDescriptor.getGlobalAssetId())), assetAdministrationShellDescriptor.getId(), assetAdministrationShellDescriptor.getIdShort(), SpecificAssetId.fromList(assetAdministrationShellDescriptor.getSpecificAssetIds()));
    }
}
