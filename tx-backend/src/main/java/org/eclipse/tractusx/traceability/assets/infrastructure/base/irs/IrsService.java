/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrsService implements IrsRepository {

    private final IRSApiClient irsClient;
    private final BpnRepository bpnRepository;
    private final TraceabilityProperties traceabilityProperties;
    private final ObjectMapper objectMapper;
    @Qualifier("assetAsBuiltRepositoryImpl")
    private final AssetCallbackRepository assetAsBuiltCallbackRepository;
    @Qualifier("assetAsPlannedRepositoryImpl")
    private final AssetCallbackRepository assetAsPlannedCallbackRepository;

    @Override
    public void createJobToResolveAssets(String globalAssetId, Direction direction, List<String> aspects, BomLifecycle bomLifecycle) {
        RegisterJobRequest registerJobRequest = RegisterJobRequest.buildJobRequest(globalAssetId, traceabilityProperties.getBpn().toString(), direction, aspects, bomLifecycle, traceabilityProperties.getUrl());
        log.info("Build HTTP Request {}", registerJobRequest);
        try {
            log.info("Build HTTP Request as JSON {}", objectMapper.writeValueAsString(registerJobRequest));
        } catch (Exception e) {
            log.error("exception", e);
        }

        irsClient.registerJob(registerJobRequest);
    }

    @Override
    public void handleJobFinishedCallback(String jobId, String state) {
        if(!Objects.equals(state, JobDetailResponse.JOB_STATUS_COMPLETED)) {
            return;
        }
        JobDetailResponse jobResponse = irsClient.getJobDetails(jobId);

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
        List<PolicyResponse> irsPolicies = irsClient.getPolicies();
        log.info("Irs has following policies: {}", irsPolicies);

        log.info("Required constraints from application yaml are : {}", traceabilityProperties.getRightOperand());


        //update existing policies
        irsPolicies.stream().filter(
                        irsPolicy -> traceabilityProperties.getRightOperand().equals(irsPolicy.policyId()))
                .forEach(existingPolicy -> checkAndUpdatePolicy(irsPolicies));


        //create missing policies
        boolean missingPolicy = irsPolicies.stream().noneMatch(irsPolicy -> irsPolicy.policyId().equals(traceabilityProperties.getRightOperand()));
        if (missingPolicy) {
            createPolicy();
        }
    }

    private void createPolicy() {
        log.info("Irs policy does not exist creating {}", traceabilityProperties.getRightOperand());
        irsClient.registerPolicy(RegisterPolicyRequest.from(traceabilityProperties.getLeftOperand(), OperatorType.fromValue(traceabilityProperties.getOperatorType()), traceabilityProperties.getRightOperand(), traceabilityProperties.getValidUntil()));
    }

    private void checkAndUpdatePolicy(List<PolicyResponse> requiredPolicies) {
        Optional<PolicyResponse> requiredPolicy = requiredPolicies.stream().filter(policyItem -> policyItem.policyId().equals(traceabilityProperties.getRightOperand())).findFirst();
        if (requiredPolicy.isPresent() &&
                traceabilityProperties.getValidUntil().isAfter(requiredPolicy.get().validUntil())
        ) {
            log.info("IRS Policy {} has outdated validity updating new ttl {}", traceabilityProperties.getRightOperand(), requiredPolicy);
            irsClient.deletePolicy(traceabilityProperties.getRightOperand());
            irsClient.registerPolicy(RegisterPolicyRequest.from(traceabilityProperties.getLeftOperand(), OperatorType.fromValue(traceabilityProperties.getOperatorType()), traceabilityProperties.getRightOperand(), traceabilityProperties.getValidUntil()));
        }
    }

    public List<PolicyResponse> getPolicies() {
        return irsClient.getPolicies();
    }

}
