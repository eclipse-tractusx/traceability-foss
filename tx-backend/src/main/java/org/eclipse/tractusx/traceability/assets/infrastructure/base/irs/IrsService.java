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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.traceability.assets.domain.base.IrsRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterPolicyRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.PolicyResponse;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class IrsService implements IrsRepository {

    private final IRSApiClient irsApiClient;
    private final BpnRepository bpnRepository;
    private final TraceabilityProperties traceabilityProperties;
    private final ObjectMapper objectMapper;
    private final AssetCallbackRepository assetAsBuiltCallbackRepository;
    private final AssetCallbackRepository assetAsPlannedCallbackRepository;

    private String adminApiKey;
    private String regularApiKey;

    public IrsService(
            IRSApiClient irsApiClient,
            BpnRepository bpnRepository,
            TraceabilityProperties traceabilityProperties,
            ObjectMapper objectMapper,
            @Qualifier("assetAsBuiltRepositoryImpl")
            AssetCallbackRepository assetAsBuiltCallbackRepository,
            @Qualifier("assetAsPlannedRepositoryImpl")
            AssetCallbackRepository assetAsPlannedCallbackRepository,
            @Value("${feign.irsApi.adminApiKey}") final String adminApiKey,
            @Value("${feign.irsApi.regularApiKey}") final String regularApikey) {
        this.irsApiClient = irsApiClient;
        this.bpnRepository = bpnRepository;
        this.traceabilityProperties = traceabilityProperties;
        this.objectMapper = objectMapper;
        this.assetAsBuiltCallbackRepository = assetAsBuiltCallbackRepository;
        this.assetAsPlannedCallbackRepository = assetAsPlannedCallbackRepository;
        this.adminApiKey = adminApiKey;
        this.regularApiKey = regularApikey;
    }

    @Override
    public void createJobToResolveAssets(String globalAssetId, Direction direction, List<String> aspects, BomLifecycle bomLifecycle) {
        RegisterJobRequest registerJobRequest = RegisterJobRequest.buildJobRequest(globalAssetId, traceabilityProperties.getBpn().toString(), direction, aspects, bomLifecycle, traceabilityProperties.getUrl());
        log.info("Build HTTP Request {}", registerJobRequest);
        try {
            log.info("Build HTTP Request as JSON {}", objectMapper.writeValueAsString(registerJobRequest));
        } catch (Exception e) {
            log.error("exception", e);
        }

        irsApiClient.registerJob(regularApiKey, registerJobRequest);
    }

    @Override
    public void handleJobFinishedCallback(String jobId, String state) {
        if (!Objects.equals(state, JobDetailResponse.JOB_STATUS_COMPLETED)) {
            return;
        }
        JobDetailResponse jobResponse = irsApiClient.getJobDetails(regularApiKey, jobId);

        long runtime = (jobResponse.jobStatus().lastModifiedOn().getTime() - jobResponse.jobStatus().startedOn().getTime()) / 1000;
        log.info("IRS call for globalAssetId: {} finished with status: {}, runtime {} s.", jobResponse.jobStatus().globalAssetId(), jobResponse.jobStatus().state(), runtime);
        try {
            log.info("Received HTTP Response: {}", objectMapper.writeValueAsString(jobResponse));
        } catch (Exception e) {
            log.warn("Unable to log IRS Response", e);
        }
        if (jobResponse.isCompleted()) {
            try {
                // TODO exception will be often thrown probably because two transactions try to commit same primary key - check if we need to update it here
                bpnRepository.updateManufacturers(jobResponse.bpns());
            } catch (Exception e) {
                log.warn("BPN Mapping Exception", e);
            }

            // persist converted assets
            jobResponse.convertAssets().forEach(assetBase -> {
                if (assetBase.getBomLifecycle() == org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_BUILT) {
                    saveOrUpdateAssets(assetAsBuiltCallbackRepository, assetBase);
                } else if (assetBase.getBomLifecycle() == org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_PLANNED) {
                    saveOrUpdateAssets(assetAsPlannedCallbackRepository, assetBase);
                }
            });
        }
    }

    void saveOrUpdateAssets(AssetCallbackRepository repository, AssetBase asset) {
        Optional<AssetBase> existingAssetOptional = repository.findById(asset.getId());
        if (existingAssetOptional.isPresent()) {
            AssetBase existingAsset = existingAssetOptional.get();
            if (existingAsset.getOwner().equals(Owner.UNKNOWN)) {
                existingAsset.setOwner(asset.getOwner());
            }
            if (!asset.getParentRelations().isEmpty()) {
                existingAsset.setParentRelations(asset.getParentRelations());
            }
            repository.save(existingAsset);
        } else {
            repository.save(asset);
        }
    }

    @Override
    public void createIrsPolicyIfMissing() {
        log.info("Check if irs policy exists");
        List<PolicyResponse> irsPolicies = Objects.requireNonNullElse(irsApiClient.getPolicies(adminApiKey), Collections.emptyList());
        log.info("Irs has following policies: {}", irsPolicies);
        log.info("Required constraints from application yaml are : {}", traceabilityProperties.getRightOperand());


        PolicyResponse matchingIrsPolicy = irsPolicies.stream()
                .filter(irsPolicy -> irsPolicy.permissions().stream()
                        .flatMap(permission -> permission.getConstraints().stream())
                        .anyMatch(constraint ->
                                constraint.getOr().stream().anyMatch(rightO ->
                                        rightO.getRightOperand().stream().anyMatch(value ->
                                                value.equals(traceabilityProperties.getRightOperand())))
                                        ||
                                        constraint.getAnd().stream().allMatch(rightO ->
                                                rightO.getRightOperand().stream().allMatch(value ->
                                                        value.equals(traceabilityProperties.getRightOperand())))
                        ))
                .findFirst()
                .orElse(null);

        if (matchingIrsPolicy == null) {
            createMissingPolicies();
        } else {
            checkAndUpdateExpiredPolicies((matchingIrsPolicy));
        }
    }

    private void createMissingPolicies() {
        log.info("Irs policy does not exist creating {}", traceabilityProperties.getRightOperand());
        irsApiClient.registerPolicy(adminApiKey, RegisterPolicyRequest.from(traceabilityProperties.getLeftOperand(), OperatorType.fromValue(traceabilityProperties.getOperatorType()), traceabilityProperties.getRightOperand(), traceabilityProperties.getValidUntil()));
    }

    private void checkAndUpdateExpiredPolicies(PolicyResponse matchingIrsPolicy) {
        if (isPolicyExpired(matchingIrsPolicy)) {
            log.info("IRS Policy {} has outdated validity updating new ttl {}", traceabilityProperties.getRightOperand(), matchingIrsPolicy);
            irsApiClient.deletePolicy(adminApiKey, traceabilityProperties.getRightOperand());
            irsApiClient.registerPolicy(adminApiKey, RegisterPolicyRequest.from(traceabilityProperties.getLeftOperand(), OperatorType.fromValue(traceabilityProperties.getOperatorType()), traceabilityProperties.getRightOperand(), traceabilityProperties.getValidUntil()));
        }
    }

    private boolean isPolicyExpired(PolicyResponse requiredPolicy) {
        return traceabilityProperties.getValidUntil().isAfter(requiredPolicy.validUntil());
    }

    public List<PolicyResponse> getPolicies() {
        return irsApiClient.getPolicies(adminApiKey);
    }


}
