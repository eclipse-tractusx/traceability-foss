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
package org.eclipse.tractusx.traceability.contracts.infrastructure.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.ContractAgreementException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractAgreementService;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.eclipse.tractusx.traceability.contracts.domain.repository.ContractRepository;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementAsBuiltEntity;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementBaseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ContractAsBuiltRepositoryImpl extends ContractRepositoryImplBase implements ContractRepository<ContractAgreementAsBuiltEntity> {

    private final JpaContractAgreementAsBuiltRepository contractAgreementRepository;

    public ContractAsBuiltRepositoryImpl(EdcContractAgreementService edcContractAgreementService, ObjectMapper objectMapper, JpaContractAgreementAsBuiltRepository contractAgreementRepository) {
        super(edcContractAgreementService, objectMapper);
        this.contractAgreementRepository = contractAgreementRepository;
    }

    @Override
    public void saveAllContractAgreements(List<String> contractAgreementIds, ContractType contractType) throws ContractAgreementException {

        List<ContractAgreementBaseEntity> contractAgreementEntities = contractAgreementIds.stream()
                .map(contractAgreementId -> ContractAgreementAsBuiltEntity.builder()
                        .contractAgreementId(contractAgreementId)
                        .type(contractType)
                        .build())
                .map(ContractAgreementBaseEntity.class::cast)
                .toList();

        List<Contract> contracts = fetchEdcContractAgreements(contractAgreementEntities);
        List<ContractAgreementAsBuiltEntity> contractAgreementsUpdated = ContractAgreementAsBuiltEntity.fromList(contracts, contractType);
        contractAgreementRepository.saveAll(contractAgreementsUpdated);
    }

    @Override
    public void saveAll(List<ContractAgreementAsBuiltEntity> contractAgreements) {
        contractAgreementRepository.saveAll(contractAgreements);
    }


}
