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
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementAsBuiltEntity;
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

    public static ContractAgreementBaseEntity toEntity(ContractAgreement contractAgreement) {
        return ContractAgreementAsBuiltEntity.builder()
                .created(contractAgreement.getCreated())
                .id(contractAgreement.getId())
                .contractAgreementId(contractAgreement.getContractAgreementId())
                .type(contractAgreement.getType())
                .updated(contractAgreement.getUpdated())
                .build();
    }

    public static List<ContractAgreementBaseEntity> toEntityList(List<ContractAgreement> contractAgreementList) {
        return contractAgreementList.stream().map(ContractAgreement::toEntity).toList();
    }

    public static ContractAgreement toContractAgreement(AssetBase assetBase, ContractType contractType){
        return ContractAgreement.builder().id(assetBase.getId())
                .contractAgreementId(assetBase.getContractAgreementId()).type(contractType).created(Instant.now()).build();
    }


}
