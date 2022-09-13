/*
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
 */
package net.catenax.traceability.assets.infrastructure.adapters.jpa.shelldescriptor;

import net.catenax.traceability.assets.domain.model.ShellDescriptor;
import net.catenax.traceability.assets.domain.ports.ShellDescriptorRepository;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Component
public class PersistentShellDescriptorsRepository implements ShellDescriptorRepository {

	private final JpaShellDescriptorRepository repository;

	public PersistentShellDescriptorsRepository(JpaShellDescriptorRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<ShellDescriptor> findAll() {
		return repository.findAll().stream()
			.map(this::toShellDescriptor)
			.toList();
	}

	@Override
	public void update(ShellDescriptor shellDescriptor) {
		repository.findByShellDescriptorId(shellDescriptor.shellDescriptorId()).ifPresent(entity -> {
			entity.setUpdated(ZonedDateTime.now());
			repository.save(entity);
		});
	}

	@Override
	public void saveAll(Collection<ShellDescriptor> values) {
		repository.saveAll(values.stream()
			.map(this::toNewEntity)
			.toList());
	}

	@Override
	public void removeOldDescriptors(ZonedDateTime now) {
		repository.deleteAllByUpdatedBefore(now);
	}

	@Override
	public void clean() {
		repository.deleteAll();
	}

	private ShellDescriptor toShellDescriptor(ShellDescriptorEntity descriptor) {
		return new ShellDescriptor(descriptor.getShellDescriptorId(), descriptor.getGlobalAssetId());
	}

	private ShellDescriptorEntity toNewEntity(ShellDescriptor descriptor) {
		return ShellDescriptorEntity.newEntity(descriptor.shellDescriptorId(), descriptor.globalAssetId());
	}

}
