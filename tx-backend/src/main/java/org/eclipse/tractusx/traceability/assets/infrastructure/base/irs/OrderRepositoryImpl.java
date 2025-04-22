/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs;

import assets.request.PartChainIdentificationKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.assetadministrationshell.SubmodelDescriptor;
import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.aas.domain.model.AAS.AASBuilder;
import org.eclipse.tractusx.traceability.aas.domain.model.Actor;
import org.eclipse.tractusx.traceability.aas.domain.repository.AASRepository;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.OrderRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterOrderRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsBatchResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Shell;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.IrsResponseAssetMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.JobState;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.configuration.domain.model.Order;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.BatchEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.OrderJPARepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.TriggerConfigurationRepository;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_BUILT;
import static org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_PLANNED;
import static org.eclipse.tractusx.traceability.common.security.Sanitizer.sanitize;

@Slf4j
@Service
public class OrderRepositoryImpl implements OrderRepository {

    private final TraceabilityProperties traceabilityProperties;
    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private static final ProcessingState STATUS_COMPLETED = ProcessingState.COMPLETED;
    private static final ProcessingState STATUS_PARTIAL = ProcessingState.PARTIAL;
    private static final JobState JOB_STATUS_COMPLETED = JobState.COMPLETED;
    private final IrsResponseAssetMapper assetMapperFactory;
    private final OrderClient orderClient;
    private final ObjectMapper objectMapper;
    private final JobClient jobClient;
    private final OrderJPARepository orderJPARepository;
    private final AASRepository aasRepository;
    private final TriggerConfigurationRepository triggerConfigurationRepository;

    public OrderRepositoryImpl(
            OrderClient orderClient,
            TraceabilityProperties traceabilityProperties,
            @Qualifier("assetAsBuiltRepositoryImpl")
            AssetAsBuiltRepository assetAsBuiltRepository,
            @Qualifier("assetAsPlannedRepositoryImpl")
            AssetAsPlannedRepository assetAsPlannedRepository,
            IrsResponseAssetMapper assetMapperFactory,
            ObjectMapper objectMapper,
            JobClient jobClient,
            OrderJPARepository orderJPARepository,
            AASRepository aasRepository,
            TriggerConfigurationRepository triggerConfigurationRepository) {
        this.traceabilityProperties = traceabilityProperties;
        this.assetAsBuiltRepository = assetAsBuiltRepository;
        this.assetAsPlannedRepository = assetAsPlannedRepository;
        this.orderClient = orderClient;
        this.assetMapperFactory = assetMapperFactory;
        this.objectMapper = objectMapper;
        this.jobClient = jobClient;
        this.orderJPARepository = orderJPARepository;
        this.aasRepository = aasRepository;
        this.triggerConfigurationRepository = triggerConfigurationRepository;
    }

    @Override
    public String createOrderToResolveAssets(
            List<PartChainIdentificationKey> keys,
            Direction direction,
            List<String> aspects,
            BomLifecycle bomLifecycle,
            OrderConfiguration orderConfiguration) {
        final String callbackUrl = traceabilityProperties.getUrl();
        RegisterOrderRequest registerOrderRequest = RegisterOrderRequest.buildOrderRequest(aspects, bomLifecycle, callbackUrl, direction, keys, orderConfiguration);
        return orderClient.registerOrder(registerOrderRequest).id();
    }

    @Override
    @Transactional
    public void handleOrderFinishedCallback(String orderId, String batchId, ProcessingState orderState, ProcessingState batchState) {
        log.info("Handling order finished callback for orderId: {}, batchId: {}, orderState: {}, batchState: {}",
                sanitize(orderId), sanitize(batchId), orderState, batchState);

        updateOrderTable(orderId, orderState, batchId, batchState);

    }

    @Transactional
    @Override
    public void requestOrderBatchAndMapAssets(String orderId, String batchId, ProcessingState batchState) {
        try {
            if (!STATUS_COMPLETED.equals(batchState) && (!STATUS_PARTIAL.equals(batchState))) {
                log.info("Skipping callback handling for orderId: {}, batchId: {} because batchState is (actual: {}).",
                        sanitize(orderId), sanitize(batchId), batchState);
                return;
            }

            final IrsBatchResponse irsBatchResponse = this.orderClient.getBatchByOrder(orderId, batchId);

            if (irsBatchResponse == null) {
                log.warn("Received null response for batch details from IRS for orderId: {}, batchId: {}.",
                        sanitize(orderId), sanitize(batchId));
                return;
            }

            if (irsBatchResponse.jobs().isEmpty()) {
                log.warn("IRS batch response for orderId: {}, batchId: {} contains no jobs.", sanitize(orderId), sanitize(batchId));
                return;
            }

            log.info("Processing {} jobs in IRS batch response for orderId: {}, batchId: {}.",
                    irsBatchResponse.jobs().size(), sanitize(orderId), sanitize(batchId));

            irsBatchResponse.jobs().forEach(jobRecord -> {
                log.debug("Processing job with ID: {} for orderId: {}, batchId: {}.", jobRecord.id(), sanitize(orderId), sanitize(batchId));
                IRSResponse irsJobDetailResponse = jobClient.getIrsJobDetailResponse(jobRecord.id());

                if (irsJobDetailResponse == null) {
                    log.warn("Skipping job with ID: {} as IRS job detail response is null.", jobRecord.id());
                    return;
                }
                if (irsJobDetailResponse.jobStatus() == null || irsJobDetailResponse.jobStatus().state() == null) {
                    log.warn("Skipping job with ID: {} as IRS job status or its state is null.", jobRecord.id());
                    return;
                }
                if (STATUS_PARTIAL.equals(batchState)) {
                    JobState jobState = JobState.fromString(irsJobDetailResponse.jobStatus().state());
                    if (!JOB_STATUS_COMPLETED.equals(jobState)) {
                        log.info("Skipping job with ID: {} in PARTIAL batch as it is not COMPLETED (actual: {}).",
                                jobRecord.id(), irsJobDetailResponse.jobStatus().state());
                        return;
                    }
                }
                List<AssetBase> assets = assetMapperFactory.toAssetBaseList(irsJobDetailResponse);

                assets.forEach(assetBase -> {
                    addTombstoneDetails(assetBase);
                    if (assetBase.getBomLifecycle() == AS_BUILT) {
                        saveOrUpdateAssets(assetAsBuiltRepository, assetBase);
                    } else if (assetBase.getBomLifecycle() == AS_PLANNED) {
                        saveOrUpdateAssets(assetAsPlannedRepository, assetBase);
                    }
                    updateAas(assetBase, irsJobDetailResponse);
                });
            });
        } catch (Exception e) {
            log.warn("Exception occurred while processing order batch for orderId: {}, batchId: {} Error: {}.", sanitize(orderId), sanitize(batchId), e.getMessage());
            try {
                orderJPARepository.findById(orderId).ifPresent(orderEntity -> {
                    orderEntity.setStatus(ProcessingState.ERROR);
                    orderJPARepository.save(orderEntity);
                    log.info("Order with ID: {} set to state ERROR.", sanitize(orderId));
                });
            } catch (Exception ex) {
                log.error("Failed to update order status to ERROR for orderId: {} due to: {}", sanitize(orderId), ex.getMessage(), ex);
            }
        }
    }

    // TODO this can be moved to assetmapperfactory
    private void updateAas(AssetBase assetBase, IRSResponse irsJobDetailResponse) {
        String aasIdentifier = irsJobDetailResponse.jobStatus().aasIdentifier();
        TriggerConfiguration triggerConfiguration = triggerConfigurationRepository.findTopByCreatedAtDesc();

        if (aasIdentifier != null) {
            List<Shell> shells = irsJobDetailResponse.shells().stream()
                    .filter(shell -> shell.payload().id().equals(aasIdentifier))
                    .toList();

            List<String> submodelDescriptorIds = shells.stream()
                    .flatMap(shell -> shell.payload().submodelDescriptors().stream().map(SubmodelDescriptor::getId))
                    .toList();

            irsJobDetailResponse.submodels().stream()
                    .filter(submodel -> submodelDescriptorIds.contains(submodel.getIdentification()))
                    .forEach(submodel -> assetMapperFactory.extractSubmodelDescription(submodel)
                            .map(Descriptions::parentId)
                            .ifPresentOrElse(globalAssetId -> aasRepository.findById(aasIdentifier)
                                            .ifPresentOrElse(aas -> {
                                                        log.info("AAS with ID: {} already exists in the database, updating it", aasIdentifier);
                                                        if (assetBase.getBomLifecycle() == AS_BUILT) {
                                                            aas.setAssetAsBuilt(assetBase);
                                                        } else {
                                                            aas.setAssetAsPlanned(assetBase);
                                                        }
                                                    }, () -> {
                                                        log.info("AAS with ID: {} not found in the database, creating new one",
                                                                aasIdentifier);
                                                        Integer aasTTL = triggerConfiguration.getAasTTL();
                                                        AASBuilder aasBuilder = AAS.builder()
                                                                .aasId(aasIdentifier)
                                                                .bpn(AAS.bpnFromString(assetBase.getManufacturerId()))
                                                                .created(LocalDateTime.now())
                                                                .updated(LocalDateTime.now())
                                                                .actor(Actor.SYSTEM)
                                                                .digitalTwinType(assetBase.getSemanticDataModel().isAsBuilt() ?
                                                                        DigitalTwinType.PART_TYPE : DigitalTwinType.PART_INSTANCE)
                                                                .ttl(aasTTL)
                                                                .expiryDate(LocalDateTime.now().plusSeconds(aasTTL / 1000));
                                                        if (assetBase.getBomLifecycle() == AS_BUILT) {
                                                            aasBuilder.assetAsBuilt(assetBase);
                                                        } else {
                                                            aasBuilder.assetAsPlanned(assetBase);
                                                        }
                                                        aasRepository.save(List.of(aasBuilder.build()));
                                                    }
                                            ),
                                    () -> log.info("Global asset id could not be extracted from Irs submodel response.")
                            ));
        }
        log.info("No aasIdentifier found in job details response: %s".formatted(irsJobDetailResponse.jobStatus().id()));
    }

    private void updateOrderTable(String orderId, ProcessingState orderState, String batchId, ProcessingState batchState) {
        orderJPARepository.findById(orderId).ifPresentOrElse(orderEntity -> {

            if (orderState != null) {
                orderEntity.setStatus(orderState);
            }

            if (batchId != null && batchState != null) {
                List<BatchEntity> batches = orderEntity.getBatches();
                batches.stream()
                        .filter(batch ->  batchId.equals(batch.getId()))
                        .findFirst()
                        .orElseGet(() -> {
                            BatchEntity newBatch = BatchEntity.builder()
                                    .id(batchId)
                                    .order(orderEntity)
                                    .status(batchState)
                                    .build();
                            orderEntity.addBatch(newBatch);
                            return newBatch;
                        });

            }

            orderJPARepository.save(orderEntity);
            log.info("Order with ID: {} updated in the database", sanitize(orderId));

        }, () -> log.warn("Order with ID: {} not found in the database.", sanitize(orderId)));
    }


    void addTombstoneDetails(AssetBase tombstone) {
        if (tombstone.getSemanticDataModel().equals(SemanticDataModel.TOMBSTONEASBUILT)) {
            assetAsBuiltRepository
                    .findById(tombstone.getId())
                    .ifPresent(oldAsset -> {
                        tombstone.setManufacturerId(oldAsset.getManufacturerId());
                        if (oldAsset.getOwner() != null && tombstone.getOwner() == Owner.UNKNOWN) {
                            tombstone.setOwner(oldAsset.getOwner());
                        }
                    });
        } else if (tombstone.getSemanticDataModel().equals(SemanticDataModel.TOMBSTONEASPLANNED)) {
            assetAsPlannedRepository
                    .findById(tombstone.getId())
                    .ifPresent(oldAsset -> {
                        tombstone.setManufacturerId(oldAsset.getManufacturerId());
                        if (oldAsset.getOwner() != null && tombstone.getOwner() == Owner.UNKNOWN) {
                            tombstone.setOwner(oldAsset.getOwner());
                        }
                    });
        }
    }

    void saveOrUpdateAssets(AssetRepository repository, AssetBase asset) {
        try {
            enrichAssetBaseByContractAgreements(repository, asset);
            if (asset.getImportState() != ImportState.ERROR) {
                asset.setTtl(triggerConfigurationRepository.findTopByCreatedAtDesc().getAasTTL());
                asset.setExpirationDate(LocalDateTime.now().plusSeconds(triggerConfigurationRepository.findTopByCreatedAtDesc().getAasTTL() / 1000));
                log.info("Asset with id {} has been updated with expiration date {}", asset.getId(), asset.getExpirationDate());
            } else {
                log.info("Asset with id {} has an import state of ERROR, skipping updating assets expiration date",
                        asset.getId());
            }
            repository.save(asset);
        } catch (DataIntegrityViolationException ex) {
            //retry save in case of ERROR: duplicate key value violates unique constraint "asset_pkey"
            log.info("Asset with id {} already exists in the database. The record will be updated instead.", asset.getId());
            repository.save(asset);
        }
    }

    private void enrichAssetBaseByContractAgreements(AssetRepository repository, AssetBase asset) {
        Optional<AssetBase> byId = repository.findById(asset.getId());
        List<ContractAgreement> agreementsToAdd = new ArrayList<>();
        byId.ifPresent(assetBase -> agreementsToAdd.addAll(assetBase.getContractAgreements()));
        try {
            log.info("Found the following existing contractAgreements {}", this.objectMapper.writeValueAsString(agreementsToAdd));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ContractType contractType = asset.getSemanticDataModel().isAsBuilt() ? ContractType.ASSET_AS_BUILT : ContractType.ASSET_AS_PLANNED;
        if (asset.getLatestContractAgreementId() != null) {
            agreementsToAdd.add(ContractAgreement.toDomain(asset.getLatestContractAgreementId(), asset.getId(), contractType));
        }
        asset.setContractAgreements(agreementsToAdd);
        try {
            log.info("Found the following NEW contractAgreements {}", this.objectMapper.writeValueAsString(agreementsToAdd));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Order order) {
        orderJPARepository
                .save(OrderEntity.builder()
                        .id(order.getId())
                        .status(order.getStatus())
                        .assetsAsBuilt(emptyIfNull(order.getPartsAsBuilt()).stream().map(AssetAsBuiltEntity::from).collect(Collectors.toSet()))
                        .assetsAsPlanned(emptyIfNull(order.getPartsAsPlanned()).stream().map(AssetAsPlannedEntity::from).collect(Collectors.toSet()))
                        .orderConfigurationId(
                                order.getOrderConfiguration() != null ? order.getOrderConfiguration().getId() : null)
                        .message(order.getMessage())
                        .build());
    }

    @Override
    public List<Order> findOrdersByStatus(List<ProcessingState> statusList) {
        return orderJPARepository.findOrdersByStatus(statusList).stream()
                .map(OrderEntity::toDomain)
                .collect(Collectors.toList());
    }

}
