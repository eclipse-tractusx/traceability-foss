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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class AssetsConverter {

    public static final String EMPTY_TEXT = "--";

    private final BpnRepository bpnRepository;

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final String SINGLE_LEVEL_USAGE_AS_BUILT = "SingleLevelUsageAsBuilt";
    private static final String ASSEMBLY_PART_RELATIONSHIP = "AssemblyPartRelationship";

    public List<Asset> readAndConvertAssets() {
        try {
            InputStream file = AssetsConverter.class.getResourceAsStream("/data/irs_assets_v2.json");
            JobResponse response = mapper.readValue(file, JobResponse.class);

            return convertAssets(response);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public List<Asset> convertDefaultAsset(List<ShellDescriptor> items) {
        return items.stream()
                .map(this::toAsset)
                .toList();
    }

    public List<Asset> convertAssets(JobResponse response) {

        Map<String, String> shortIds = response.shells().stream()
                .collect(Collectors.toMap(Shell::identification, Shell::idShort));

        List<Asset> ownParts = mapToOwnParts(response, shortIds);
        List<Asset> otherParts = new ArrayList<>();

        if (isSupplierDirection(response)) {
            otherParts.addAll(mapToOtherParts(response, shortIds, Owner.SUPPLIER));
        } else {
            otherParts.addAll(mapToOtherParts(response, shortIds, Owner.CUSTOMER));
        }
        List<Asset> convertedAssets = new ArrayList<>();
        convertedAssets.addAll(ownParts);
        convertedAssets.addAll(otherParts);
        return convertedAssets;
    }

    private boolean isSupplierDirection(JobResponse response) {
        return response.jobStatus().parameter().direction().equals("downward");
    }

    private List<Asset> mapToOtherParts(JobResponse response, Map<String, String> shortIds, Owner owner) {
        List<SerialPartTypization> customerParts = response.serialPartTypizations().stream().filter(serialPartTypization -> !serialPartTypization.catenaXId().equals(response.jobStatus().globalAssetId())).toList();
        return customerParts.stream()
                .map(part ->
                        Asset.builder()
                                .id(part.catenaXId())
                                .idShort(defaultValue(shortIds.get(part.catenaXId())))
                                .nameAtManufacturer(defaultValue(part.partTypeInformation().nameAtManufacturer()))
                                .manufacturerPartId(defaultValue(part.partTypeInformation().manufacturerPartId()))
                                .partInstanceId(partInstanceId(part))
                                .manufacturerId(manufacturerId(part))
                                .batchId(batchId(part))
                                .manufacturerName(manufacturerName(part))
                                .nameAtCustomer(defaultValue(part.partTypeInformation().nameAtCustomer()))
                                .customerPartId(defaultValue(part.partTypeInformation().customerPartId()))
                                .manufacturingDate(manufacturingDate(part))
                                .manufacturingCountry(manufacturingCountry(part))
                                .owner(owner)
                                .underInvestigation(false)
                                .qualityType(QualityType.OK)
                                .van(van(part))
                                .build()).toList();
    }

    private List<Asset> mapToOwnParts(JobResponse response, Map<String, String> shortIds) {

        List<SerialPartTypization> ownParts = response.serialPartTypizations().stream().filter(serialPartTypization -> serialPartTypization.catenaXId().equals(response.jobStatus().globalAssetId())).toList();

        // The Relationship on supplierPart catenaXId contains the id of the asset which has a relationship
        Map<String, List<Relationship>> supplierPartsMap = response.relationships().stream()
                .filter(relationship -> ASSEMBLY_PART_RELATIONSHIP.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::catenaXId, Collectors.toList()));

        // The Relationship on customerPart childCatenaXId contains the id of the asset which has a relationship
        Map<String, List<Relationship>> customerPartsMap = response.relationships().stream()
                .filter(relationship -> SINGLE_LEVEL_USAGE_AS_BUILT.equals(relationship.aspectType().getAspectName()))
                .collect(Collectors.groupingBy(Relationship::childCatenaXId, Collectors.toList()));

        return ownParts.stream()
                .map(part ->
                        Asset.builder()
                                .id(part.catenaXId())
                                .idShort(defaultValue(shortIds.get(part.catenaXId())))
                                .nameAtManufacturer(defaultValue(part.partTypeInformation().nameAtManufacturer()))
                                .manufacturerPartId(defaultValue(part.partTypeInformation().manufacturerPartId()))
                                .partInstanceId(partInstanceId(part))
                                .manufacturerId(manufacturerId(part))
                                .batchId(batchId(part))
                                .manufacturerName(manufacturerName(part))
                                .nameAtCustomer(defaultValue(part.partTypeInformation().nameAtCustomer()))
                                .customerPartId(defaultValue(part.partTypeInformation().customerPartId()))
                                .manufacturingDate(manufacturingDate(part))
                                .manufacturingCountry(manufacturingCountry(part))
                                .owner(Owner.OWN)
                                .childDescriptions(getChildParts(supplierPartsMap, shortIds, part.catenaXId()))
                                .parentDescriptions(getParentParts(customerPartsMap, shortIds, part.catenaXId()))
                                .underInvestigation(false)
                                .qualityType(QualityType.OK)
                                .van(van(part))
                                .build()).toList();
    }

    private Asset toAsset(ShellDescriptor shellDescriptor) {
        String manufacturerId = shellDescriptor.manufacturerId();
        String manufacturerName = bpnRepository.findManufacturerName(manufacturerId).orElse(EMPTY_TEXT);
        return Asset.builder()
                .id(shellDescriptor.globalAssetId())
                .idShort(shellDescriptor.idShort())
                .nameAtManufacturer(shellDescriptor.idShort())
                .manufacturerPartId(defaultValue(shellDescriptor.manufacturerPartId()))
                .partInstanceId(defaultValue(shellDescriptor.partInstanceId()))
                .manufacturerId(defaultValue(manufacturerId))
                .batchId(defaultValue(shellDescriptor.batchId()))
                .manufacturerName(manufacturerName)
                .nameAtCustomer(shellDescriptor.idShort())
                .customerPartId(shellDescriptor.manufacturerPartId())
                .manufacturingCountry(EMPTY_TEXT)
                .owner(Owner.OWN)
                .childDescriptions(Collections.emptyList())
                .parentDescriptions(Collections.emptyList())
                .underInvestigation(false)
                .qualityType(QualityType.OK)
                .van(EMPTY_TEXT)
                .build();
    }

    private String manufacturerName(SerialPartTypization part) {
        String manufacturerId = manufacturerId(part);

        return bpnRepository.findManufacturerName(manufacturerId).orElse(EMPTY_TEXT);
    }

    private String manufacturerId(SerialPartTypization part) {
        return part.getLocalId(LocalIdKey.MANUFACTURER_ID)
                .orElse(EMPTY_TEXT);
    }

    private String batchId(SerialPartTypization part) {
        return part.getLocalId(LocalIdKey.BATCH_ID)
                .orElse(EMPTY_TEXT);
    }

    private String partInstanceId(SerialPartTypization part) {
        return part.getLocalId(LocalIdKey.PART_INSTANCE_ID)
                .orElse(EMPTY_TEXT);
    }

    private String manufacturingCountry(SerialPartTypization part) {
        if (part.manufacturingInformation() == null) {
            return EMPTY_TEXT;
        }
        return part.manufacturingInformation().country();
    }

    private Instant manufacturingDate(SerialPartTypization part) {
        if (part.manufacturingInformation() == null) {
            return null;
        }

        return Optional.ofNullable(part.manufacturingInformation().date())
                .map(Date::toInstant)
                .orElse(null);
    }

    private String defaultValue(String value) {
        if (!StringUtils.hasText(value)) {
            return EMPTY_TEXT;
        }
        return value;
    }

    private List<Descriptions> getChildParts(Map<String, List<Relationship>> relationships, Map<String, String> shortIds, String catenaXId) {
        log.info("Child Parts Mapping: catenaXiD {} with relationships {} and shortIds {}", catenaXId, relationships, shortIds);
        return Optional.ofNullable(relationships.get(catenaXId))
                .orElse(Collections.emptyList())
                .stream()
                .map(child -> new Descriptions(child.childCatenaXId(), shortIds.get(child.childCatenaXId())))
                .toList();
    }

    private List<Descriptions> getParentParts(Map<String, List<Relationship>> relationships, Map<String, String> shortIds, String catenaXId) {
        log.info("Parent Parts Mapping: catenaXiD {} with relationships {} and shortIds {}", catenaXId, relationships, shortIds);
        return Optional.ofNullable(relationships.get(catenaXId))
                .orElse(Collections.emptyList())
                .stream()
                .map(child -> new Descriptions(child.catenaXId(), shortIds.get(child.catenaXId())))
                .toList();
    }

    private String van(SerialPartTypization part) {
        return part.getLocalId(LocalIdKey.VAN)
                .orElse(EMPTY_TEXT);
    }

}
