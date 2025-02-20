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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.mapper.TombstoneMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.AssetBaseMappers;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.relationship.RelationshipMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.SubmodelMapper;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnService;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Shell;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.enrichAssetBase;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.enrichUpwardAndDownwardDescriptions;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.getContractAgreementId;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.getOwner;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.getShortId;

@Component
@Slf4j
@RequiredArgsConstructor
public class IrsResponseAssetMapper implements AssetBaseMappers<IRSResponse> {

    private final AssetBaseMapperProvider assetBaseMapperProvider;
    private final ObjectMapper objectMapper;
    private final BpnService bpnService;
    private final TraceabilityProperties traceabilityProperties;


    @Override
    public List<AssetBase> toAssetBaseList(IRSResponse irsResponse) {
        Map<String, List<Descriptions>> descriptionMap = extractRelationshipToDescriptionMap(irsResponse);
        List<DetailAspectModel> tractionBatteryCode = MapperHelper.extractTractionBatteryCode(irsResponse.submodels(), irsResponse.jobStatus().globalAssetId(), assetBaseMapperProvider);
        List<DetailAspectModel> partSiteInformationAsPlanned = MapperHelper.extractPartSiteInformationAsPlanned(irsResponse.submodels(), assetBaseMapperProvider);
        List<AssetBase> tombstones = TombstoneMapper.mapTombstones(irsResponse.jobStatus(), irsResponse.tombstones(), objectMapper);
        if (tombstones != null) {
            log.info("Found {} tombstones", tombstones.size());
        }

        List<AssetBase> submodelAssets = new ArrayList<>(irsResponse
                .submodels()
                .stream()
                .map(irsSubmodel -> {
                    Optional<SubmodelMapper> mapper = assetBaseMapperProvider.getMainSubmodelMapper(irsSubmodel);
                    if (mapper.isPresent()) {
                        AssetBase assetBase = mapper.get().extractSubmodel(irsSubmodel);
                        assetBase.setOwner(getOwner(assetBase, irsResponse, traceabilityProperties.getBpn().toString()));
                        assetBase.setIdShort(getShortId(irsResponse.shells(), assetBase.getId()));
                        assetBase.setLatestContractAgreementId(getContractAgreementId(irsResponse.shells(), assetBase.getId()));
                        assetBase.setManufacturerId(getManufacturerId(irsResponse, assetBase));
                        assetBase.setManufacturerName(bpnService.findByBpn(assetBase.getManufacturerId()));
                        enrichUpwardAndDownwardDescriptions(descriptionMap, assetBase);
                        enrichAssetBase(tractionBatteryCode, assetBase);
                        enrichAssetBase(partSiteInformationAsPlanned, assetBase);
                        return assetBase;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList());

        if (tombstones != null) {
            submodelAssets.addAll(tombstones);
        }
        return submodelAssets;
    }


    @NotNull
    private Map<String, List<Descriptions>> extractRelationshipToDescriptionMap(IRSResponse irsResponse) {
        Map<String, List<Descriptions>> descriptionMap = new HashMap<>();
        irsResponse.relationships().forEach(relationship -> {
            Optional<RelationshipMapper> relationshipMapper = assetBaseMapperProvider.getRelationshipMapper(relationship);
            if (relationshipMapper.isPresent()) {
                Descriptions descriptions = relationshipMapper.get().extractDescription(relationship);
                String catenaXIdString = String.valueOf(relationship.getCatenaXId());
                descriptionMap.computeIfAbsent(catenaXIdString, key -> new ArrayList<>()).add(descriptions);
            }
        });
        return descriptionMap;
    }

    private String getManufacturerId(IRSResponse irsResponse, AssetBase assetBase) {
        log.debug("Starting getManufacturerId for AssetBase ID: {}", assetBase.getId());

        if (assetBase.getManufacturerId() == null && assetBase.getId().equals(irsResponse.jobStatus().globalAssetId())) {
            String manufacturerIdFromJob = irsResponse.jobStatus().parameter().bpn();
            log.debug("Manufacturer ID found in job status: {}", manufacturerIdFromJob);
            return manufacturerIdFromJob;
        }

        if (assetBase.getManufacturerId() == null) {
            log.debug("Attempting to derive manufacturerId from Shell Payloads...");

            return irsResponse.shells().stream()
                    .filter(shell -> shell.payload() != null)
                    .filter(shell -> shell.payload().id().equals(assetBase.getId()))
                    .flatMap(shell -> shell.payload().specificAssetIds().stream())
                    .filter(specificAssetId -> "manufacturerId".equalsIgnoreCase(specificAssetId.name()))
                    .peek(specificAssetId -> log.debug("Found manufacturerId in specificAssetIds: {}", specificAssetId.value()))
                    .map(Shell.Payload.SpecificAssetId::value)
                    .findFirst()
                    .orElse(null);
        }

        log.debug("Returning existing manufacturerId: {}", assetBase.getManufacturerId());
        return assetBase.getManufacturerId();
    }

}


