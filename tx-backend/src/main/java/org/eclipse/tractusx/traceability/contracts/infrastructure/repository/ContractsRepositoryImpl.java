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
import org.eclipse.tractusx.traceability.contracts.domain.repository.ContractsRepository;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementInfoView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
public class ContractsRepositoryImpl implements ContractsRepository {

    private final EdcContractAgreementService edcContractAgreementService;
    private final JpaContractAgreementInfoViewRepository contractAgreementInfoViewRepository;

    @Override
    public PageResult<Contract> getContractsByPageable(Pageable pageable, SearchCriteria searchCriteria) {
        try {
            List<ContractSpecification> contractAgreementSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream()
                    .map(ContractSpecification::new)
                    .toList();
            Specification<ContractAgreementInfoView> specification = BaseSpecification.toSpecification(contractAgreementSpecifications);
            Page<ContractAgreementInfoView> contractAgreementInfoViews = contractAgreementInfoViewRepository.findAll(specification, pageable);

            return new PageResult<>(fetchEdcContractAgreements(contractAgreementInfoViews),
                    contractAgreementInfoViews.getPageable().getPageNumber(),
                    contractAgreementInfoViews.getTotalPages(),
                    contractAgreementInfoViews.getPageable().getPageSize(),
                    contractAgreementInfoViews.getTotalElements());

        } catch (ContractAgreementException e) {
            throw new ContractException(e);
        }

    }

    private List<Contract> fetchEdcContractAgreements(Page<ContractAgreementInfoView> contractAgreementInfoViews) throws ContractAgreementException {
        List<String> contractAgreementIds = contractAgreementInfoViews.getContent().stream().map(ContractAgreementInfoView::getContractAgreementId).toList();

        List<EdcContractAgreementsResponse> contractAgreements = edcContractAgreementService.getContractAgreements(contractAgreementIds);

        throwIfListDiverge(contractAgreementIds, contractAgreements);

        Map<String, EdcContractAgreementNegotiationResponse> contractNegotiations = contractAgreements.stream()
                .map(agreement -> new ImmutablePair<>(agreement.contractAgreementId(),
                        edcContractAgreementService.getContractAgreementNegotiation(agreement.contractAgreementId()))
                ).collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));


        return contractAgreements.stream().map(contractAgreement ->
                Contract.builder()
                        .contractId(contractAgreement.contractAgreementId())
                        .counterpartyAddress(contractNegotiations.get(contractAgreement.contractAgreementId()).counterPartyAddress())
                        .creationDate(Instant.ofEpochMilli(contractAgreement.contractSigningDate()))
                        .state(contractNegotiations.get(contractAgreement.contractAgreementId()).state())
                        .build()
        ).toList();
    }

    private void throwIfListDiverge(List<String> contractAgreementIds, List<EdcContractAgreementsResponse> contractAgreements) {
        ArrayList<String> givenList = new ArrayList<>(contractAgreementIds);
        Collections.sort(contractAgreementIds);

        List<String> expectedList = contractAgreements.stream()
                .sorted(Comparator.comparing(EdcContractAgreementsResponse::contractAgreementId))
                .map(EdcContractAgreementsResponse::contractAgreementId)
                .toList();

        if (!contractAgreementIds.equals(expectedList)) {
            contractAgreementIds.removeAll(expectedList);
            throw new ContractException("Can not find the following contract agreement Ids in EDC: " + givenList);
        }
    }

}
