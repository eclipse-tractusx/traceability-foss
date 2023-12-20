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
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Submodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Relationship;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType.SINGLE_LEVEL_BOM_AS_BUILT;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.SINGLE_LEVEL_USAGE_AS_BUILT;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.TRACTION_BATTERY_CODE;

@Slf4j
public record ImportRequest(
        List<AssetWrapperRequest> assetAsBuiltWrapperRequest,
        List<AssetWrapperRequest> assetAsPlannedWrapperRequest
) {


    @JsonCreator
    static ImportRequest of(
            @JsonProperty("assets") List<AssetWrapperRequest> assetRawRequestList
    ) {
        List<AssetWrapperRequest> assetAsBuiltWrapperRequest = new ArrayList<>();
        List<AssetWrapperRequest> assetAsPlannedWrapperRequest = new ArrayList<>();

        for (AssetWrapperRequest assetWrapperRequest : assetRawRequestList) {
            AssetMetaInfoRequest assetMetaInfoRequest = assetWrapperRequest.assetMetaInfoRequest();

            List<Submodel> mainAspectsAsPlanned = assetWrapperRequest.mainAspectModels().stream().filter(submodel -> isAsPlanned(submodel.getAspectType())).toList();
            List<Submodel> upwardAspectsAsPlanned = assetWrapperRequest.upwardRelationship().stream().filter(submodel -> isAsPlanned(submodel.getAspectType())).toList();
            List<Submodel> downwardAspectsAsPlanned = assetWrapperRequest.downwardRelationship().stream().filter(submodel -> isAsPlanned(submodel.getAspectType())).toList();

            List<Submodel> mainAspectsAsBuilt = assetWrapperRequest.mainAspectModels().stream().filter(submodel -> !isAsPlanned(submodel.getAspectType())).toList();
            List<Submodel> upwardAspectsAsBuilt = assetWrapperRequest.upwardRelationship().stream().filter(submodel -> !isAsPlanned(submodel.getAspectType())).toList();
            List<Submodel> downwardAspectsAsasBuilt = assetWrapperRequest.downwardRelationship().stream().filter(submodel -> !isAsPlanned(submodel.getAspectType())).toList();

            assetAsPlannedWrapperRequest.add(new AssetWrapperRequest(assetMetaInfoRequest, mainAspectsAsPlanned, upwardAspectsAsPlanned, downwardAspectsAsPlanned));
            assetAsBuiltWrapperRequest.add(new AssetWrapperRequest(assetMetaInfoRequest, mainAspectsAsBuilt, upwardAspectsAsBuilt, downwardAspectsAsasBuilt));

        }

        return new ImportRequest(
                assetAsBuiltWrapperRequest, assetAsPlannedWrapperRequest
        );
    }

    public List<AssetBase> convertAssetsAsBuilt() {
        return new ArrayList<>(mapToOwnPartsAsBuilt(null, null));
    }

    public List<AssetBase> convertAssetsAsPlanned() {
        return new ArrayList<>(mapToOwnPartsAsPlanned(null, null));
    }

    private List<AssetBase> mapToOwnPartsAsBuilt(Map<String, String> shortIds, Map<String, String> bpnMapping) {
        List<AssetWrapperRequest> assetWrapperRequests = assetAsBuiltWrapperRequest();

        List<SemanticDataModel> ownParts = semanticDataModels().stream()
                .filter(semanticDataModel -> Aspect.isMasterAspect(semanticDataModel.getAspectType()))
                .filter(semanticDataModel -> isOwnPart(semanticDataModel, jobStatus))
                .toList();
        log.info(":: mapToOwnPartsAsBuilt()");
        log.info(":: ownParts: {}", ownParts);
        // The Relationship on supplierPart catenaXId contains the id of the asset which has a relationship
        Map<String, List<Relationship>> supplierPartsMap = relationships().stream()
                .filter(relationship -> SINGLE_LEVEL_BOM_AS_BUILT.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::catenaXId, Collectors.toList()));

        // The Relationship on customerPart childCatenaXId contains the id of the asset which has a relationship
        Map<String, List<Relationship>> customerPartsMap = relationships().stream()
                .filter(relationship -> SINGLE_LEVEL_USAGE_AS_BUILT.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::childCatenaXId, Collectors.toList()));

        //TRACEFOSS-2333: A tractionBatteryCode has no catenaxId. If a tractionBatteryCode is present for the requested
        // global_asset_id, then it can be automatically mapped to the own SerialPart
        Optional<DetailAspectDataTractionBatteryCode> tractionBatteryCodeOptional = semanticDataModels.stream()
                .filter(semanticDataModel -> semanticDataModel.getAspectType().contains(TRACTION_BATTERY_CODE.getAspectName()))
                .map(DetailAspectDataTractionBatteryCode.class::cast).findFirst();

        final List<AssetBase> assets = ownParts
                .stream()
                .map(semanticDataModel -> semanticDataModel.toDomainAsBuilt(semanticDataModel.localIdentifiers(), shortIds, Owner.OWN, bpnMapping,
                        getParentParts(customerPartsMap, shortIds, semanticDataModel.catenaXId()),
                        getChildParts(supplierPartsMap, shortIds, semanticDataModel.catenaXId()),
                        tractionBatteryCodeOptional, ImportState.TRANSIENT))
                .toList();
        log.info(":: mapped assets: {}", assets);
        return assets;
    }

    private List<AssetBase> mapToOwnPartsAsPlanned(Map<String, String> shortIds, Map<String, String> bpnMapping) {

        List<SemanticDataModel> ownPartsAsPlanned =
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
        return assets;
    }

    private static boolean isAsPlanned(final String aspectType) {
        return aspectType.contains("AsPlanned");
    }

    private static boolean isAsBuilt(final String aspectType) {
        return !aspectType.contains("AsPlanned");
    }
}


