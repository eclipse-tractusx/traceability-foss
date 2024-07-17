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
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementAsPlannedEntity;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementBaseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ContractAsPlannedRepositoryImpl extends ContractRepositoryImplBase implements ContractRepository<ContractAgreementAsPlannedEntity> {

    private final JpaContractAgreementAsPlannedRepository contractAgreementRepository;

    public ContractAsPlannedRepositoryImpl(EdcContractAgreementService edcContractAgreementService, ObjectMapper objectMapper, JpaContractAgreementAsPlannedRepository contractAgreementRepository) {
        super(edcContractAgreementService, objectMapper);
        this.contractAgreementRepository = contractAgreementRepository;
    }

    @Override
    public void saveAllContractAgreements(List<String> contractAgreementIds, ContractType contractType) throws ContractAgreementException {

        List<ContractAgreementAsPlannedEntity> contractAgreementEntities = contractAgreementIds.stream()
                .map(contractAgreementId -> ContractAgreementAsPlannedEntity.builder()
                        .contractAgreementId(contractAgreementId)
                        .type(contractType)
                        .build())
                .collect(Collectors.toList());

        List<ContractAgreementBaseEntity> baseEntities = contractAgreementEntities
                .stream()
                .map(entity -> (ContractAgreementBaseEntity) entity)
                .toList();


        List<Contract> contracts = fetchEdcContractAgreements(baseEntities);
        List<ContractAgreementAsPlannedEntity> contractAgreementsUpdated = ContractAgreementAsPlannedEntity.fromList(contracts, contractType);
        contractAgreementRepository.saveAll(contractAgreementsUpdated);
    }

    @Override
    public void saveAll(List<ContractAgreementAsPlannedEntity> contractAgreements) {
        contractAgreementRepository.saveAll(contractAgreements);
    }

}
