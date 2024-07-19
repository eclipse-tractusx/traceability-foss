/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023, 2024 Contributors to the Eclipse Foundation
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.JobRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.IrsResponseAssetMapper;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_BUILT;
import static org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_PLANNED;

@Slf4j
@Service
public class JobRepositoryImpl implements JobRepository {

    private final TraceabilityProperties traceabilityProperties;
    private final AssetCallbackRepository assetAsBuiltCallbackRepository;
    private final AssetCallbackRepository assetAsPlannedCallbackRepository;
    private static final String JOB_STATUS_COMPLETED = "COMPLETED";

    private final IrsResponseAssetMapper assetMapperFactory;

    private final JobClient jobClient;
    private final ObjectMapper objectMapper;

    public JobRepositoryImpl(
            JobClient jobClient,
            TraceabilityProperties traceabilityProperties,
            @Qualifier("assetAsBuiltRepositoryImpl")
            AssetCallbackRepository assetAsBuiltCallbackRepository,
            @Qualifier("assetAsPlannedRepositoryImpl")
            AssetCallbackRepository assetAsPlannedCallbackRepository,
            IrsResponseAssetMapper assetMapperFactory, ObjectMapper objectMapper) {
        this.traceabilityProperties = traceabilityProperties;
        this.assetAsBuiltCallbackRepository = assetAsBuiltCallbackRepository;
        this.assetAsPlannedCallbackRepository = assetAsPlannedCallbackRepository;
        this.jobClient = jobClient;
        this.assetMapperFactory = assetMapperFactory;
        this.objectMapper = objectMapper;
    }

    @Override
    public void createJobToResolveAssets(String globalAssetId, Direction direction, List<String> aspects, BomLifecycle bomLifecycle) {
        RegisterJobRequest registerJobRequest = RegisterJobRequest.buildJobRequest(globalAssetId, traceabilityProperties.getBpn().toString(), direction, aspects, bomLifecycle, traceabilityProperties.getUrl());
        this.jobClient.registerJob(registerJobRequest);
    }


    @Override
    public void handleJobFinishedCallback(String jobId, String state) {
        if (!Objects.equals(state, JOB_STATUS_COMPLETED)) {
            return;
        }

        final IRSResponse jobResponseIRS = this.jobClient.getIrsJobDetailResponse(jobId);

        if (jobResponseIRS == null) {
            return;
        }

        long runtime = (jobResponseIRS.jobStatus().lastModifiedOn().getTime() - jobResponseIRS.jobStatus().startedOn().getTime()) / 1000;
        log.info("IRS call for globalAssetId: {} finished with status: {}, runtime {} s.", jobResponseIRS.jobStatus().globalAssetId(), jobResponseIRS.jobStatus().state(), runtime);

        if (jobCompleted(jobResponseIRS.jobStatus())) {
            List<AssetBase> assets = assetMapperFactory.toAssetBaseList(jobResponseIRS);
            assets.forEach(assetBase -> {
                if (assetBase.getBomLifecycle() == AS_BUILT) {
                    saveOrUpdateAssets(assetAsBuiltCallbackRepository, assetBase);
                } else if (assetBase.getBomLifecycle() == AS_PLANNED) {
                    saveOrUpdateAssets(assetAsPlannedCallbackRepository, assetBase);
                }
            });
        }
    }

    void saveOrUpdateAssets(AssetCallbackRepository repository, AssetBase asset) {
        try {
            enrichAssetBaseByContractAgreements(repository, asset);
            repository.save(asset);
        } catch (DataIntegrityViolationException ex) {
            //retry save in case of ERROR: duplicate key value violates unique constraint "asset_pkey"
            log.info("Asset with id {} already exists in the database. The record will be updated instead.", asset.getId());
            repository.save(asset);
        }
    }

    private void enrichAssetBaseByContractAgreements(AssetCallbackRepository repository, AssetBase asset) {
        Optional<AssetBase> byId = repository.findById(asset.getId());
        List<ContractAgreement> agreementsToAdd = new ArrayList<>();
        byId.ifPresent(assetBase -> agreementsToAdd.addAll(assetBase.getContractAgreements()));
        try {
            log.info("Found the following existing contractAgreements {}", this.objectMapper.writeValueAsString(agreementsToAdd));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ContractType contractType = asset.getSemanticDataModel().isAsBuilt() ? ContractType.ASSET_AS_BUILT : ContractType.ASSET_AS_PLANNED;
        agreementsToAdd.add(ContractAgreement.toDomain(asset.getLatestContractAgreementId(), asset.getId(), contractType));
        asset.setContractAgreements(agreementsToAdd);
        try {
            log.info("Found the following NEW contractAgreements {}", this.objectMapper.writeValueAsString(agreementsToAdd));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean jobCompleted(JobStatus jobStatus) {
        return JOB_STATUS_COMPLETED.equals(jobStatus.state());
    }

}
