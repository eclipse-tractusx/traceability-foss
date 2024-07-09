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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "contract_agreement_full")
public class ContractAgreementViewEntity extends ContractAgreementBaseEntity {
    public static ContractAgreementViewEntity from(Contract contract, ContractType contractType) {
        return ContractAgreementViewEntity.builder()
                .id(contract.getContractId())
                .contractAgreementId(contract.getContractId())
                .type(contractType)
                .created(Instant.now())
                .build();
    }

    public static List<ContractAgreementViewEntity> fromList(List<Contract> contracts, ContractType contractType) {
        return contracts.stream().map(contract -> ContractAgreementViewEntity.from(contract, contractType)).toList();
    }

    public static ContractAgreementViewEntity fromDomainToEntity(ContractAgreement contractAgreement) {
        return ContractAgreementViewEntity.builder()
                .id(contractAgreement.getId())
                .contractAgreementId(contractAgreement.getContractAgreementId())
                .type(contractAgreement.getType())
                .created(contractAgreement.getCreated())
                .updated(contractAgreement.getUpdated()).build();
    }

    public static List<ContractAgreementViewEntity> fromDomainToEntityList(List<ContractAgreement> contractAgreements) {
        return contractAgreements.stream().map(ContractAgreementViewEntity::fromDomainToEntity).toList();
    }
}
