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

import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataAsBuilt;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.MainAspectAsBuiltRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.SingleLevelBomAsBuiltRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.SingleLevelUsageAsBuiltRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel.extractDetailAspectModelTractionBatteryCode;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isAsBuiltMainAspect;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isDownwardRelationshipAsBuilt;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isTractionBatteryCode;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isUpwardRelationshipAsBuilt;

public class MainAspectAsBuiltStrategy implements MappingStrategy {
    // TODO the mapping method here is almost the same as in SemanticDataModel.toDomainAsBuilt
    @Override
    public AssetBase mapToAssetBase(ImportRequest.AssetImportRequest assetImportRequestV2, TraceabilityProperties traceabilityProperties) {

        List<IrsSubmodel> submodels = assetImportRequestV2.submodels();
        MainAspectAsBuiltRequest asBuiltAspect = submodels.stream()
                .filter(genericSubmodel -> isAsBuiltMainAspect(genericSubmodel.getAspectType()))
                .map(IrsSubmodel::getPayload)
                .filter(MainAspectAsBuiltRequest.class::isInstance)
                .map(MainAspectAsBuiltRequest.class::cast)
                .findFirst()
                .orElse(null);

        List<DetailAspectDataTractionBatteryCode> detailAspectDataTractionBatteryCodes = submodels.stream()
                .filter(genericSubmodel -> isTractionBatteryCode(genericSubmodel.getAspectType()))
                .map(IrsSubmodel::getPayload)
                .filter(DetailAspectDataTractionBatteryCode.class::isInstance)
                .map(DetailAspectDataTractionBatteryCode.class::cast)
                .toList();

        List<Descriptions> parentRelations = submodels.stream()
                .filter(genericSubmodel -> isUpwardRelationshipAsBuilt(genericSubmodel.getAspectType()))
                .map(IrsSubmodel::getPayload)
                .filter(SingleLevelBomAsBuiltRequest.class::isInstance)
                .map(SingleLevelBomAsBuiltRequest.class::cast)
                .findFirst()
                .map(SingleLevelBomAsBuiltRequest::childItems)
                .orElse(Collections.emptyList())
                .stream()
                .map(childItem -> new Descriptions(childItem.catenaXId(), null, null, null))
                .toList();

        List<Descriptions> childRelations = submodels.stream()
                .filter(genericSubmodel -> isDownwardRelationshipAsBuilt(genericSubmodel.getAspectType()))
                .map(IrsSubmodel::getPayload)
                .filter(SingleLevelUsageAsBuiltRequest.class::isInstance)
                .map(SingleLevelUsageAsBuiltRequest.class::cast)
                .findFirst()
                .map(SingleLevelUsageAsBuiltRequest::parentItems)
                .map(parentItems -> parentItems.stream()
                        .map(parentItem -> new Descriptions(
                                parentItem.catenaXId(),
                                null,
                                null,
                                null)).toList()).orElse(Collections.emptyList());


        final AtomicReference<String> semanticModelId = new AtomicReference<>();
        final AtomicReference<org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel> semanticDataModel = new AtomicReference<>();
        final AtomicReference<String> manufacturerId = new AtomicReference<>();
        final AtomicReference<String> van = new AtomicReference<>();

        List<DetailAspectModel> detailAspectModels = new ArrayList<>();
        DetailAspectModel asBuiltDetailAspect = extractDetailAspectModelsAsBuilt(asBuiltAspect.manufacturingInformation(), asBuiltAspect.partTypeInformation());
        detailAspectModels.add(asBuiltDetailAspect);

        asBuiltAspect.localIdentifiers().stream().filter(localIdentifier -> localIdentifier.key().equals("partInstanceId")).findFirst().ifPresent(s -> {
            semanticModelId.set(s.value());
            semanticDataModel.set(org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel.SERIALPART);
            detailAspectDataTractionBatteryCodes.forEach(detailAspectDataTractionBatteryCode -> detailAspectModels.add(extractDetailAspectModelTractionBatteryCode(detailAspectDataTractionBatteryCode)));
        });

        asBuiltAspect.localIdentifiers().stream().filter(localId -> localId.key().equals("batchId")).findFirst().ifPresent(s -> {
            semanticModelId.set(s.value());
            semanticDataModel.set(org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel.BATCH);
        });

        asBuiltAspect.localIdentifiers().stream().filter(localId -> localId.key().equals("jisNumber")).findFirst().ifPresent(s -> {
            semanticModelId.set(s.value());
            semanticDataModel.set(org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel.JUSTINSEQUENCE);
        });

        asBuiltAspect.localIdentifiers().stream().filter(localId -> localId.key().equals("van")).findFirst().ifPresent(s -> {
            van.set(s.value());
        });

        asBuiltAspect.localIdentifiers().stream().filter(localId -> localId.key().equals("manufacturerId")).findFirst().ifPresent(s -> manufacturerId.set(s.value()));

        if (semanticDataModel.get() == null) {
            semanticDataModel.set(org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel.UNKNOWN);
        }


        return AssetBase.builder()
                .id(assetImportRequestV2.assetMetaInfoRequest().catenaXId())
                .semanticModelId(semanticModelId.get())
                .detailAspectModels(detailAspectModels)
                .manufacturerId(manufacturerId.get())
                .nameAtManufacturer(asBuiltAspect.partTypeInformation().nameAtManufacturer())
                .manufacturerPartId(asBuiltAspect.partTypeInformation().manufacturerPartId())
                .parentRelations(parentRelations)
                .childRelations(childRelations)
                .owner(Owner.OWN)
                // TODO enrich
                .classification(null)
                .qualityType(QualityType.OK)
                .semanticDataModel(semanticDataModel.get())
                .importState(ImportState.TRANSIENT)
                .importNote(ImportNote.TRANSIENT_CREATED)
                .van(van.get())
                .build();
    }

    public static DetailAspectModel extractDetailAspectModelsAsBuilt(MainAspectAsBuiltRequest.ManufacturingInformation manufacturingInformation,
                                                                     MainAspectAsBuiltRequest.PartTypeInformation partTypeInformation) {

        DetailAspectDataAsBuilt detailAspectDataAsBuilt = DetailAspectDataAsBuilt.builder()
                .customerPartId(partTypeInformation.customerPartId())
                .manufacturingCountry(manufacturingInformation.country())
                .manufacturingDate(OffsetDateTime.parse(manufacturingInformation.date()))
                .nameAtCustomer(partTypeInformation.nameAtCustomer())
                .partId(partTypeInformation.manufacturerPartId())
                .build();
        return DetailAspectModel.builder().data(detailAspectDataAsBuilt).type(DetailAspectType.AS_BUILT).build();
    }
}
