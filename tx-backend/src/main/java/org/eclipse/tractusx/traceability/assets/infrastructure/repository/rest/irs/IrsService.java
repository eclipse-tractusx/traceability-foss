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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.IrsRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.AssetsConverter;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.JobResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.StartJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.StartJobResponse;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrsService implements IrsRepository {

    private final IRSApiClient irsClient;
    private final AssetsConverter assetsConverter;
    private final BpnRepository bpnRepository;


    @Override
    public List<Asset> findAssets(String globalAssetId, Direction direction, List<String> aspects) {
        StartJobResponse startJobResponse = irsClient.registerJob(StartJobRequest.buildJobRequest(globalAssetId, direction, aspects));
        JobResponse jobResponse = irsClient.getJobDetails(startJobResponse.id());

        JobStatus jobStatus = jobResponse.jobStatus();
        long runtime = (jobStatus.lastModifiedOn().getTime() - jobStatus.startedOn().getTime()) / 1000;
        log.info("IRS call for globalAssetId: {} finished with status: {}, runtime {} s.", globalAssetId, jobStatus.state(), runtime);

        if (jobResponse.isCompleted()) {
            bpnRepository.updateManufacturers(jobResponse.bpns());
            return assetsConverter.convertAssetsAndLog(jobResponse, globalAssetId);
        }

        return Collections.emptyList();
    }
}
