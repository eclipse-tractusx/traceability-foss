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
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementBaseEntity;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementNotificationEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ContractNotificationRepositoryImpl extends ContractRepositoryImplBase implements ContractRepository<ContractAgreementNotificationEntity> {

    private final JpaContractAgreementNotificationRepository contractAgreementRepository;

    public ContractNotificationRepositoryImpl(EdcContractAgreementService edcContractAgreementService, ObjectMapper objectMapper, EdcContractAgreementService edcContractAgreementService1, JpaContractAgreementNotificationRepository contractAgreementRepository) {
        super(edcContractAgreementService, objectMapper);
        this.contractAgreementRepository = contractAgreementRepository;
    }

    @Override
    public void saveAllContractAgreements(List<String> contractAgreementIds, ContractType contractType) throws ContractAgreementException {

        List<ContractAgreementBaseEntity> contractAgreementEntities = contractAgreementIds.stream()
                .map(contractAgreementId -> ContractAgreementNotificationEntity.builder()
                        .contractAgreementId(contractAgreementId)
                        .type(contractType)
                        .build())
                .map(entity -> (ContractAgreementBaseEntity) entity)
                .collect(Collectors.toList());

        List<Contract> contracts = fetchEdcContractAgreements(contractAgreementEntities);
        List<ContractAgreementNotificationEntity> contractAgreementsUpdated = ContractAgreementNotificationEntity.fromList(contracts, contractType);
        contractAgreementRepository.saveAll(contractAgreementsUpdated);
    }

    @Override
    public void saveAll(List<ContractAgreementNotificationEntity> contractAgreements) {
        contractAgreementRepository.saveAll(contractAgreements);
    }

}
