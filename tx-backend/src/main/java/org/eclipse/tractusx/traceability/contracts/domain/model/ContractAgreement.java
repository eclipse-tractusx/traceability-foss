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
package org.eclipse.tractusx.traceability.contracts.domain.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementAsBuiltEntity;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementAsPlannedEntity;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementBaseEntity;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class ContractAgreement {

    private String id;
    private String contractAgreementId;
    @Enumerated(EnumType.STRING)
    private ContractType type;
    private Instant created;
    private Instant updated;
    private String globalAssetId;

    public static ContractAgreementBaseEntity toEntity(ContractAgreement contractAgreement) {
        return ContractAgreementBaseEntity.builder()
                .created(contractAgreement.getCreated())
                .contractAgreementId(contractAgreement.getContractAgreementId())
                .type(contractAgreement.getType())
                .updated(contractAgreement.getUpdated())
                .globalAssetId(contractAgreement.getGlobalAssetId())
                .build();
    }

    public static ContractAgreement toDomain(String contractAgreementId, String globalAssetId, ContractType contractType) {
        return ContractAgreement.builder()
                .contractAgreementId(contractAgreementId)
                .type(contractType)
                .globalAssetId(globalAssetId)
                .created(Instant.now())
                .updated(Instant.now())
                .build();
    }


    public static List<ContractAgreement> fromAsBuiltEntityToContractAgreements(List<ContractAgreementAsBuiltEntity> contractAgreementAsBuiltEntities) {

        return contractAgreementAsBuiltEntities.stream()
                .map(contractAgreement -> ContractAgreement
                        .builder()
                        .contractAgreementId(contractAgreement.getContractAgreementId())
                        .id(contractAgreement.getId())
                        .type(contractAgreement.getType())
                        .created(contractAgreement.getCreated())
                        .updated(contractAgreement.getUpdated())
                        .build())
                .toList();
    }

    public static List<ContractAgreement> fromAsPlannedEntityToContractAgreements(List<ContractAgreementAsPlannedEntity> contractAgreementAsPlannedEntities) {

        return contractAgreementAsPlannedEntities.stream()
                .map(contractAgreement -> ContractAgreement
                        .builder()
                        .contractAgreementId(contractAgreement.getContractAgreementId())
                        .id(contractAgreement.getId())
                        .type(contractAgreement.getType())
                        .created(contractAgreement.getCreated())
                        .updated(contractAgreement.getUpdated())
                        .build())
                .toList();
    }
}
