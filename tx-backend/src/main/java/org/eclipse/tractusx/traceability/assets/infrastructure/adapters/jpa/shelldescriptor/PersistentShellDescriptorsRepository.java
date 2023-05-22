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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.shelldescriptor;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.assets.domain.service.repository.ShellDescriptorRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PersistentShellDescriptorsRepository implements ShellDescriptorRepository {

	private final JpaShellDescriptorRepository repository;

	@Override
	public List<ShellDescriptor> findAll() {
		return repository.findAll().stream()
			.map(ShellDescriptorEntity::toShellDescriptor)
			.toList();
	}

	@Override
	@Transactional
	public void update(ShellDescriptor shellDescriptor) {
		repository.findByShellDescriptorId(shellDescriptor.shellDescriptorId()).ifPresent(entity -> {
			entity.setUpdated(ZonedDateTime.now());
			repository.save(entity);
		});
	}

	@Override
	public void saveAll(Collection<ShellDescriptor> values) {
		repository.saveAll(values.stream()
			.map(ShellDescriptorEntity::newEntityFrom)
			.toList());
	}

	@Override
	public void removeDescriptorsByUpdatedBefore(ZonedDateTime now) {
		repository.deleteAllByUpdatedBefore(now);
	}
}
