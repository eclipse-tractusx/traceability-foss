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
package org.eclipse.tractusx.traceability.contracts.infrastructure.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;

import java.time.Instant;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "contract_agreement_as_planned")
public class ContractAgreementAsPlannedEntity extends ContractAgreementBaseEntity {

    @ManyToOne
    @JoinColumn(name = "globalAssetId", referencedColumnName = "id", insertable = false, updatable = false)
    private AssetAsPlannedEntity assetAsPlanned;

    public static ContractAgreementAsPlannedEntity from(Contract contract, ContractType contractType) {
        return ContractAgreementAsPlannedEntity.builder()
                .globalAssetId(null)
                .contractAgreementId(contract.getContractId())
                .type(contractType)
                .created(Instant.now())
                .build();
    }

    public static List<ContractAgreementAsPlannedEntity> fromList(List<Contract> contracts, ContractType contractType) {
        return emptyIfNull(contracts).stream().map(contract -> ContractAgreementAsPlannedEntity.from(contract, contractType)).toList();
    }

    public static ContractAgreementAsPlannedEntity fromDomainToEntity(ContractAgreement contractAgreement) {
        return ContractAgreementAsPlannedEntity.builder()
                .contractAgreementId(contractAgreement.getContractAgreementId())
                .type(contractAgreement.getType())
                .created(contractAgreement.getCreated())
                .updated(contractAgreement.getUpdated()).build();
    }

    public static List<ContractAgreementAsPlannedEntity> fromDomainToEntityList(List<ContractAgreement> contractAgreements) {
        return emptyIfNull(contractAgreements).stream().map(ContractAgreementAsPlannedEntity::fromDomainToEntity).toList();
    }
}
