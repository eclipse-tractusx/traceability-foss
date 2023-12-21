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
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.GenericSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Submodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Relationship;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.LocalId;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

            if (BomLifecycle.AS_BUILT.equals(assetWrapperRequest.bomLifecycle())){
                List<SemanticDataModel> mainAspectsAsBuilt = assetWrapperRequest.mainAspectModels();
                List<GenericSubmodel> upwardAspectsAsBuilt = assetWrapperRequest.upwardRelationship();
                List<GenericSubmodel> downwardAspectsAsBuilt = assetWrapperRequest.downwardRelationship();
                assetAsBuiltWrapperRequest.add(new AssetWrapperRequest(assetMetaInfoRequest, mainAspectsAsBuilt, upwardAspectsAsBuilt, downwardAspectsAsBuilt, BomLifecycle.AS_BUILT));
            }

            if (BomLifecycle.AS_PLANNED.equals(assetWrapperRequest.bomLifecycle())){
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

    public List<AssetBase> convertAssetsAsPlanned() {
        return new ArrayList<>(mapToOwnPartsAsPlanned(null, null));
    }

    private List<AssetBase> mapToOwnPartsAsBuilt() {

        List<AssetWrapperRequest> assetWrapperRequests = bomLifecycleToAssetWrapperRequestList.get(BomLifecycle.AS_BUILT);
        for (AssetWrapperRequest assetWrapperRequest : assetWrapperRequests){

            AssetMetaInfoRequest assetMetaInfoRequest = assetWrapperRequest.assetMetaInfoRequest();
            List<SemanticDataModel> mainAspectModels = assetWrapperRequest.mainAspectModels();
            List<GenericSubmodel> downwardModels = assetWrapperRequest.downwardRelationship();
            List<GenericSubmodel> upwardModels = assetWrapperRequest.upwardRelationship();


/*            Map<String, List<Relationship>> supplierPartsMap = relationships().stream()
                    .filter(relationship -> SINGLE_LEVEL_BOM_AS_BUILT.equals(relationship.aspectType().getAspectName()))
                    .collect(Collectors.groupingBy(Relationship::catenaXId, Collectors.toList()));

            // The Relationship on customerPart childCatenaXId contains the id of the asset which has a relationship
            Map<String, List<Relationship>> customerPartsMap = relationships().stream()
                    .filter(relationship -> SINGLE_LEVEL_USAGE_AS_BUILT.equals(relationship.aspectType().getAspectName()))
                    .collect(Collectors.groupingBy(Relationship::childCatenaXId, Collectors.toList()));*/


            List<LocalId> localIds = Collections.emptyList();
            Map<String, String> shortIds = new HashMap<>();
            Owner owner = Owner.OWN;
            Map<String, String> bpns = new HashMap<>();
            List<Descriptions> parentRelations =  Collections.emptyList();
            List<Descriptions> childRelations = Collections.emptyList();
            Optional< DetailAspectDataTractionBatteryCode > tractionBatteryCodeOptional = Optional.empty();
            ImportState assetImportState = ImportState.TRANSIENT;

            List<AssetBase> list = mainAspectModels.stream().map(semanticDataModel -> semanticDataModel.toDomainAsBuilt(localIds, shortIds, owner, bpns, parentRelations, childRelations, tractionBatteryCodeOptional, assetImportState)).toList();
            return list;

        }

        return Collections.emptyList();
    }

    private List<AssetBase> mapToOwnPartsAsPlanned(Map<String, String> shortIds, Map<String, String> bpnMapping) {
        List<AssetWrapperRequest> assetWrapperRequests = bomLifecycleToAssetWrapperRequestList.get(BomLifecycle.AS_PLANNED);
      /*  List<SemanticDataModel> ownPartsAsPlanned =
                semanticDataModels().stream()
                        .filter(semanticDataModel -> isOwnPart(semanticDataModel, jobStatus))
                        .filter(semanticDataModel -> Aspect.isMasterAspect(semanticDataModel.aspectType())).toList();

        List<SemanticDataModel> isPartSiteInformationAsPlanned =
                semanticDataModels().stream()
                        .filter(semanticDataModel -> isOwnPart(semanticDataModel, jobStatus))
                        .filter(semanticDataModel -> semanticDataModel.aspectType().contains(Aspect.PART_SITE_INFORMATION_AS_PLANNED.getAspectName())).toList();

        addPartSiteInformationAsPlannedToOwnPartsAsPlanned(ownPartsAsPlanned, isPartSiteInformationAsPlanned);

        log.info(":: mapToOwnPartsAsPlanned()");
        log.info(":: ownPartsAsPlanned: {}", ownPartsAsPlanned);

        Map<String, List<Relationship>> singleLevelBomRelationship = relationships().stream()
                .filter(relationship -> SINGLE_LEVEL_BOM_AS_PLANNED.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::catenaXId, Collectors.toList()));

        String ownerBpn = jobStatus.parameter().bpn();

        final List<AssetBase> assets = ownPartsAsPlanned
                .stream()
                .map(semanticDataModel -> semanticDataModel.toDomainAsPlanned(
                        shortIds,
                        Owner.OWN,
                        bpnMapping,
                        Collections.emptyList(),
                        getChildParts(singleLevelBomRelationship, shortIds, semanticDataModel.catenaXId()),
                        ownerBpn,
                        ImportState.PERSISTENT
                ))
                .toList();
        log.info(":: mapped assets: {}", assets);
        return assets;*/
        return Collections.emptyList();
    }

    private static boolean isAsPlanned(final String aspectType) {
        return aspectType.contains("AsPlanned");
    }

    private static boolean isAsBuilt(final String aspectType) {
        return !aspectType.contains("AsPlanned");
    }
}


