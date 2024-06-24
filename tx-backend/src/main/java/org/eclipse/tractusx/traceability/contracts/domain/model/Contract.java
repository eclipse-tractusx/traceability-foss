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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementView;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractType;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Data
@Builder
public class Contract {
    private String contractId;
    private String counterpartyAddress;
    private OffsetDateTime creationDate;
    private OffsetDateTime endDate;
    private String state;
    private String policy;

    public static ContractAgreementView toEntity(Contract contract, ContractType contractType) {
        return ContractAgreementView.builder()
                .id(contract.getContractId())
                .contractAgreementId(contract.getContractId())
                .type(contractType)
                .created(Instant.now())
                .build();
    }

    public static List<ContractAgreementView> toEntityList(List<Contract> contracts, ContractType contractType) {
        return contracts.stream().map(contract -> Contract.toEntity(contract, contractType)).toList();
    }
}
