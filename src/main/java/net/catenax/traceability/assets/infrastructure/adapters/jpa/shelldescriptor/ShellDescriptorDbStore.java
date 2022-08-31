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
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;

@Component
public class ShellDescriptorDbStore {

	private final ShellDescriptorRepository shellDescriptorRepository;

	public ShellDescriptorDbStore(ShellDescriptorRepository shellDescriptorRepository) {
		this.shellDescriptorRepository = shellDescriptorRepository;
	}

	public void store(List<ShellDescriptor> descriptors) {
		List<ShellDescriptorEntity> descriptorEntities = map(descriptors);
		shellDescriptorRepository.saveAll(descriptorEntities);
	}

	public Long count(String shellDescriptorId) {
		return shellDescriptorRepository.countAllByShellDescriptorIdEquals(shellDescriptorId);
	}

	public void deleteAll() {
		shellDescriptorRepository.deleteAll();
	}

	private List<ShellDescriptorEntity> map(List<ShellDescriptor> descriptors) {
		ZonedDateTime now = ZonedDateTime.now();
		return descriptors.stream().map(d -> new ShellDescriptorEntity(now, now, d.shellDescriptorId(), d.globalAssetId(), d.rawDescriptor())).toList();
	}
}
