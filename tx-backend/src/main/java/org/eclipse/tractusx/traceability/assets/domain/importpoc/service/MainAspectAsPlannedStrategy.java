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

import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned;
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
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.GenericSubmodel;

import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel.extractDetailAspectModelsAsPlanned;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isAsPlannedMainAspect;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isDownwardRelationshipAsPlanned;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isPartSiteInformationAsPlanned;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isUpwardRelationshipAsPlanned;


public class MainAspectAsPlannedStrategy implements MappingStrategy {

    @Override
    public AssetBase mapToAssetBase(ImportRequest.AssetImportRequest assetImportRequestV2, TraceabilityProperties traceabilityProperties) {
        List<GenericSubmodel> submodels = assetImportRequestV2.submodels();

        MainAspectAsPlannedRequest partAsPlannedV2 = submodels.stream()
                .filter(genericSubmodel -> isAsPlannedMainAspect(genericSubmodel.getAspectType()))
                .map(GenericSubmodel::getPayload)
                .filter(MainAspectAsPlannedRequest.class::isInstance)
                .map(MainAspectAsPlannedRequest.class::cast)
                .findFirst()
                .orElse(null);

        PartSiteInformationAsPlannedRequest partSiteInformationAsPlannedRequest = submodels.stream()
                .filter(genericSubmodel -> isPartSiteInformationAsPlanned(genericSubmodel.getAspectType()))
                .map(GenericSubmodel::getPayload)
                .filter(PartSiteInformationAsPlannedRequest.class::isInstance)
                .map(PartSiteInformationAsPlannedRequest.class::cast)
                .findFirst()
                .orElse(null);

        List<Descriptions> parentRelations = submodels.stream()
                .filter(genericSubmodel -> isUpwardRelationshipAsPlanned(genericSubmodel.getAspectType()))
                .map(GenericSubmodel::getPayload)
                .filter(SingleLevelBomAsPlannedRequest.class::isInstance)
                .map(SingleLevelBomAsPlannedRequest.class::cast)
                .map(singleLevelBomAsPlannedRequest -> new Descriptions(singleLevelBomAsPlannedRequest.catenaXId(), null))
                .toList();


        List<Descriptions> childRelations = submodels.stream()
                .filter(genericSubmodel -> isDownwardRelationshipAsPlanned(genericSubmodel.getAspectType()))
                .map(GenericSubmodel::getPayload)
                .filter(SingleLevelUsageAsPlannedRequest.class::isInstance)
                .map(SingleLevelUsageAsPlannedRequest.class::cast)
                .map(singleLevelUsageAsPlannedRequest -> new Descriptions(singleLevelUsageAsPlannedRequest.catenaXId(), null))
                .toList();

        List<DetailAspectModel> detailAspectModels = new ArrayList<>();
        if (partSiteInformationAsPlannedRequest != null) {
            detailAspectModels.addAll(extractDetailAspectModelsPartSiteInformationAsPlanned(emptyIfNull(partSiteInformationAsPlannedRequest.sites())));
        }

        DetailAspectModel asPlannedDetailAspect = extractDetailAspectModelsAsPlanned(partAsPlannedV2.validityPeriod());
        detailAspectModels.add(asPlannedDetailAspect);


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
                    .activeAlert(false)
                    .classification(partAsPlannedV2.partTypeInformation().classification())
                    .inInvestigation(false)
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

    public static DetailAspectModel extractDetailAspectModelsAsPlanned(MainAspectAsPlannedRequest.ValidityPeriod validityPeriod) {
        DetailAspectDataAsPlanned detailAspectDataAsPlanned = DetailAspectDataAsPlanned.builder()
                .validityPeriodFrom(validityPeriod.validFrom())
                .validityPeriodTo(validityPeriod.validTo())
                .build();
        return DetailAspectModel.builder().data(detailAspectDataAsPlanned).type(DetailAspectType.AS_PLANNED).build();
    }


}
