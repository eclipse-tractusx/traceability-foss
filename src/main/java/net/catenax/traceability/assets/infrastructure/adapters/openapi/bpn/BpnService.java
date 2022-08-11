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

package net.catenax.traceability.assets.infrastructure.adapters.openapi.bpn;

import feign.FeignException;
import net.catenax.traceability.assets.domain.BpnRepository;
import net.catenax.traceability.assets.infrastructure.adapters.cache.bpn.BpnCache;
import net.catenax.traceability.assets.infrastructure.adapters.cache.bpn.BpnMapping;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BpnService implements BpnRepository {

	private static final Logger logger = LoggerFactory.getLogger(BpnService.class);

	private static final String BPN_TYPE = "BPN";

	private final BpnCache bpnCache;
	private final BpnApiClient bpnApiClient;

	public BpnService(BpnCache bpnCache, BpnApiClient bpnApiClient) {
		this.bpnCache = bpnCache;
		this.bpnApiClient = bpnApiClient;
	}

	@Override
	public Optional<String> findManufacturerName(String manufacturerId) {
		Optional<String> companyNameResult = bpnCache.getCompanyName(manufacturerId);

		if (companyNameResult.isPresent()) {
			return companyNameResult;
		}

		final BusinessPartnerResponse businessPartner;

		try {
			businessPartner = bpnApiClient.getBusinessPartner(manufacturerId, BPN_TYPE);
		} catch (FeignException e) {
			logger.error("Exception during calling bpn business partner api", e);

			return Optional.empty();
		}

		List<NameResponse> names = businessPartner.getNames();

		if (names.isEmpty()) {
			logger.warn("Names not found for {} BPN", manufacturerId);

			return Optional.empty();
		}

		Optional<String> firstNotEmptyManufacturerName = names.stream()
			.filter(it -> StringUtils.isNotBlank(it.getValue()))
			.findFirst()
			.map(NameResponse::getValue);

		firstNotEmptyManufacturerName.ifPresentOrElse(
			manufacturerName -> bpnCache.put(new BpnMapping(manufacturerId, manufacturerName)),
			() -> logger.warn("Manufacturer name not found for {} id", manufacturerId)
		);

		return firstNotEmptyManufacturerName;
	}
}
