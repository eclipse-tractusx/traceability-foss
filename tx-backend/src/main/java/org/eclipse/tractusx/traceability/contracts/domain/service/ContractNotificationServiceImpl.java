package org.eclipse.tractusx.traceability.contracts.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.contracts.application.mapper.ContractFieldMapper;
import org.eclipse.tractusx.traceability.contracts.domain.repository.ContractRepository;
import org.eclipse.tractusx.traceability.contracts.infrastructure.repository.ContractNotificationRepositoryImpl;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractNotificationServiceImpl extends AbstractContractBaseService {

    private final ContractNotificationRepositoryImpl contractNotificationRepository;
    private final ContractFieldMapper contractFieldMapper;

    @Override
    protected ContractRepository getContractRepository() {
        return contractNotificationRepository;
    }

    @Override
    protected ContractFieldMapper getContractFieldMapper() {
        return contractFieldMapper;
    }

}
