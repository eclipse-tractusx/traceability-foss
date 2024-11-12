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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.AssetMetaInfoRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.AssetBaseMappers;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.SubmodelMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.relationship.SubmodelRelationshipMapper;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.enrichAssetBase;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel.MapperHelper.enrichUpwardAndDownwardDescriptions;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImportAssetMapper implements AssetBaseMappers<List<ImportRequest.AssetImportRequest>> {

    private final AssetBaseMapperProvider assetBaseMapperProvider;
    private final TraceabilityProperties traceabilityProperties;

    @Override
    public List<AssetBase> toAssetBaseList(List<ImportRequest.AssetImportRequest> assetImportRequestList) {
        List<AssetBase> assetBaseList = new ArrayList<>();
        assetImportRequestList.forEach(assetImportRequest -> {
            AssetMetaInfoRequest assetMetaInfoRequest = assetImportRequest.assetMetaInfoRequest();
            List<IrsSubmodel> submodels = assetImportRequest.submodels();
            Map<String, List<Descriptions>> descriptionMap = extractRelationshipToDescriptionMap(assetMetaInfoRequest, submodels);
            List<DetailAspectModel> tractionBatteryCode = MapperHelper.extractTractionBatteryCode(submodels, assetMetaInfoRequest.catenaXId(), assetBaseMapperProvider);
            List<DetailAspectModel> partSiteInformationAsPlanned = MapperHelper.extractPartSiteInformationAsPlanned(submodels, assetBaseMapperProvider);
            List<AssetBase> mainSubmodels = submodels.stream().map(irsSubmodel -> {
                Optional<SubmodelMapper> mapper = assetBaseMapperProvider.getMainSubmodelMapper(irsSubmodel);
                if (mapper.isPresent()) {
                    AssetBase assetBase = mapper.get().extractSubmodel(irsSubmodel);
                    assetBase.setOwner(Owner.OWN);
                    assetBase.setQualityType(QualityType.OK);
                    assetBase.setImportNote(ImportNote.TRANSIENT_CREATED);
                    assetBase.setImportState(ImportState.TRANSIENT);
                    assetBase.setManufacturerId(traceabilityProperties.getBpn().value());
                    assetBase.setDigitalTwinType(assetMetaInfoRequest.digitalTwinType());
                    enrichUpwardAndDownwardDescriptions(descriptionMap, assetBase);
                    enrichUpwardAndDownwardDescriptions(descriptionMap, assetBase);
                    enrichAssetBase(tractionBatteryCode, assetBase);
                    enrichAssetBase(partSiteInformationAsPlanned, assetBase);
                    return assetBase;
                }
                return null;
            }).filter(Objects::nonNull).toList();
            assetBaseList.addAll(mainSubmodels);
        });
        return assetBaseList;
    }

    @NotNull
    private Map<String, List<Descriptions>> extractRelationshipToDescriptionMap(AssetMetaInfoRequest assetMetaInfoRequest, List<IrsSubmodel> irsSubmodels) {
        Map<String, List<Descriptions>> descriptionMap = new HashMap<>();
        irsSubmodels.forEach(irsSubmodel -> {
            Optional<SubmodelRelationshipMapper> relationshipSubmodelMapper = assetBaseMapperProvider.getRelationshipSubmodelMapper(irsSubmodel);
            if (relationshipSubmodelMapper.isPresent()) {
                Descriptions descriptions = relationshipSubmodelMapper.get().extractDescription(irsSubmodel);
                String catenaXIdString = String.valueOf(assetMetaInfoRequest.catenaXId());
                descriptionMap.computeIfAbsent(catenaXIdString, key -> new ArrayList<>()).add(descriptions);
            }
        });
        return descriptionMap;
    }


}


