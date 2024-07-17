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
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.eclipse.tractusx.traceability.contracts.domain.repository.ContractRepository;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementViewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContractViewRepositoryImpl implements ContractRepository<ContractAgreementViewEntity> {

    private final EdcContractAgreementService edcContractAgreementService;
    private final JpaContractAgreementViewRepository contractAgreementRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PageResult<Contract> getContractsByPageable(Pageable pageable, SearchCriteria searchCriteria) {
        try {
            List<ContractAgreementViewSpecification> contractAgreementSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream()
                    .map(ContractAgreementViewSpecification::new)
                    .toList();
            Specification<ContractAgreementViewEntity> specification = BaseSpecification.toSpecification(contractAgreementSpecifications);
            Page<ContractAgreementViewEntity> contractAgreementEntities = contractAgreementRepository.findAll(specification, pageable);
            logAllContractAgreementEntities(contractAgreementEntities);

            if (contractAgreementEntities.getContent().isEmpty()) {
                log.warn("Cannot find contract agreement Ids for asset ids in searchCriteria: " + searchCriteria.getSearchCriteriaFilterList());
                return new PageResult<>(List.of(), 0, 0, 0, 0L);
            }

            return new PageResult<>(fetchEdcContractAgreements(contractAgreementEntities.getContent()),
                    contractAgreementEntities.getPageable().getPageNumber(),
                    contractAgreementEntities.getTotalPages(),
                    contractAgreementEntities.getPageable().getPageSize(),
                    contractAgreementEntities.getTotalElements());

        } catch (ContractAgreementException e) {
            throw new ContractException(e);
        }

    }

    private void logAllContractAgreementEntities(Page<ContractAgreementViewEntity> contractAgreementEntities) {

        for (ContractAgreementViewEntity entity : contractAgreementEntities) {
            try {
                String jsonString = objectMapper.writeValueAsString(entity);
                log.info("ContractAgreementViewEntity: {}", jsonString);
            } catch (JsonProcessingException e) {
                log.error("Error converting ContractAgreementViewEntity to JSON string", e);
            }
        }
    }

    @Override
    public void saveAllContractAgreements(List<String> contractAgreementIds, ContractType contractType) throws ContractAgreementException {

    }

    @Override
    public void saveAll(List<ContractAgreementViewEntity> contractAgreements) {

    }

    @Override
    public void save(ContractAgreement contractAgreement) {

    }

    @Override
    public List<ContractAgreementViewEntity> findAll() {
        return List.of();
    }


    private List<Contract> fetchEdcContractAgreements(List<ContractAgreementViewEntity> contractAgreementEntities) throws ContractAgreementException {
        List<String> contractAgreementIds = contractAgreementEntities.stream().filter(Objects::nonNull).map(ContractAgreementViewEntity::getContractAgreementId).filter(Objects::nonNull).toList();
        log.info("Trying to fetch contractAgreementIds from EDC: " + contractAgreementIds);

        List<EdcContractAgreementsResponse> contractAgreements = edcContractAgreementService.getContractAgreements(contractAgreementIds);

        validateContractAgreements(contractAgreementIds, contractAgreements);


        Map<String, ContractType> contractTypes = contractAgreementEntities.stream()
                .collect(Collectors.toMap(
                        ContractAgreementViewEntity::getContractAgreementId,
                        ContractAgreementViewEntity::getType,
                        (existing, replacement) -> {
                            // Define your merging logic here
                            // For example, keep the existing value
                            return existing;

                            // Alternatively, you could choose to log the conflict or merge in another way
                            // e.g., return some merged version of existing and replacement
                        }
                ));

        // Print the result
        contractTypes.forEach((key, value) -> log.info("{}: {}", key, value));



        Map<String, EdcContractAgreementNegotiationResponse> contractNegotiations = contractAgreements.stream()
                .map(agreement -> new ImmutablePair<>(agreement.contractAgreementId(),
                        edcContractAgreementService.getContractAgreementNegotiation(agreement.contractAgreementId()))
                ).collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));


        return contractAgreements.stream().map(contractAgreement ->
                {
                    try {
                        String globalAssetId = contractAgreementIds.stream()
                                .filter(id -> id.equals(contractAgreement.contractAgreementId()))
                                .findFirst()
                                .orElse(null);
                        return Contract.builder()
                                .contractId(contractAgreement.contractAgreementId())
                                .globalAssetId(globalAssetId)
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
                .map(EdcContractAgreementsResponse::contractAgreementId)
                .sorted()
                .toList();
        log.info("EDC responded with the following contractAgreementIds: " + expectedList);

        // Filter the givenList to find out which IDs are missing in the expectedList
        List<String> missingIds = givenList.stream()
                .filter(id -> !expectedList.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            log.warn("Cannot find the following contract agreement IDs in EDC: " + missingIds);
        }
    }

}
