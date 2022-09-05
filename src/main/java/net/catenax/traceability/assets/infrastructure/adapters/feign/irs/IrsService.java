/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.assets.infrastructure.adapters.feign.irs;

import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.ports.IrsRepository;
import net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter;
import net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model.JobResponse;
import net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model.StartJobRequest;
import net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model.StartJobResponse;
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

	public IrsService(IRSApiClient irsClient, AssetsConverter assetsConverter) {
		this.irsClient = irsClient;
		this.assetsConverter = assetsConverter;
	}

	@Override
	public List<Asset> findAssets(String globalAssetId) {
		StartJobResponse job = irsClient.registerJob(StartJobRequest.forGlobalAssetId(globalAssetId));
		JobResponse jobDetails = irsClient.getJobDetails(job.jobId());

		logger.info("IRS call for globalAssetId: {} finished with status: {} ", globalAssetId, jobDetails.jobStatus());

		if (jobDetails.isCompleted()) {
			return assetsConverter.convertAssets(jobDetails);
		}

		return Collections.emptyList();
	}
}
