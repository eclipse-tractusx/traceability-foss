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

package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.registryclient.DigitalTwinRegistryKey;
import org.eclipse.tractusx.irs.registryclient.decentral.DecentralDigitalTwinRegistryService;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.model.RegistryShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.model.RegistryShellDescriptorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class RegistryService {
    private final String applicationBPN;
    private final String manufacturerIdKey;

    private final DecentralDigitalTwinRegistryService decentralDigitalTwinRegistryService;


    public RegistryService(@Value("${traceability.bpn}") String applicationBPN,
                           @Value("${traceability.registry.manufacturerIdKey}") String manufacturerIdKey,
                           DecentralDigitalTwinRegistryService decentralDigitalTwinRegistryService) {
        this.applicationBPN = applicationBPN;
        this.manufacturerIdKey = manufacturerIdKey;
        this.decentralDigitalTwinRegistryService = decentralDigitalTwinRegistryService;
    }

    public List<ShellDescriptor> findOwnShellDescriptors() throws RegistryServiceException {

        log.info("Fetching all shell descriptor IDs for BPN {}.", applicationBPN);

        Map<String, Object> ownManufacturerIdBPNMap = new HashMap<>();

        ownManufacturerIdBPNMap.put("assetIds", getFilterValue(manufacturerIdKey, applicationBPN));

        Collection<DigitalTwinRegistryKey> registryKeys = null;
        try {
            registryKeys = decentralDigitalTwinRegistryService.lookupShellIdentifiers(applicationBPN);
            registryKeys.forEach(digitalTwinRegistryKey -> log.info("DTR Key" + digitalTwinRegistryKey));
        } catch (Exception e) {

            log.error("Fetching shell ownShellsRegistryResponse failed", e);
        }

        log.info("Fetching shell ownShellsRegistryResponse.");

        final RegistryShellDescriptorResponse ownShellsRegistryResponse;
        try {
            Collection<AssetAdministrationShellDescriptor> assetAdministrationShellDescriptors = decentralDigitalTwinRegistryService.fetchShells(registryKeys);
            assetAdministrationShellDescriptors.forEach(assetAdministrationShellDescriptor -> {
                log.info("Asset Administration Shell Descriptor " + assetAdministrationShellDescriptor);
                log.info("Asset Administration Global Asset Id " + assetAdministrationShellDescriptor.getGlobalAssetId());
            });

            ownShellsRegistryResponse = RegistryShellDescriptorResponse.fromCollection(assetAdministrationShellDescriptors);

        } catch (FeignException e) {

            log.error("Fetching shell ownShellsRegistryResponse failed", e);

            throw e;
        }

        log.info("Received {} shell ownShellsRegistryResponse for {} IDs.",
                ownShellsRegistryResponse.items().size(),
                registryKeys == null ? 0 : registryKeys.size());

        List<ShellDescriptor> ownShellDescriptors = ownShellsRegistryResponse.items().stream()
                .filter(it -> Objects.nonNull(it.globalAssetId()))
                .map(RegistryShellDescriptor::toShellDescriptor)
                .toList();

        log.info("Found {} shell ownShellsRegistryResponse containing a global asset ID.", ownShellDescriptors.size());

        return ownShellDescriptors;
    }

    private String getFilterValue(String key, String value) {
        return URLEncoder.encode(String.format("{\"key\":\"%s\",\"value\":\"%s\"}", key, value), StandardCharsets.UTF_8);
    }
}
