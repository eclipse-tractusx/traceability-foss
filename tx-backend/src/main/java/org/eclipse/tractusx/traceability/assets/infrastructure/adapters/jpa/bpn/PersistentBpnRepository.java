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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.bpn;

import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PersistentBpnRepository implements BpnRepository {

	private JpaBpnRepository repository;

	public PersistentBpnRepository(JpaBpnRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<String> findManufacturerName(String manufacturerId) {
		return repository.findById(manufacturerId)
			.map(BpnEntity::getManufacturerName);
	}

	@Override
	@Transactional
	public void updateManufacturers(Map<String, String> bpns) {
		List<BpnEntity> entities = bpns.entrySet().stream()
			.map(entry -> new BpnEntity(entry.getKey(), entry.getValue()))
			.toList();

		repository.saveAll(entities);
	}

}
