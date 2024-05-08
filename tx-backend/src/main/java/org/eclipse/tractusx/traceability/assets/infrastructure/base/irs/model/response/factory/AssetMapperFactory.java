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
import org.eclipse.tractusx.irs.component.Relationship;
import org.eclipse.tractusx.irs.component.enums.Direction;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.mapper.TombstoneMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.asbuilt.AsBuiltDetailMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.asplanned.AsPlannedDetailMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.relationship.RelationshipMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.SubmodelMapper;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.enrichAssetBase;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.getContractAgreementId;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.getOwner;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.getShortId;

@Component
@Slf4j
@RequiredArgsConstructor
public class AssetMapperFactory {

    private final List<SubmodelMapper> baseMappers;
    private final List<RelationshipMapper> relationshipMappers;
    private final List<AsPlannedDetailMapper> asPlannedDetailMappers;
    private final List<AsBuiltDetailMapper> asBuiltDetailMappers;
    private final ObjectMapper objectMapper;
    private final BpnService bpnService;

    public List<AssetBase> mapToAssetBaseList(IRSResponse irsResponse) {
        Map<String, List<Descriptions>> descriptionMap = extractRelationshipToDescriptionMap(irsResponse);
        List<DetailAspectModel> tractionBatteryCode = extractTractionBatteryCode(irsResponse);
        List<DetailAspectModel> partSiteInformationAsPlanned = extractPartSiteInformationAsPlanned(irsResponse);
        List<AssetBase> tombstones = TombstoneMapper.mapTombstones(irsResponse.jobStatus(), irsResponse.tombstones(), objectMapper);
        if (tombstones != null) {
            log.info("Found {} tombstones", tombstones.size());
        }
        return toAssetBase(irsResponse, descriptionMap, tractionBatteryCode, partSiteInformationAsPlanned, tombstones);
    }


    @NotNull
    private List<AssetBase> toAssetBase(IRSResponse irsResponse,
                                        Map<String, List<Descriptions>> descriptionMap, List<DetailAspectModel> tractionBatteryCode,
                                        List<DetailAspectModel> partSiteInformationAsPlanned,
                                        List<AssetBase> tombstones) {
        List<AssetBase> submodelAssets = new ArrayList<>(irsResponse
                .submodels()
                .stream()
                .map(irsSubmodel -> {
                    Optional<SubmodelMapper> mapper = getMainSubmodelMapper(irsSubmodel);
                    if (mapper.isPresent()) {
                        AssetBase assetBase = mapper.get().extractSubmodel(irsSubmodel);
                        assetBase.setOwner(getOwner(assetBase, irsResponse));
                        assetBase.setIdShort(getShortId(irsResponse.shells(), assetBase.getId()));
                        assetBase.setContractAgreementId(getContractAgreementId(irsResponse.shells(), assetBase.getId()));
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

        submodelAssets.addAll(tombstones);
        return submodelAssets;
    }


    @NotNull
    private List<DetailAspectModel> extractPartSiteInformationAsPlanned(IRSResponse irsResponse) {
        return irsResponse
                .submodels()
                .stream()
                .flatMap(irsSubmodel -> {
                    Optional<AsPlannedDetailMapper> mapper = getAsPlannedDetailMapper(irsSubmodel);
                    return mapper.map(asPlannedDetailMapper -> asPlannedDetailMapper.extractDetailAspectModel(irsSubmodel).stream()).orElseGet(Stream::empty);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @NotNull
    private List<DetailAspectModel> extractTractionBatteryCode(IRSResponse irsResponse) {
        return irsResponse
                .submodels()
                .stream()
                .flatMap(irsSubmodel -> {
                    Optional<AsBuiltDetailMapper> mapper = getAsBuiltDetailMapper(irsSubmodel);
                    return mapper.map(asBuiltDetailMapper -> asBuiltDetailMapper.extractDetailAspectModel(irsSubmodel, irsResponse.jobStatus().globalAssetId()).stream()).orElseGet(Stream::empty);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private static void enrichUpwardAndDownwardDescriptions(Map<String, List<Descriptions>> descriptionsMap, AssetBase assetBase) {
        List<Descriptions> upwardDescriptions = new ArrayList<>();
        List<Descriptions> downwardDescriptions = new ArrayList<>();

        List<Descriptions> descriptions = descriptionsMap.get(assetBase.getId());
        for (Descriptions description : emptyIfNull(descriptions)) {
            if (description.direction() == Direction.UPWARD) {
                upwardDescriptions.add(description);
            } else if (description.direction() == Direction.DOWNWARD) {
                downwardDescriptions.add(description);
            }
        }

        assetBase.setChildRelations(downwardDescriptions);
        assetBase.setParentRelations(upwardDescriptions);
    }

    @NotNull
    private Map<String, List<Descriptions>> extractRelationshipToDescriptionMap(IRSResponse irsResponse) {
        Map<String, List<Descriptions>> descriptionMap = new HashMap<>();

        irsResponse.relationships().forEach(relationship -> {
            Optional<RelationshipMapper> relationshipMapper = getRelationshipMapper(relationship);
            if (relationshipMapper.isPresent()) {
                Descriptions descriptions = relationshipMapper.get().extractDescription(relationship);
                String catenaXIdString = String.valueOf(relationship.getCatenaXId());
                descriptionMap.computeIfAbsent(catenaXIdString, key -> new ArrayList<>()).add(descriptions);
            }

        });
        return descriptionMap;
    }

    private Optional<SubmodelMapper> getMainSubmodelMapper(IrsSubmodel irsSubmodel) {
        return baseMappers.stream().filter(assetBaseMapper -> assetBaseMapper.validMapper(irsSubmodel)).findFirst();
    }

    private Optional<SubmodelMapper> getRelationshipSubmodelMapper(IrsSubmodel irsSubmodel) {
        return baseMappers.stream().filter(assetBaseMapper -> assetBaseMapper.validMapper(irsSubmodel)).findFirst();
    }

    private Optional<RelationshipMapper> getRelationshipMapper(Relationship relationship) {
        return relationshipMappers.stream().filter(relationshipMapper -> relationshipMapper.validMapper(relationship)).findFirst();
    }

    private Optional<AsPlannedDetailMapper> getAsPlannedDetailMapper(IrsSubmodel irsSubmodel) {
        return asPlannedDetailMappers.stream().filter(asPlannedDetailMapper -> asPlannedDetailMapper.validMapper(irsSubmodel)).findFirst();
    }

    private Optional<AsBuiltDetailMapper> getAsBuiltDetailMapper(IrsSubmodel irsSubmodel) {
        return asBuiltDetailMappers.stream().filter(asBuiltDetailMapper -> asBuiltDetailMapper.validMapper(irsSubmodel)).findFirst();
    }

    private String getManufacturerId(IRSResponse irsResponse, AssetBase assetBase){
        if (assetBase.getManufacturerId() == null && assetBase.getId().equals(irsResponse.jobStatus().globalAssetId())) {
            return irsResponse.jobStatus().parameter().bpn();
        }
        return assetBase.getManufacturerId();
    }

}


