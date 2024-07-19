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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcContractAgreementNegotiationResponse;
import org.eclipse.tractusx.irs.edc.client.contract.model.EdcContractAgreementsResponse;
import org.eclipse.tractusx.irs.edc.client.contract.model.exception.ContractAgreementException;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractAgreementService;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementBaseEntity;
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

@Component
@AllArgsConstructor
@Slf4j
public class ContractRepositoryImplBase {

    private final EdcContractAgreementService edcContractAgreementService;
    private final ObjectMapper objectMapper;


    public List<Contract> fetchEdcContractAgreements(List<ContractAgreementBaseEntity> contractAgreementEntities) throws ContractAgreementException {
        List<String> contractAgreementIds = contractAgreementEntities.stream().filter(Objects::nonNull).map(ContractAgreementBaseEntity::getContractAgreementId).filter(Objects::nonNull).toList();

        log.info("Trying to fetch contractAgreementIds from EDC: " + contractAgreementIds);

        List<EdcContractAgreementsResponse> contractAgreements = edcContractAgreementService.getContractAgreements(contractAgreementIds);

        validateContractAgreements(contractAgreementIds, contractAgreements);

        Map<String, ContractType> contractTypes = contractAgreementEntities.stream()
                .collect(Collectors.toMap(
                        ContractAgreementBaseEntity::getContractAgreementId,
                        ContractAgreementBaseEntity::getType,
                        (existing, replacement) -> existing // retain existing value if duplicate key is encountered
                ));

        Map<String, EdcContractAgreementNegotiationResponse> contractNegotiations = contractAgreements.stream()
                .map(agreement -> new ImmutablePair<>(agreement.contractAgreementId(),
                        edcContractAgreementService.getContractAgreementNegotiation(agreement.contractAgreementId()))
                ).collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));


        return contractAgreements.stream().map(contractAgreement ->
                {
                    try {

                        String globalAssetId = contractAgreementEntities.stream()
                                .filter(contractAgreementViewEntity -> contractAgreementViewEntity.getContractAgreementId() == null || contractAgreement.contractAgreementId() == null || contractAgreementViewEntity.getContractAgreementId().equals(contractAgreement.contractAgreementId()))
                                .findFirst()
                                .map(ContractAgreementBaseEntity::getGlobalAssetId)
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
        if (contractAgreementIds == null || contractAgreements == null) {
            log.warn("Either contractAgreementIds or contractAgreements is null.");
            return;
        }

        ArrayList<String> givenList = new ArrayList<>(contractAgreementIds);
        Collections.sort(givenList);

        List<String> expectedList = contractAgreements.stream().map(EdcContractAgreementsResponse::contractAgreementId)
                .filter(Objects::nonNull)// Ensure no null values are mapped
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
