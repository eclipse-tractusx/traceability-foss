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
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.traceability.assets.domain.base.IrsRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.PolicyResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.AssetMapperFactory;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_BUILT;
import static org.eclipse.tractusx.irs.component.enums.BomLifecycle.AS_PLANNED;
import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.AssetMapperFactory.extractBpnMap;

@Slf4j
@Service
public class IrsService implements IrsRepository {

    private final BpnRepository bpnRepository;
    private final TraceabilityProperties traceabilityProperties;
    private final ObjectMapper objectMapper;
    private final AssetCallbackRepository assetAsBuiltCallbackRepository;
    private final AssetCallbackRepository assetAsPlannedCallbackRepository;

    private static final String JOB_STATUS_COMPLETED = "COMPLETED";

    private static final String JOB_STATUS_RUNNING = "RUNNING";
    private final AssetMapperFactory assetMapperFactory;

    private final IrsClient irsClient;

    public IrsService(
            IrsClient irsClient,
            BpnRepository bpnRepository,
            TraceabilityProperties traceabilityProperties,
            ObjectMapper objectMapper,
            @Qualifier("assetAsBuiltRepositoryImpl")
            AssetCallbackRepository assetAsBuiltCallbackRepository,
            @Qualifier("assetAsPlannedRepositoryImpl")
            AssetCallbackRepository assetAsPlannedCallbackRepository, AssetMapperFactory assetMapperFactory) {
        this.bpnRepository = bpnRepository;
        this.traceabilityProperties = traceabilityProperties;
        this.objectMapper = objectMapper;
        this.assetAsBuiltCallbackRepository = assetAsBuiltCallbackRepository;
        this.assetAsPlannedCallbackRepository = assetAsPlannedCallbackRepository;
        this.irsClient = irsClient;
        this.assetMapperFactory = assetMapperFactory;
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
        this.irsClient.registerJob(registerJobRequest);
    }


    @Override
    public void handleJobFinishedCallback(String jobId, String state) {
        if (!Objects.equals(state, JOB_STATUS_COMPLETED)) {
            return;
        }

        final IRSResponse jobResponseIRS = this.irsClient.getIrsJobDetailResponse(jobId);

        if (jobResponseIRS == null) {
            return;
        }

        long runtime = (jobResponseIRS.jobStatus().lastModifiedOn().getTime() - jobResponseIRS.jobStatus().startedOn().getTime()) / 1000;
        log.info("IRS call for globalAssetId: {} finished with status: {}, runtime {} s.", jobResponseIRS.jobStatus().globalAssetId(), jobResponseIRS.jobStatus().state(), runtime);

        if (jobCompleted(jobResponseIRS.jobStatus())) {
            try {
                Map<String, String> bpnMap = extractBpnMap(jobResponseIRS);
                bpnRepository.updateManufacturers(bpnMap);
            } catch (Exception e) {
                log.warn("BPN Mapping Exception", e);
            }

            List<AssetBase> assets = assetMapperFactory.mapToAssetBaseList(jobResponseIRS);

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
        Optional<AssetBase> existingAssetOptional = repository.findById(asset.getId());
        if (existingAssetOptional.isPresent()) {
            AssetBase existingAsset = existingAssetOptional.get();
            if (existingAsset.getOwner().equals(Owner.UNKNOWN)) {
                existingAsset.setOwner(asset.getOwner());
            }
            if (!asset.getParentRelations().isEmpty()) {
                existingAsset.setParentRelations(asset.getParentRelations());
            }
            existingAsset.setTombstone(asset.getTombstone() == null ? "" : asset.getTombstone());
            repository.save(existingAsset);
        } else {
            repository.save(asset);
        }
    }

    @Override
    public void createIrsPolicyIfMissing() {
        log.info("Check if irs policy exists");
        final List<PolicyResponse> irsPolicies = this.irsClient.getPolicies();
        final List<String> irsPoliciesIds = irsPolicies.stream().map(policyResponse -> policyResponse.payload().policyId()).toList();
        log.info("Irs has following policies: {}", irsPoliciesIds);

        log.info("Required constraints from application yaml are : {}", traceabilityProperties.getRightOperand());

        PolicyResponse matchingPolicy = findMatchingPolicy(irsPolicies);

        if (matchingPolicy == null) {
            createMissingPolicies();
        } else {
            checkAndUpdatePolicy(matchingPolicy);
        }
    }

    private PolicyResponse findMatchingPolicy(List<PolicyResponse> irsPolicies) {
        return irsPolicies.stream()
                .filter(irsPolicy -> emptyIfNull(irsPolicy.payload().policy().getPermissions()).stream()
                        .flatMap(permission -> {
                            Constraints constraint = permission.getConstraint();
                            return constraint != null ? constraint.getAnd().stream() : Stream.empty();
                        })
                        .anyMatch(constraint -> constraint.getRightOperand().equals(traceabilityProperties.getRightOperand()))
                        || emptyIfNull(irsPolicy.payload().policy().getPermissions()).stream()
                        .flatMap(permission -> {
                            Constraints constraint = permission.getConstraint();
                            return constraint != null ? constraint.getOr().stream() : Stream.empty();
                        })
                        .anyMatch(constraint -> constraint.getRightOperand().equals(traceabilityProperties.getRightOperand())))
                .findFirst()
                .orElse(null);
    }


    private void createMissingPolicies() {
        log.info("Irs policy does not exist creating {}", traceabilityProperties.getRightOperand());
        this.irsClient.registerPolicy();
    }

    private void checkAndUpdatePolicy(PolicyResponse requiredPolicy) {
        if (isPolicyExpired(requiredPolicy)) {
            log.info("IRS Policy {} has outdated validity updating new ttl", traceabilityProperties.getRightOperand());
            this.irsClient.deletePolicy();
            this.irsClient.registerPolicy();
        }
    }

    private boolean isPolicyExpired(PolicyResponse requiredPolicy) {
        return traceabilityProperties.getValidUntil().isAfter(requiredPolicy.validUntil());
    }

    public List<PolicyResponse> getPolicies() {
        return irsClient.getPolicies();
    }

    public static boolean jobCompleted(JobStatus jobStatus) {
        return JOB_STATUS_COMPLETED.equals(jobStatus.state());
    }

    public static boolean jobRunning(JobStatus jobStatus) {
        return JOB_STATUS_RUNNING.equals(jobStatus.state());
    }

}
