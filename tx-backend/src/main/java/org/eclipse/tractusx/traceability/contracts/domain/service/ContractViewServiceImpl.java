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
package org.eclipse.tractusx.traceability.contracts.domain.service;

import contract.request.ContractRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaMapper;
import org.eclipse.tractusx.traceability.contracts.application.mapper.ContractFieldMapper;
import org.eclipse.tractusx.traceability.contracts.application.service.ContractServiceReadOnly;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.eclipse.tractusx.traceability.contracts.infrastructure.repository.ContractViewRepositoryImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractViewServiceImpl implements ContractServiceReadOnly {

    private final ContractViewRepositoryImpl contractViewRepository;
    private final ContractFieldMapper contractFieldMapper;

    @Override
    public PageResult<Contract> getContracts(ContractRequest contractRequest) {
        Pageable pageable = OwnPageable.toPageable(OwnPageable.builder()
                .page(contractRequest.getPage())
                .size(contractRequest.getSize())
                .sort(contractRequest.getSort())
                .build(), contractFieldMapper);
        SearchCriteria searchCriteria = SearchCriteriaMapper.toSearchCriteria(contractFieldMapper, Collections.singletonList(contractRequest.getContractFilter()));
        searchCriteria.getSearchCriteriaFilterList().add(SearchCriteriaFilter.builder().key("contractAgreementId").strategy(SearchCriteriaStrategy.IS_NOT_NULL).operator(SearchCriteriaOperator.AND).build());
        return contractViewRepository.getContractsByPageable(pageable, searchCriteria);
    }

}
