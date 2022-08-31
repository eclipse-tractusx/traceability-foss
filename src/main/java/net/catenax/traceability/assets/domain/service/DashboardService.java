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

package net.catenax.traceability.assets.domain.service;

import net.catenax.traceability.assets.domain.model.Dashboard;
import net.catenax.traceability.assets.domain.ports.AssetRepository;
import net.catenax.traceability.common.security.KeycloakAuthentication;
import net.catenax.traceability.common.security.KeycloakRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class DashboardService {

	private final AssetRepository assetRepository;

	public DashboardService(AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}

	public Dashboard getDashboard(KeycloakAuthentication keycloakAuthentication) {
		long assetsCount = assetRepository.countAssets();

		if (keycloakAuthentication.hasRole(KeycloakRole.USER)) {
			return new Dashboard(assetsCount, null);
		} else if (keycloakAuthentication.hasAtLeastOneRole(KeycloakRole.ADMIN, KeycloakRole.SUPERVISOR)) {
			return new Dashboard(assetsCount, assetsCount);
		} else {
			throw new AccessDeniedException("User has invalid role to access the dashboard.");
		}
	}
}
