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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.response.semanticdatamodel;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record SemanticDataModel(
        String catenaXId,
        PartTypeInformation partTypeInformation,
        ManufacturingInformation manufacturingInformation,
        List<LocalId> localIdentifiers
) {
    public SemanticDataModel(String catenaXId, PartTypeInformation partTypeInformation, ManufacturingInformation manufacturingInformation, List<LocalId> localIdentifiers) {
        this.catenaXId = catenaXId;
        this.partTypeInformation = partTypeInformation;
        this.manufacturingInformation = manufacturingInformation;
        this.localIdentifiers = Objects.requireNonNullElse(localIdentifiers, Collections.emptyList());
    }

    public static List<Asset> toDomainList(List<SemanticDataModel> parts, Map<String, String> shortIds, Owner owner, Map<String, String> bpns, List<Descriptions> parentRelations, List<Descriptions> childRelations) {
        return parts.stream().map(semanticDataModel -> semanticDataModel.toDomain(shortIds, owner, bpns, parentRelations, childRelations)).toList();
    }

    public Optional<String> getLocalId(LocalIdKey key) {
        return localIdentifiers.stream()
                .filter(localId -> localId.key() == key)
                .findFirst()
                .map(LocalId::value);
    }

    public Asset toDomain(Map<String, String> shortIds, Owner owner, Map<String, String> bpns, List<Descriptions> parentRelations, List<Descriptions> childRelations) {
        final String manufacturerName = bpns.get(manufacturerId());
        return Asset.builder()
                .id(catenaXId())
                .idShort(defaultValue(shortIds.get(catenaXId())))
                .nameAtManufacturer(defaultValue(partTypeInformation().nameAtManufacturer()))
                .manufacturerPartId(defaultValue(partTypeInformation().manufacturerPartId()))
                .partInstanceId(partInstanceId())
                .manufacturerId(manufacturerId())
                .batchId(batchId())
                .manufacturerName(defaultValue(manufacturerName))
                .nameAtCustomer(defaultValue(partTypeInformation().nameAtCustomer()))
                .customerPartId(defaultValue(partTypeInformation().customerPartId()))
                .manufacturingDate(manufacturingDate())
                .manufacturingCountry(manufacturingCountry())
                .parentDescriptions(parentRelations)
                .childDescriptions(childRelations)
                .owner(owner)
                .underInvestigation(false)
                .underAlert(false)
                .qualityType(QualityType.OK)
                .van(van())
                .build();
    }

    private String manufacturerId() {
        return getLocalId(LocalIdKey.MANUFACTURER_ID)
                .orElse("--");
    }

    private String batchId() {
        return getLocalId(LocalIdKey.BATCH_ID)
                .orElse("--");
    }

    private String partInstanceId() {
        return getLocalId(LocalIdKey.PART_INSTANCE_ID)
                .orElse("--");
    }

    private String manufacturingCountry() {
        if (manufacturingInformation() == null) {
            return "--";
        }
        return manufacturingInformation().country();
    }

    private Instant manufacturingDate() {
        if (manufacturingInformation() == null) {
            return null;
        }

        return Optional.ofNullable(manufacturingInformation().date())
                .map(Date::toInstant)
                .orElse(null);
    }

    private String defaultValue(String value) {
        final String EMPTY_TEXT = "--";
        if (!StringUtils.hasText(value)) {
            return EMPTY_TEXT;
        }
        return value;
    }

    private String van() {
        final String EMPTY_TEXT = "--";
        return getLocalId(LocalIdKey.VAN)
                .orElse(EMPTY_TEXT);
    }
}

