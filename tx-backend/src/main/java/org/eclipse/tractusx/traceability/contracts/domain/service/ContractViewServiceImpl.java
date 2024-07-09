package org.eclipse.tractusx.traceability.contracts.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.contracts.application.mapper.ContractFieldMapper;
import org.eclipse.tractusx.traceability.contracts.domain.repository.ContractRepository;
import org.eclipse.tractusx.traceability.contracts.infrastructure.repository.ContractViewRepositoryImpl;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractViewServiceImpl extends AbstractContractBaseService {

    private final ContractViewRepositoryImpl contractViewRepository;
    private final ContractFieldMapper contractFieldMapper;

    @Override
    protected ContractRepository getContractRepository() {
        return contractViewRepository;
    }

    @Override
    protected ContractFieldMapper getContractFieldMapper() {
        return contractFieldMapper;
    }

}
