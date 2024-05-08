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

import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.MainAspectAsPlannedRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.PartSiteInformationAsPlannedRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.SingleLevelBomAsPlannedRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.SingleLevelUsageAsPlannedRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isAsPlannedMainAspect;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isPartSiteInformationAsPlanned;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isUpwardRelationshipAsPlanned;


public class MainAspectAsPlannedStrategy implements MappingStrategy {

    // TODO the mapping method here is almost the same as in SemanticDataModel.toDomainAsPlanned
    @Override
    public AssetBase mapToAssetBase(ImportRequest.AssetImportRequest assetImportRequestV2, TraceabilityProperties traceabilityProperties) {
        List<IrsSubmodel> submodels = assetImportRequestV2.submodels();

        MainAspectAsPlannedRequest partAsPlannedV2 = submodels.stream()
                .filter(genericSubmodel -> isAsPlannedMainAspect(genericSubmodel.getAspectType()))
                .map(IrsSubmodel::getPayload)
                .filter(MainAspectAsPlannedRequest.class::isInstance)
                .map(MainAspectAsPlannedRequest.class::cast)
                .findFirst()
                .orElse(null);

        PartSiteInformationAsPlannedRequest partSiteInformationAsPlannedRequest = submodels.stream()
                .filter(genericSubmodel -> isPartSiteInformationAsPlanned(genericSubmodel.getAspectType()))
                .map(IrsSubmodel::getPayload)
                .filter(PartSiteInformationAsPlannedRequest.class::isInstance)
                .map(PartSiteInformationAsPlannedRequest.class::cast)
                .findFirst()
                .orElse(null);

        List<Descriptions> parentRelations = submodels.stream()
                .filter(genericSubmodel -> isUpwardRelationshipAsPlanned(genericSubmodel.getAspectType()))
                .map(IrsSubmodel::getPayload)
                .filter(SingleLevelBomAsPlannedRequest.class::isInstance)
                .map(SingleLevelBomAsPlannedRequest.class::cast)
                .findFirst()
                .map(SingleLevelBomAsPlannedRequest::childItems)
                .orElse(Collections.emptyList())
                .stream()
                .map(childItem -> new Descriptions(childItem.catenaXId(), null, null, null))
                .toList();


        List<Descriptions> childRelations = submodels.stream()
                .map(IrsSubmodel::getPayload)
                .filter(SingleLevelUsageAsPlannedRequest.class::isInstance)
                .map(SingleLevelUsageAsPlannedRequest.class::cast)
                .findFirst()
                .map(SingleLevelUsageAsPlannedRequest::parentParts)
                .orElse(Collections.emptyList())
                .stream()
                .map(parentPart -> new Descriptions(parentPart.parentCatenaXId(), null, null, null))
                .toList();

        List<DetailAspectModel> detailAspectModels = new ArrayList<>();
        if (partSiteInformationAsPlannedRequest != null) {
            detailAspectModels.addAll(extractDetailAspectModelsPartSiteInformationAsPlanned(emptyIfNull(partSiteInformationAsPlannedRequest.sites())));
        }

        AssetBase.AssetBaseBuilder assetBaseBuilder = AssetBase.builder();
        if (partAsPlannedV2 != null) {
            assetBaseBuilder
                    .id(assetImportRequestV2.assetMetaInfoRequest().catenaXId())
                    .manufacturerId(traceabilityProperties.getBpn().value())
                    .nameAtManufacturer(partAsPlannedV2.partTypeInformation().nameAtManufacturer())
                    .manufacturerPartId(partAsPlannedV2.partTypeInformation().manufacturerPartId())
                    .parentRelations(parentRelations)
                    .detailAspectModels(detailAspectModels)
                    .childRelations(childRelations)
                    .owner(Owner.OWN)
                    .classification(partAsPlannedV2.partTypeInformation().classification())
                    .qualityType(QualityType.OK)
                    .semanticDataModel(org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel.PARTASPLANNED)
                    .importState(ImportState.TRANSIENT)
                    .importNote(ImportNote.TRANSIENT_CREATED);
        }

        return assetBaseBuilder.build();

    }

    public static List<DetailAspectModel> extractDetailAspectModelsPartSiteInformationAsPlanned(List<PartSiteInformationAsPlannedRequest.Site> sites) {
        List<DetailAspectModel> detailAspectModels = new ArrayList<>();
        emptyIfNull(sites).forEach(site -> {
            DetailAspectDataPartSiteInformationAsPlanned detailAspectDataPartSiteInformationAsPlanned = DetailAspectDataPartSiteInformationAsPlanned.builder()
                    .catenaXSiteId(site.catenaXSiteId())
                    .functionValidFrom(OffsetDateTime.parse(site.functionValidFrom()))
                    .function(site.function())
                    .functionValidUntil(OffsetDateTime.parse(site.functionValidUntil()))
                    .build();
            detailAspectModels.add(DetailAspectModel.builder().data(detailAspectDataPartSiteInformationAsPlanned).type(DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED).build());
        });

        return detailAspectModels;
    }


}
