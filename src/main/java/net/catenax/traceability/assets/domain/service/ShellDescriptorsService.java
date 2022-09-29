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

package net.catenax.traceability.assets.domain.service;

import net.catenax.traceability.assets.domain.model.ShellDescriptor;
import net.catenax.traceability.assets.domain.ports.ShellDescriptorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ShellDescriptorsService {
	private static final Logger logger = LoggerFactory.getLogger(ShellDescriptorsService.class);

	private final ShellDescriptorRepository shellDescriptorRepository;

	public ShellDescriptorsService(ShellDescriptorRepository shellDescriptorRepository) {
		this.shellDescriptorRepository = shellDescriptorRepository;
	}

	@Transactional
	public List<ShellDescriptor> update(List<ShellDescriptor> descriptors) {
		logger.info("Starting update of {} shell descriptors.", descriptors.size());
		Map<String, ShellDescriptor> existingDescriptors = shellDescriptorRepository.findAll().stream()
			.collect(Collectors.toMap(ShellDescriptor::globalAssetId, Function.identity()));
		List<ShellDescriptor> descriptorsToSync = new ArrayList<>();
		ZonedDateTime now = ZonedDateTime.now();

		for (ShellDescriptor descriptor : descriptors) {
			if (existingDescriptors.containsKey(descriptor.globalAssetId())) {
				shellDescriptorRepository.update(existingDescriptors.get(descriptor.globalAssetId()));
			} else {
				descriptorsToSync.add((descriptor));
			}
		}

		shellDescriptorRepository.saveAll(descriptorsToSync);
		shellDescriptorRepository.removeOldDescriptors(now);

		logger.info("Finished update of {} shell descriptors.", descriptors.size());
		logger.info("Updated needed for {} descriptors.", descriptorsToSync.size());

		return descriptorsToSync;
	}
}
