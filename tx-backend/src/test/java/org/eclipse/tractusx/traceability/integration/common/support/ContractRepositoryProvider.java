package org.eclipse.tractusx.traceability.integration.common.support;

import lombok.Getter;
import org.eclipse.tractusx.traceability.contracts.infrastructure.repository.ContractAsBuiltRepositoryImpl;
import org.eclipse.tractusx.traceability.contracts.infrastructure.repository.ContractAsPlannedRepositoryImpl;
import org.eclipse.tractusx.traceability.contracts.infrastructure.repository.ContractNotificationRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ContractRepositoryProvider {

    @Autowired
    ContractAsBuiltRepositoryImpl contractAsBuiltRepository;

    @Autowired
    ContractNotificationRepositoryImpl contractNotificationRepository;

    @Autowired
    ContractAsPlannedRepositoryImpl contractAsPlannedRepository;


}
