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

package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.component.assetadministrationshell.Endpoint;
import org.eclipse.tractusx.irs.component.assetadministrationshell.IdentifierKeyValuePair;
import org.eclipse.tractusx.irs.component.assetadministrationshell.ProtocolInformation;
import org.eclipse.tractusx.irs.component.assetadministrationshell.Reference;
import org.eclipse.tractusx.irs.component.assetadministrationshell.SecurityAttribute;
import org.eclipse.tractusx.irs.component.assetadministrationshell.SemanticId;
import org.eclipse.tractusx.irs.component.assetadministrationshell.SubmodelDescriptor;
import org.eclipse.tractusx.irs.registryclient.decentral.DigitalTwinRegistryCreateShellService;
import org.eclipse.tractusx.irs.registryclient.decentral.exception.CreateDtrShellException;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.repository.SubmodelPayloadRepository;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.common.properties.RegistryProperties;
import org.eclipse.tractusx.traceability.submodel.domain.repository.SubmodelServerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DtrService {
    private static final String GLOBAL_REFERENCE = "GlobalReference";
    private static final String EXTERNAL_REFERENCE = "ExternalReference";

    private final DigitalTwinRegistryCreateShellService dtrCreateShellService;
    private final SubmodelPayloadRepository submodelPayloadRepository;
    private final SubmodelServerRepository submodelServerRepository;
    private final EdcProperties edcProperties;
    private final RegistryProperties registryProperties;

    public String createShellInDtr(final AssetBase assetBase, String submodelServerAssetId) throws CreateDtrShellException {
        Map<String, String> payloadByAspectType = submodelPayloadRepository.getAspectTypesAndPayloadsByAssetId(assetBase.getId());
        Map<String, UUID> createdSubmodelIdByAspectType = payloadByAspectType.entrySet().stream()
                .map(this::createSubmodel)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<SubmodelDescriptor> descriptors = toSubmodelDescriptors(createdSubmodelIdByAspectType, submodelServerAssetId);
        AssetAdministrationShellDescriptor assetAdministrationShellDescriptor = aasFrom(assetBase, descriptors);
        dtrCreateShellService.createShell(assetAdministrationShellDescriptor);
        return assetBase.getId();
    }

    private List<SubmodelDescriptor> toSubmodelDescriptors(Map<String, UUID> createdSubmodelIdByAspectType, String submodelServerAssetId) {
        return createdSubmodelIdByAspectType.entrySet()
                .stream().map(entry ->
                        toSubmodelDescriptor(entry.getKey(), entry.getValue(), submodelServerAssetId)

                ).toList();
    }

    private SubmodelDescriptor toSubmodelDescriptor(String aspectType, UUID submodelServerIdReference, String submodelServerAssetId) {
        return SubmodelDescriptor.builder()
                .description(List.of())
                .idShort(aspectTypeToSimpleSubmodelName(aspectType))
                .id(submodelServerIdReference.toString())
                .semanticId(
                        Reference.builder()
                                .type(EXTERNAL_REFERENCE)
                                .keys(
                                        List.of(SemanticId.builder()
                                                .type(GLOBAL_REFERENCE)
                                                .value(aspectType).build())
                                ).build()
                )
                .endpoints(
                        List.of(
                                Endpoint.builder()
                                        .interfaceInformation("SUBMODEL-3.0")
                                        .protocolInformation(
                                                ProtocolInformation.builder()
                                                        .href(edcProperties.getProviderDataplaneEdcUrl() + "/api/public/" + submodelServerIdReference)
                                                        .endpointProtocol("HTTP")
                                                        .endpointProtocolVersion(List.of("1.1"))
                                                        .subprotocol("DSP")
                                                        .subprotocolBodyEncoding("plain")
                                                        .subprotocolBody(getSubProtocol(submodelServerAssetId))
                                                        .securityAttributes(
                                                                List.of(SecurityAttribute.none())
                                                        ).build()
                                        ).build()
                        )
                ).build();
    }

    private String getSubProtocol(String submodelServerAssetId) {
        final String edcProviderControlplaneUrl = edcProperties.getProviderEdcUrl();
        return "id=%s;dspEndpoint=%s".formatted(submodelServerAssetId, edcProviderControlplaneUrl);
    }

    private String aspectTypeToSimpleSubmodelName(String aspectType) {
        String[] split = aspectType.split("#");
        return split[1];
    }

    private Map.Entry<String, UUID> createSubmodel(Map.Entry<String, String> payloadByAspectType) {
        UUID submodelId = UUID.randomUUID();
        submodelServerRepository.saveSubmodel(submodelId.toString(), payloadByAspectType.getValue());
        log.info("create submodelId {} for aspectType {} on submodelServer", submodelId, payloadByAspectType.getKey());
        return Map.entry(payloadByAspectType.getKey(), submodelId);
    }

    private AssetAdministrationShellDescriptor aasFrom(AssetBase assetBase, List<SubmodelDescriptor> descriptors) {
        return AssetAdministrationShellDescriptor.builder()
                .globalAssetId(assetBase.getId())
                .idShort(assetBase.getIdShort())
                .id(UUID.randomUUID().toString())
                .specificAssetIds(aasIdentifiersFromAsset(assetBase))
                .submodelDescriptors(descriptors)
                .build();
    }

    List<IdentifierKeyValuePair> aasIdentifiersFromAsset(AssetBase assetBase) {

        List<IdentifierKeyValuePair> identifierKeyValuePairs = List.of(
                IdentifierKeyValuePair.builder()
                        .name("manufacturerId")
                        .value(assetBase.getManufacturerId())
                        .externalSubjectId(
                                Reference.builder()
                                        .type(EXTERNAL_REFERENCE)
                                        .keys(getExternalSubjectIds())
                                        .build())
                        .build(),
                IdentifierKeyValuePair.builder()
                        .name("manufacturerPartId")
                        .value(assetBase.getManufacturerPartId())
                        .externalSubjectId(
                                Reference.builder()
                                        .type(EXTERNAL_REFERENCE)
                                        .keys(getExternalSubjectIds())
                                        .build())
                        .build()
        );
        log.info("IdentifierKeyValuePair {}", identifierKeyValuePairs);
        return identifierKeyValuePairs;
    }


    private List<SemanticId> getExternalSubjectIds() {
        List<SemanticId> externalSubjectIds = List.of(SemanticId.builder()
                .type(GLOBAL_REFERENCE)
                .value("PUBLIC_READABLE")
                .build());
        List<SemanticId> configurationExternalSubjectIds = getAllowedBpns().stream()
                .map(allowedBpn ->
                        SemanticId.builder()
                                .type(GLOBAL_REFERENCE)
                                .value(allowedBpn)
                                .build()
                )
                .toList();

        return Stream.concat(externalSubjectIds.stream(), configurationExternalSubjectIds.stream()).toList();
    }

    // TODO: Issue #740 will handle and decide how to avoid having allowed bpns in config ( should be managed by policy to access data ) needs investigation
    public List<String> getAllowedBpns() {
        return Arrays.stream(registryProperties.getAllowedBpns().split(",")).toList();
    }
}
