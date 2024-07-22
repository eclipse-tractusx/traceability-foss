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
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.eclipse.tractusx.traceability.contracts.domain.exception.ContractException;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.eclipse.tractusx.traceability.contracts.domain.repository.ContractRepositoryReadOnly;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementBaseEntity;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementViewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@Slf4j
public class ContractViewRepositoryImpl extends ContractRepositoryImplBase implements ContractRepositoryReadOnly<ContractAgreementViewEntity> {

    private final JpaContractAgreementViewRepository contractAgreementRepository;

    public ContractViewRepositoryImpl(EdcContractAgreementService edcContractAgreementService,
                                      ObjectMapper objectMapper,
                                      JpaContractAgreementViewRepository contractAgreementRepository) {
        super(edcContractAgreementService, objectMapper);
        this.contractAgreementRepository = contractAgreementRepository;
    }

    @Override
    public PageResult<Contract> getContractsByPageable(Pageable pageable, SearchCriteria searchCriteria) {
        try {
            List<ContractAgreementViewSpecification> contractAgreementSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream()
                    .map(ContractAgreementViewSpecification::new)
                    .toList();
            Specification<ContractAgreementViewEntity> specification = BaseSpecification.toSpecification(contractAgreementSpecifications);
            Page<ContractAgreementViewEntity> contractAgreementEntities = contractAgreementRepository.findAll(specification, pageable);

            if (contractAgreementEntities.getContent().isEmpty()) {
                log.warn("Cannot find contract agreement Ids for asset ids in searchCriteria: " + searchCriteria.getSearchCriteriaFilterList());
                return new PageResult<>(List.of(), 0, 0, 0, 0L);
            }
            List<ContractAgreementBaseEntity> baseEntities = contractAgreementEntities.getContent()
                    .stream()
                    .map(ContractAgreementBaseEntity.class::cast)
                    .toList();
            List<Contract> contracts = fetchEdcContractAgreements(baseEntities)
                    .stream()
                    .filter(contract -> contract.getContractId() != null)
                    .filter(contract -> contract.getGlobalAssetId() != null && !contract.getType().equals(ContractType.NOTIFICATION)).toList();

            return new PageResult<>(contracts,
                    contractAgreementEntities.getPageable().getPageNumber(),
                    contractAgreementEntities.getTotalPages(),
                    contractAgreementEntities.getPageable().getPageSize(),
                    contractAgreementEntities.getTotalElements());

        } catch (ContractAgreementException e) {
            throw new ContractException(e);
        }

    }
}
