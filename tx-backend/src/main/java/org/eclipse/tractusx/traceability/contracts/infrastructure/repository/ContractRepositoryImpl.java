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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcContractAgreementNegotiationResponse;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcContractAgreementsResponse;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.ContractAgreementException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractAgreementService;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.eclipse.tractusx.traceability.contracts.domain.exception.ContractException;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.eclipse.tractusx.traceability.contracts.domain.repository.ContractRepository;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementView;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractRepositoryImpl implements ContractRepository {

    private final EdcContractAgreementService edcContractAgreementService;
    private final JpaContractAgreementInfoViewRepository contractAgreementInfoViewRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<Contract> getContractsByPageable(Pageable pageable, SearchCriteria searchCriteria) {
        try {
            List<ContractSpecification> contractAgreementSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream()
                    .map(ContractSpecification::new)
                    .toList();
            Specification<ContractAgreementView> specification = BaseSpecification.toSpecification(contractAgreementSpecifications);
            Page<ContractAgreementView> contractAgreementInfoViews = contractAgreementInfoViewRepository.findAll(specification, pageable);

            if (contractAgreementInfoViews.getContent().isEmpty()) {
                log.warn("Cannot find contract agreement Ids for asset ids in searchCriteria: " + searchCriteria.getSearchCriteriaFilterList());
                return new PageResult<>(List.of(), 0, 0, 0, 0L);
            }

            return new PageResult<>(fetchEdcContractAgreements(contractAgreementInfoViews.getContent()),
                    contractAgreementInfoViews.getPageable().getPageNumber(),
                    contractAgreementInfoViews.getTotalPages(),
                    contractAgreementInfoViews.getPageable().getPageSize(),
                    contractAgreementInfoViews.getTotalElements());

        } catch (ContractAgreementException e) {
            throw new ContractException(e);
        }

    }

    @Override
    public void saveAllContractAgreements(List<String> contractAgreementIds, ContractType contractType) throws ContractAgreementException {

        List<ContractAgreementView> contractAgreementViews = contractAgreementIds.stream()
                .map(contractAgreementId -> ContractAgreementView.builder()
                        .contractAgreementId(contractAgreementId)
                        .type(contractType)
                        .build())
                .collect(Collectors.toList());

        List<Contract> contracts = fetchEdcContractAgreements(contractAgreementViews);
        List<ContractAgreementView> contractAgreementViewsUpdated = Contract.toEntityList(contracts, contractType);
        contractAgreementInfoViewRepository.saveAll(contractAgreementViewsUpdated);
    }

    private List<Contract> fetchEdcContractAgreements(List<ContractAgreementView> contractAgreementInfoViews) throws ContractAgreementException {
        List<String> contractAgreementIds = contractAgreementInfoViews.stream().map(ContractAgreementView::getContractAgreementId).toList();
        log.info("Trying to fetch contractAgreementIds from EDC: " + contractAgreementIds);

        List<EdcContractAgreementsResponse> contractAgreements = edcContractAgreementService.getContractAgreements(contractAgreementIds);

        validateContractAgreements(contractAgreementIds, contractAgreements);

        Map<String, ContractType> contractTypes = contractAgreementInfoViews.stream()
                .collect(Collectors.toMap(ContractAgreementView::getContractAgreementId, ContractAgreementView::getType));

        Map<String, EdcContractAgreementNegotiationResponse> contractNegotiations = contractAgreements.stream()
                .map(agreement -> new ImmutablePair<>(agreement.contractAgreementId(),
                        edcContractAgreementService.getContractAgreementNegotiation(agreement.contractAgreementId()))
                ).collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));


        return contractAgreements.stream().map(contractAgreement ->
                {
                    try {
                        return Contract.builder()
                                .contractId(contractAgreement.contractAgreementId())
                                .counterpartyAddress(contractNegotiations.get(contractAgreement.contractAgreementId()).counterPartyAddress())
                                .creationDate(OffsetDateTime.ofInstant(Instant.ofEpochSecond(contractAgreement.contractSigningDate()), ZoneId.systemDefault()))
                                .state(contractNegotiations.get(contractAgreement.contractAgreementId()).state())
                                .policy(objectMapper.writeValueAsString(contractAgreement.policy()))
                                .type(contractTypes.get(contractAgreement.contractAgreementId()))
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).toList();
    }

    private void validateContractAgreements(List<String> contractAgreementIds, List<EdcContractAgreementsResponse> contractAgreements) {
        ArrayList<String> givenList = new ArrayList<>(contractAgreementIds);
        Collections.sort(givenList);

        List<String> expectedList = contractAgreements.stream()
                .sorted(Comparator.comparing(EdcContractAgreementsResponse::contractAgreementId))
                .map(EdcContractAgreementsResponse::contractAgreementId)
                .toList();
        log.info("EDC responded with the following contractAgreementIds: " + expectedList);

        if (!givenList.equals(expectedList)) {
            givenList.removeAll(expectedList);
            throw new ContractException("Can not find the following contract agreement Ids in EDC: " + givenList);
        }
    }

}
