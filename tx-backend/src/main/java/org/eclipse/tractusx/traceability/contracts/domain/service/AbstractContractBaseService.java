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

import org.eclipse.tractusx.irs.edc.client.contract.model.exception.ContractAgreementException;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.contracts.application.mapper.ContractFieldMapper;
import org.eclipse.tractusx.traceability.contracts.application.service.ContractService;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.eclipse.tractusx.traceability.contracts.domain.repository.ContractRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;


public abstract class AbstractContractBaseService implements ContractService {

    protected abstract ContractRepository getContractRepository();

    protected abstract ContractFieldMapper getContractFieldMapper();


    @Override
    public PageResult<Contract> getContracts(PageableFilterRequest pageableFilterRequest) {
        Pageable pageable = OwnPageable.toPageable(pageableFilterRequest.getOwnPageable(), getContractFieldMapper());
        SearchCriteria searchCriteria = pageableFilterRequest.getSearchCriteriaRequestParam().toSearchCriteria(getContractFieldMapper());
        return getContractRepository().getContractsByPageable(pageable, searchCriteria);
    }

    @Override
    public void saveContractAgreements(List<String> contractAgreementIds, ContractType contractType) throws ContractAgreementException {
        getContractRepository().saveAllContractAgreements(contractAgreementIds, contractType);
    }

    @Override
    public void saveAll(List<ContractAgreement> contractAgreements) {
        getContractRepository().saveAll(ContractAgreement.toEntityList(contractAgreements));
    }
}
