/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Relationship;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public record JobDetailResponse(
        JobStatus jobStatus,
        List<Shell> shells,
        List<SemanticDataModel> semanticDataModels,
        List<Relationship> relationships,
        Map<String, String> bpns
) {

    private static final String SINGLE_LEVEL_USAGE_AS_BUILT = "SingleLevelUsageAsBuilt";
    private static final String SINGLE_LEVEL_BOM_AS_BUILT = "SingleLevelBomAsBuilt";
    private static final String SINGLE_LEVEL_BOM_AS_PLANNED = "SingleLevelBomAsPlanned";
    private static final String JOB_STATUS_COMPLETED = "COMPLETED";
    private static final String JOB_STATUS_RUNNING = "RUNNING";
    private static final String AS_PLANNED_MAPPING_ASPECT = "urn:bamm:io.catenax.part_as_planned:1.0.0#PartAsPlanned";
    private static final String AS_BUILT_MAPPING_ASPECT_SERIALPART = "urn:bamm:io.catenax.serial_part:1.0.0#SerialPart";
    private static final String AS_BUILT_MAPPING_ASPECT_BATCH = "urn:bamm:io.catenax.batch:1.0.0#Batch";

    @JsonCreator
    static JobDetailResponse of(
            @JsonProperty("job") JobStatus jobStatus,
            @JsonProperty("relationships") List<Relationship> relationships,
            @JsonProperty("shells") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Shell> shells,
            @JsonProperty("submodels") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Submodel> submodels,
            @JsonProperty("bpns") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Bpn> bpns
    ) {
        Map<String, String> bpnsMap = bpns.stream()
                .collect(Collectors.toMap(Bpn::manufacturerId, Bpn::manufacturerName));

        List<SemanticDataModel> semanticDataModels = submodels.stream()
                .filter(submodel -> submodel.getPayload() instanceof SemanticDataModel)
                .map(submodel -> {
                    SemanticDataModel payload = (SemanticDataModel) submodel.getPayload();
                    payload.setAspectType(submodel.getAspectType());
                    return payload;
                }).toList();

        return new JobDetailResponse(
                jobStatus,
                shells,
                semanticDataModels,
                relationships,
                bpnsMap
        );
    }

    public boolean isRunning() {
        return JOB_STATUS_RUNNING.equals(jobStatus.state());
    }

    public boolean isCompleted() {
        return JOB_STATUS_COMPLETED.equals(jobStatus.state());
    }

    public List<Asset> convertAssets() {
        return convertAssets(BomLifecycle.fromString(jobStatus().parameter().bomLifecycle()));
    }

    private List<Asset> convertAssets(BomLifecycle bomLifecycle) {

        log.info(":: convertAssets(\"{}\")", bomLifecycle.getRealName());
        log.info(":: relationships: {}", relationships.toString());

        Map<String, String> shortIds = shells().stream()
                .collect(Collectors.toMap(
                        Shell::identification,
                        Shell::idShort,
                        (existingValue, newValue) -> existingValue));

        Map<String, String> bpnMapping = bpns();

        List<Asset> ownParts = new ArrayList<>();
        List<Asset> otherParts = new ArrayList<>();
        if (bomLifecycle.equals(BomLifecycle.AS_BUILT)) {
            ownParts = mapToOwnPartsAsBuilt(shortIds, bpnMapping);
            if (isSupplierDirection()) {
                otherParts.addAll(mapToOtherPartsAsBuilt(shortIds, Owner.SUPPLIER, bpnMapping));
            } else {
                otherParts.addAll(mapToOtherPartsAsBuilt(shortIds, Owner.CUSTOMER, bpnMapping));
            }
        }
        if (bomLifecycle.equals(BomLifecycle.AS_PLANNED)) {
            ownParts = mapToOwnPartsAsPlanned(shortIds, bpnMapping);
            if (isSupplierDirection()) {
                otherParts.addAll(mapToOtherPartsAsPlanned(shortIds, Owner.SUPPLIER, bpnMapping));
            }
        }

        List<Asset> convertedAssets = new ArrayList<>();
        convertedAssets.addAll(ownParts);
        convertedAssets.addAll(otherParts);
        return convertedAssets;
    }

    private boolean isSupplierDirection() {
        return jobStatus().parameter().direction().equalsIgnoreCase(Direction.DOWNWARD.name());
    }

    private List<Asset> mapToOtherPartsAsBuilt(Map<String, String> shortIds, Owner owner, Map<String, String> bpnMapping) {
        List<SemanticDataModel> otherParts = semanticDataModels().stream().filter(semanticDataModel -> isAsBuiltAndOtherPart(semanticDataModel, jobStatus)).toList();

        log.info(":: mapToOtherPartsAsBuilt()" );
        log.info(":: otherParts: {}", otherParts);
        final List<Asset> assets =  otherParts
                .stream()
                .map(semanticDataModel -> semanticDataModel.toDomain(semanticDataModel.localIdentifiers(), shortIds, owner, bpnMapping,
                        Collections.emptyList(),
                        Collections.emptyList()))
                .toList();
        log.info(":: mapped assets: {}", assets);
        return assets;
    }

    private List<Asset> mapToOtherPartsAsPlanned(Map<String, String> shortIds, Owner owner, Map<String, String> bpnMapping) {
        List<SemanticDataModel> otherParts = semanticDataModels().stream().filter(semanticDataModel -> isAsPlannedAndOtherPart(semanticDataModel, jobStatus)).toList();
        log.info(":: mapToOtherPartsAsPlanned()" );
        log.info(":: otherParts: {}", otherParts);
        final List<Asset> assets =  otherParts
                .stream()
                .map(semanticDataModel -> semanticDataModel.toDomainAsPlanned(shortIds, owner, bpnMapping,
                        Collections.emptyList(),
                        Collections.emptyList()))
                .toList();
        log.info(":: mapped assets: {}", assets);
        return assets;
    }

    private List<Asset> mapToOwnPartsAsPlanned(Map<String, String> shortIds, Map<String, String> bpnMapping) {

        List<SemanticDataModel> ownPartsAsPlanned =
                semanticDataModels().stream()
                        .filter(semanticDataModel -> isAsPlannedAndOwnPart(semanticDataModel, jobStatus)).toList();

        log.info(":: mapToOwnPartsAsPlanned()" );
        log.info(":: ownPartsAsPlanned: {}", ownPartsAsPlanned);

        Map<String, List<Relationship>> singleLevelBomRelationship = relationships().stream()
                .filter(relationship -> SINGLE_LEVEL_BOM_AS_PLANNED.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::catenaXId, Collectors.toList()));


        final List<Asset> assets =  ownPartsAsPlanned
                .stream()
                .map(semanticDataModel -> semanticDataModel.toDomainAsPlanned(shortIds, Owner.OWN, bpnMapping,
                        Collections.emptyList(),
                        getChildParts(singleLevelBomRelationship, shortIds, semanticDataModel.catenaXId())))
                .toList();
        log.info(":: mapped assets: {}", assets);
        return assets;
    }

    private List<Asset> mapToOwnPartsAsBuilt(Map<String, String> shortIds, Map<String, String> bpnMapping) {
        List<SemanticDataModel> ownParts = semanticDataModels().stream().filter(semanticDataModel -> isAsBuiltAndOwnPart(semanticDataModel, jobStatus)).toList();
        log.info(":: mapToOwnPartsAsBuilt()" );
        log.info(":: ownParts: {}", ownParts);
        // The Relationship on supplierPart catenaXId contains the id of the asset which has a relationship
        Map<String, List<Relationship>> supplierPartsMap = relationships().stream()
                .filter(relationship -> SINGLE_LEVEL_BOM_AS_BUILT.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::catenaXId, Collectors.toList()));

        // The Relationship on customerPart childCatenaXId contains the id of the asset which has a relationship
        Map<String, List<Relationship>> customerPartsMap = relationships().stream()
                .filter(relationship -> SINGLE_LEVEL_USAGE_AS_BUILT.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::childCatenaXId, Collectors.toList()));

        final List<Asset> assets = ownParts
                .stream()
                .map(semanticDataModel -> semanticDataModel.toDomain(semanticDataModel.localIdentifiers(), shortIds, Owner.OWN, bpnMapping,
                        getParentParts(customerPartsMap, shortIds, semanticDataModel.catenaXId()),
                        getChildParts(supplierPartsMap, shortIds, semanticDataModel.catenaXId())))
                .toList();
        log.info(":: mapped assets: {}", assets);
        return assets;
    }

    private List<Descriptions> getChildParts(Map<String, List<Relationship>> relationships, Map<String, String> shortIds, String catenaXId) {

        return Optional.ofNullable(relationships.get(catenaXId))
                .orElse(Collections.emptyList())
                .stream()
                .map(child -> new Descriptions(child.childCatenaXId(), shortIds.get(child.childCatenaXId())))
                .toList();
    }

    private List<Descriptions> getParentParts(Map<String, List<Relationship>> relationships, Map<String, String> shortIds, String catenaXId) {
        return Optional.ofNullable(relationships.get(catenaXId))
                .orElse(Collections.emptyList())
                .stream()
                .map(child -> new Descriptions(child.catenaXId(), shortIds.get(child.catenaXId())))
                .toList();
    }

    private boolean isAsBuiltAndOwnPart(SemanticDataModel semanticDataModel, JobStatus jobStatus) {
        final boolean result = semanticDataModel.getCatenaXId().equals(jobStatus.globalAssetId()) &&
                (semanticDataModel.aspectType().equals(AS_BUILT_MAPPING_ASPECT_SERIALPART)
                        || semanticDataModel.aspectType().equals(AS_BUILT_MAPPING_ASPECT_BATCH));
        log.info(":: isAsBuildAndOwnPart() {}", semanticDataModel);
        log.info(":: result: {}", result);
        return result;
    }

    private boolean isAsBuiltAndOtherPart(SemanticDataModel semanticDataModel, JobStatus jobStatus) {
        final boolean result =!semanticDataModel.getCatenaXId()
                .equals(jobStatus.globalAssetId()) &&
                (semanticDataModel.aspectType().equals(AS_BUILT_MAPPING_ASPECT_SERIALPART)
                        || semanticDataModel.aspectType().equals(AS_BUILT_MAPPING_ASPECT_BATCH));
        log.info(":: isAsBuiltAndOtherPart() {}", semanticDataModel);
        log.info(":: result: {}", result);
        return result;
    }

    private boolean isAsPlannedAndOwnPart(SemanticDataModel semanticDataModel, JobStatus jobStatus) {
        final boolean result = semanticDataModel.getCatenaXId().equals(jobStatus.globalAssetId()) && semanticDataModel.aspectType().equals(AS_PLANNED_MAPPING_ASPECT);
        log.info(":: isAsPlannedAndOwnPart() {}", semanticDataModel);
        log.info(":: result: {}", result);
        return result;
    }

    private boolean isAsPlannedAndOtherPart(SemanticDataModel semanticDataModel, JobStatus jobStatus) {
        final boolean result = !semanticDataModel.getCatenaXId().equals(jobStatus.globalAssetId()) && semanticDataModel.aspectType().equals(AS_PLANNED_MAPPING_ASPECT);
        log.info(":: isAsPlannedAndOtherPart() {}", semanticDataModel);
        log.info(":: result: {}", result);
        return result;
    }

}

record Bpn(
        String manufacturerId,
        String manufacturerName
) {
}
