package org.eclipse.tractusx.traceability.contracts.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.contracts.application.mapper.ContractFieldMapper;
import org.eclipse.tractusx.traceability.contracts.application.service.ContractServiceReadOnly;
import org.eclipse.tractusx.traceability.contracts.domain.model.Contract;
import org.eclipse.tractusx.traceability.contracts.infrastructure.repository.ContractViewRepositoryImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractViewServiceImpl implements ContractServiceReadOnly {

    private final ContractViewRepositoryImpl contractViewRepository;
    private final ContractFieldMapper contractFieldMapper;

    @Override
    public PageResult<Contract> getContracts(PageableFilterRequest pageableFilterRequest) {
        Pageable pageable = OwnPageable.toPageable(pageableFilterRequest.getOwnPageable(), contractFieldMapper);
        SearchCriteria searchCriteria = pageableFilterRequest.getSearchCriteriaRequestParam().toSearchCriteria(contractFieldMapper);
        return contractViewRepository.getContractsByPageable(pageable, searchCriteria);
    }

}
