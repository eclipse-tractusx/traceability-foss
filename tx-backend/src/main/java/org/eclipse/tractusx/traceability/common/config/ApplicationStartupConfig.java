/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.common.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.application.base.service.AssetBaseService;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.contracts.application.service.ContractService;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.eclipse.tractusx.traceability.notification.application.contract.model.CreateNotificationContractRequest;
import org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationMethod;
import org.eclipse.tractusx.traceability.notification.application.contract.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.contract.EdcNotificationContractService;
import org.eclipse.tractusx.traceability.policies.domain.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.eclipse.tractusx.traceability.common.config.ApplicationProfiles.NOT_INTEGRATION_TESTS;


@Slf4j
@Component
@Profile(NOT_INTEGRATION_TESTS)
public class ApplicationStartupConfig {
    private final PolicyRepository policyRepository;
    private final AssetBaseService asPlannedService;
    private final AssetBaseService asBuiltService;
    private final ContractService contractService;

    @Autowired
    public ApplicationStartupConfig(PolicyRepository policyRepository,
                                    @Qualifier("assetAsBuiltServiceImpl") AssetBaseService asBuiltService,
                                    @Qualifier("assetAsPlannedServiceImpl") AssetBaseService asPlannedService,
                                    EdcNotificationContractService edcNotificationContractService,
                                    ContractService contractService) {
        this.policyRepository = policyRepository;
        this.asPlannedService = asPlannedService;
        this.asBuiltService = asBuiltService;
        this.edcNotificationContractService = edcNotificationContractService;
        this.contractService = contractService;
    }

    private final EdcNotificationContractService edcNotificationContractService;
    private static final List<CreateNotificationContractRequest> NOTIFICATION_CONTRACTS = List.of(
            new CreateNotificationContractRequest(NotificationType.QUALITY_ALERT, NotificationMethod.UPDATE),
            new CreateNotificationContractRequest(NotificationType.QUALITY_ALERT, NotificationMethod.RECEIVE),
            new CreateNotificationContractRequest(NotificationType.QUALITY_INVESTIGATION, NotificationMethod.UPDATE),
            new CreateNotificationContractRequest(NotificationType.QUALITY_INVESTIGATION, NotificationMethod.RECEIVE)
    );

    @EventListener(ApplicationReadyEvent.class)
    public void registerIrsPolicy() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                policyRepository.createPolicyBasedOnAppConfig();
            } catch (Exception exception) {
                log.error("Failed to create Irs Policies: ", exception);
            }
        });

        executor.shutdown();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createNotificationContracts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            log.info("on ApplicationReadyEvent create notification contracts.");
            try {
                NOTIFICATION_CONTRACTS.forEach(edcNotificationContractService::handle);
            } catch (Exception exception) {
                log.error("Failed to create notification contracts: ", exception);
            }
            log.info("on ApplicationReadyEvent notification contracts created.");
        });
        executor.shutdown();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void insertIntoContractAgreements() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            log.info("on ApplicationReadyEvent insert into contracts.");
            try {
                log.info("Method yourMethod() started.");

                List<AssetBase> asBuilt = asBuiltService.findAll();
                List<AssetBase> asPlanned = asPlannedService.findAll();

                log.info("Retrieved assets: asBuilt={}, asPlanned={}", asBuilt, asPlanned);

                List<ContractAgreement> contractAgreementIdsAsBuilt = asBuilt.stream()
                        .filter(asBuiltAsset -> asBuiltAsset.getContractAgreementId() != null)  // Filtering out null contractAgreementIds
                        .map(asBuiltAsset -> ContractAgreement.builder()
                                .type(ContractType.ASSET_AS_BUILT)
                                .contractAgreementId(asBuiltAsset.getContractAgreementId())
                                .id(asBuiltAsset.getId())
                                .created(Instant.now())
                                .build())
                        .toList();

                List<ContractAgreement> contractAgreementIdsAsPlanned = asPlanned.stream()
                        .filter(asPlannedAsset -> asPlannedAsset.getContractAgreementId() != null)  // Filtering out null contractAgreementIds
                        .map(asPlannedAsset -> ContractAgreement.builder()
                                .type(ContractType.ASSET_AS_PLANNED)  // Assuming the type should be ASSET_AS_PLANNED for asPlanned list
                                .contractAgreementId(asPlannedAsset.getContractAgreementId())
                                .id(asPlannedAsset.getId())
                                .created(Instant.now())
                                .build())
                        .toList();


                log.info("Created ContractAgreements: asBuilt={}, asPlanned={}", contractAgreementIdsAsBuilt, contractAgreementIdsAsPlanned);

                List<ContractAgreement> mergedAgreements = Stream.concat(contractAgreementIdsAsBuilt.stream(), contractAgreementIdsAsPlanned.stream())
                        .toList();
                log.info("Merged agreements: {}", mergedAgreements);

                contractService.saveAll(mergedAgreements);
                log.info("Saved merged agreements successfully.");

            } catch (Exception exception) {
                log.error("Failed to insert contracts: ", exception);
            }
            log.info("on ApplicationReadyEvent insert into contracts successfully done.");
        });
        executor.shutdown();
    }


}
