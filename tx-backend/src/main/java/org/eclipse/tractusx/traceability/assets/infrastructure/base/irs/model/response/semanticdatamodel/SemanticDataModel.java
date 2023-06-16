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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.model.SemanticModel;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public record SemanticDataModel(
        String catenaXId,
        PartTypeInformation partTypeInformation,
        ManufacturingInformation manufacturingInformation,
        List<LocalId> localIdentifiers,
        ValidityPeriod validityPeriod

) {
    public SemanticDataModel(String catenaXId, PartTypeInformation partTypeInformation, ManufacturingInformation manufacturingInformation, List<LocalId> localIdentifiers, ValidityPeriod validityPeriod) {
        this.catenaXId = catenaXId;
        this.partTypeInformation = partTypeInformation;
        this.manufacturingInformation = manufacturingInformation;
        this.localIdentifiers = Objects.requireNonNullElse(localIdentifiers, Collections.emptyList());
        this.validityPeriod = validityPeriod;
    }

    public Optional<String> getLocalId(LocalIdKey key) {
        return localIdentifiers.stream()
                .filter(localId -> localId.key() == key)
                .findFirst()
                .map(LocalId::value);
    }

    public Optional<String> getLocalIdByInput(LocalIdKey key, List<LocalId> localIds) {
        return localIds.stream()
                .filter(localId -> localId.key() == key)
                .findFirst()
                .map(LocalId::value);
    }

    public Asset toDomain(List<LocalId> localIds, Map<String, String> shortIds, Owner owner, Map<String, String> bpns, List<Descriptions> parentRelations, List<Descriptions> childRelations) {
        final String manufacturerName = bpns.get(manufacturerId());

        final AtomicReference<String> semanticModelId = new AtomicReference<>();
        final AtomicReference<org.eclipse.tractusx.traceability.assets.domain.model.SemanticDataModel> semanticDataModel = new AtomicReference<>();

        getLocalIdByInput(LocalIdKey.PART_INSTANCE_ID, localIds).ifPresent(s -> {
            semanticModelId.set(s);
            semanticDataModel.set(org.eclipse.tractusx.traceability.assets.domain.model.SemanticDataModel.SERIALPARTTYPIZATION);
        });

        getLocalIdByInput(LocalIdKey.BATCH_ID, localIds).ifPresent(s -> {
            semanticModelId.set(s);
            semanticDataModel.set(org.eclipse.tractusx.traceability.assets.domain.model.SemanticDataModel.BATCH);
        });


        if (semanticDataModel.get() == null) {
            semanticDataModel.set(org.eclipse.tractusx.traceability.assets.domain.model.SemanticDataModel.UNKNOWN);
        }

        return Asset.builder()
                .id(catenaXId())
                .idShort(defaultValue(shortIds.get(catenaXId())))
                .semanticModelId(semanticModelId.get())
                .semanticModel(SemanticModel.from(partTypeInformation, manufacturingInformation))
                .manufacturerId(manufacturerId())
                .manufacturerName(defaultValue(manufacturerName))
                .parentRelations(parentRelations)
                .childRelations(childRelations)
                .owner(owner)
                .activeAlert(false)
                .underInvestigation(false)
                .qualityType(QualityType.OK)
                .semanticDataModel(semanticDataModel.get())
                .van(van())
                .build();
    }

    public Asset toDomainAsPlanned(List<LocalId> localIds, Map<String, String> shortIds, Owner owner, Map<String, String> bpns, List<Descriptions> parentRelations, List<Descriptions> childRelations) {
        final String manufacturerName = bpns.get(manufacturerId());


        return Asset.builder()
                .id(catenaXId())
                .idShort(defaultValue(shortIds.get(catenaXId())))
                .semanticModel(SemanticModel.from(partTypeInformation))
                .manufacturerId(manufacturerId())
                .manufacturerName(defaultValue(manufacturerName))
                .parentRelations(parentRelations)
                .childRelations(childRelations)
                .owner(owner)
                .activeAlert(false)
                .underInvestigation(false)
                .qualityType(QualityType.OK)
                .semanticDataModel(org.eclipse.tractusx.traceability.assets.domain.model.SemanticDataModel.PARTASPLANNED)
                .van(van())
                .build();
    }

    private String manufacturerId() {
        return getLocalId(LocalIdKey.MANUFACTURER_ID)
                .orElse("--");
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

