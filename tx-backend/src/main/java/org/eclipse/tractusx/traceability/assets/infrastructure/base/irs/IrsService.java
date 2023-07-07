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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.BpnRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.IrsRepository;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.config.IrsPolicyConfig;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterPolicyRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.PolicyResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.RegisterJobResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.IrsPolicy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrsService implements IrsRepository {

    private final IRSApiClient irsClient;
    private final BpnRepository bpnRepository;
    private final IrsPolicyConfig irsPolicyConfig;

    @Override
    public List<Asset> findAssets(String globalAssetId, Direction direction, List<String> aspects, BomLifecycle bomLifecycle) {
        RegisterJobResponse startJobResponse = irsClient.registerJob(RegisterJobRequest.buildJobRequest(globalAssetId, direction, aspects, bomLifecycle));
        JobDetailResponse jobResponse = irsClient.getJobDetails(startJobResponse.id());

        JobStatus jobStatus = jobResponse.jobStatus();
        long runtime = (jobStatus.lastModifiedOn().getTime() - jobStatus.startedOn().getTime()) / 1000;
        log.info("IRS call for globalAssetId: {} finished with status: {}, runtime {} s.", globalAssetId, jobStatus.state(), runtime);

        if (jobResponse.isCompleted()) {
            try {
                bpnRepository.updateManufacturers(jobResponse.bpns());
            } catch (Exception e) {
                log.warn("BPN Mapping Exception", e);
            }
            return jobResponse.convertAssets();
        }
        return Collections.emptyList();
    }

    @Override
    public void createIrsPolicyIfMissing() {
        log.info("Check if irs policy exists");
        List<PolicyResponse> irsPolicies = irsClient.getPolicies();
        log.info("Irs has following policies: {}", irsPolicies);


        final IrsPolicy requiredPolicy = irsPolicyConfig.getPolicy();

        final Optional<IrsPolicy> existingPolicy = irsPolicies.stream().filter(policy -> policy.policyId().equals(requiredPolicy.getPolicyId()))
                .map(PolicyResponse::toDomain)
                .findFirst();

        if (existingPolicy.isPresent()
                && existingPolicy.get()
                .getTtlAsInstant()
                .isBefore(requiredPolicy.getTtlAsInstant())
        ) {
            log.info("IRS Policy has outdated validity updating new ttl");
            irsClient.deletePolicy(existingPolicy.get().getPolicyId());
            irsClient.registerPolicy(RegisterPolicyRequest.from(requiredPolicy));
        }

        if (existingPolicy.isEmpty()) {
            log.info("Irs policy does not exist creating {}", requiredPolicy);
            irsClient.registerPolicy(RegisterPolicyRequest.from(requiredPolicy));
        }
    }

}
