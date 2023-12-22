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
package org.eclipse.tractusx.traceability.assets.domain.importpoc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.GenericSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse.addPartSiteInformationAsPlannedToOwnPartsAsPlanned;

@Slf4j
public record ImportRequest(
        Map<BomLifecycle, List<AssetWrapperRequest>> bomLifecycleToAssetWrapperRequestList
) {


    @JsonCreator
    static ImportRequest of(
            @JsonProperty("assets") List<AssetWrapperRequest> assetRawRequestList
    ) {
        List<AssetWrapperRequest> assetAsBuiltWrapperRequest = new ArrayList<>();
        List<AssetWrapperRequest> assetAsPlannedWrapperRequest = new ArrayList<>();

        for (AssetWrapperRequest assetWrapperRequest : assetRawRequestList) {
            AssetMetaInfoRequest assetMetaInfoRequest = assetWrapperRequest.assetMetaInfoRequest();

            if (BomLifecycle.AS_BUILT.equals(assetWrapperRequest.bomLifecycle())) {
                List<SemanticDataModel> mainAspectsAsBuilt = assetWrapperRequest.mainAspectModels();


                List<GenericSubmodel> upwardAspectsAsBuilt = assetWrapperRequest.upwardRelationship().stream()
                        .map(request -> {
                            // Assuming there is a method getSubmodel() in SingleLevelBomAsBuiltRequest
                            SingelLevelUsageAsBuiltRequest submodel = (SingelLevelUsageAsBuiltRequest) request.getPayload();

                            // Perform any additional operations on submodel if needed

                            // Create a new SingleLevelBomAsBuiltRequest with the casted submodel
                            return new GenericSubmodel(request.getAspectType(), submodel/* pass other parameters and the modified submodel */);
                        }).toList();

                List<GenericSubmodel> downwardAspectsAsBuilt = assetWrapperRequest.downwardRelationship();
                assetAsBuiltWrapperRequest.add(new AssetWrapperRequest(assetMetaInfoRequest, mainAspectsAsBuilt, upwardAspectsAsBuilt, downwardAspectsAsBuilt, BomLifecycle.AS_BUILT));
            }

            if (BomLifecycle.AS_PLANNED.equals(assetWrapperRequest.bomLifecycle())) {
                List<SemanticDataModel> mainAspectsAsPlanned = assetWrapperRequest.mainAspectModels();
                List<GenericSubmodel> upwardAspectsAsPlanned = assetWrapperRequest.upwardRelationship();

                List<GenericSubmodel> downwardAspectsAsPlanned = assetWrapperRequest.downwardRelationship();
                assetAsPlannedWrapperRequest.add(new AssetWrapperRequest(assetMetaInfoRequest, mainAspectsAsPlanned, upwardAspectsAsPlanned, downwardAspectsAsPlanned, BomLifecycle.AS_PLANNED));

            }

        }
        Map<BomLifecycle, List<AssetWrapperRequest>> bomLifecycleToAssetWrapperList = new EnumMap<>(BomLifecycle.class);

        bomLifecycleToAssetWrapperList.put(BomLifecycle.AS_BUILT, assetAsBuiltWrapperRequest);
        bomLifecycleToAssetWrapperList.put(BomLifecycle.AS_PLANNED, assetAsPlannedWrapperRequest);
        return new ImportRequest(bomLifecycleToAssetWrapperList);
    }

    public List<AssetBase> convertAssetsAsBuilt() {
        return new ArrayList<>(mapToOwnPartsAsBuilt());
    }

    public List<AssetBase> convertAssetsAsPlanned(final String bpn) {
        return new ArrayList<>(mapToOwnPartsAsPlanned(bpn));
    }

    private List<AssetBase> mapToOwnPartsAsBuilt() {

        List<AssetWrapperRequest> assetWrapperRequests = bomLifecycleToAssetWrapperRequestList.get(BomLifecycle.AS_BUILT);
        List<AssetBase> list = new ArrayList<>();
        for (AssetWrapperRequest assetWrapperRequest : assetWrapperRequests) {

            List<SemanticDataModel> mainAspectModels = assetWrapperRequest.mainAspectModels().stream().filter(semanticDataModel -> !semanticDataModel.aspectType().contains("traction_battery_code")).toList();

            List<DetailAspectDataTractionBatteryCode> detailAspectDataTractionBatteryCodes = assetWrapperRequest
                    .mainAspectModels()
                    .stream()
                    .filter(semanticDataModel -> semanticDataModel.aspectType().contains("traction_battery_code"))
                    .map(semanticDataModel -> (DetailAspectDataTractionBatteryCode) semanticDataModel).toList();

            List<GenericSubmodel> downwardModels = assetWrapperRequest.downwardRelationship();
            List<GenericSubmodel> upwardModels = assetWrapperRequest.upwardRelationship();


            List<Descriptions> parentRelations = upwardModels.stream()
                    .map(genericSubmodel -> {
                        SingelLevelUsageAsBuiltRequest payload = (SingelLevelUsageAsBuiltRequest) genericSubmodel.getPayload();
                        return payload.customers().stream()
                                .flatMap(customer -> customer.parentItems().stream())
                                .map(parentItem -> new Descriptions(parentItem.catenaXId(), null)).toList();
                    })
                    .flatMap(List::stream).toList();


            List<Descriptions> childRelations = downwardModels.stream()
                    .map(genericSubmodel -> {
                        SingleLevelBomAsBuiltRequest payload = (SingleLevelBomAsBuiltRequest) genericSubmodel.getPayload();
                        return payload.childItems().stream()
                                .map(childItem -> new Descriptions(childItem.catenaXId(), null)).toList();
                    })
                    .flatMap(List::stream).toList();


            list.addAll(mainAspectModels.stream().map(semanticDataModel -> semanticDataModel.toDomainAsBuiltLight(parentRelations, childRelations, detailAspectDataTractionBatteryCodes)).toList());

        }

        return list;
    }

    private List<AssetBase> mapToOwnPartsAsPlanned(final String bpn) {
        List<AssetWrapperRequest> assetWrapperRequests = bomLifecycleToAssetWrapperRequestList.get(BomLifecycle.AS_PLANNED);
        List<AssetBase> list = new ArrayList<>();
        for (AssetWrapperRequest assetWrapperRequest : assetWrapperRequests) {

            List<SemanticDataModel> mainAspectModels = assetWrapperRequest.mainAspectModels().stream().filter(semanticDataModel -> !semanticDataModel.aspectType().contains(Aspect.PART_SITE_INFORMATION_AS_PLANNED.getAspectName())).toList();
            List<SemanticDataModel> partSiteInfoAsPlanned =
                    mainAspectModels.stream()
                            .filter(semanticDataModel -> semanticDataModel.aspectType().contains(Aspect.PART_SITE_INFORMATION_AS_PLANNED.getAspectName())).toList();

            addPartSiteInformationAsPlannedToOwnPartsAsPlanned(mainAspectModels, partSiteInfoAsPlanned);

            List<GenericSubmodel> downwardModels = assetWrapperRequest.downwardRelationship();
            List<GenericSubmodel> upwardModels = assetWrapperRequest.upwardRelationship();


            List<Descriptions> parentRelations = upwardModels.stream()
                    .map(genericSubmodel -> {
                        SingleLevelUsageAsPlannedRequest payload = (SingleLevelUsageAsPlannedRequest) genericSubmodel.getPayload();
                        return payload.parentParts().stream()
                                .map(parentPart -> new Descriptions(parentPart.parentCatenaXId(), null)).toList();
                    })
                    .flatMap(List::stream).toList();


            List<Descriptions> childRelations = downwardModels.stream()
                    .map(genericSubmodel -> {
                        SingleLevelBomAsPlannedRequest payload = (SingleLevelBomAsPlannedRequest) genericSubmodel.getPayload();
                        return payload.childItems().stream()
                                .map(childItem -> new Descriptions(childItem.catenaXId(), null)).toList();
                    })
                    .flatMap(List::stream).toList();

            list.addAll(mainAspectModels.stream().map(semanticDataModel -> semanticDataModel.toDomainAsPlannedLight(parentRelations, childRelations, bpn)).toList());

        }

        return list;
    }

}


