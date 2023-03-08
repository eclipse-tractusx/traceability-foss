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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository;
import org.eclipse.tractusx.traceability.assets.domain.ports.IrsRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.JobResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.JobStatus;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.StartJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.StartJobResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class IrsService implements IrsRepository {

	private static final Logger logger = LoggerFactory.getLogger(IrsService.class);

	private final IRSApiClient irsClient;
	private final AssetsConverter assetsConverter;
	private final BpnRepository bpnRepository;

	public IrsService(IRSApiClient irsClient, AssetsConverter assetsConverter, BpnRepository bpnRepository) {
		this.irsClient = irsClient;
		this.assetsConverter = assetsConverter;
		this.bpnRepository = bpnRepository;
	}

	@Override
	public List<Asset> findAssets(String globalAssetId) {
		StartJobResponse job = irsClient.registerJob(StartJobRequest.forGlobalAssetId(globalAssetId));
		JobResponse jobDetails = irsClient.getJobDetails(job.id());

		JobStatus jobStatus = jobDetails.jobStatus();
		long runtime = (jobStatus.lastModifiedOn().getTime() - jobStatus.startedOn().getTime()) / 1000;
		logger.info("IRS call for globalAssetId: {} finished with status: {}, runtime {} s.", globalAssetId, jobStatus.state(), runtime);

		if (jobDetails.isCompleted()) {
			bpnRepository.updateManufacturers(jobDetails.bpns());
			return assetsConverter.convertAssets(jobDetails);
		}

		return Collections.emptyList();
	}
}
