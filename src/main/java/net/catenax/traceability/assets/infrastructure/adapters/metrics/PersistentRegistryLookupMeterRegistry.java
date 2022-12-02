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

package net.catenax.traceability.assets.infrastructure.adapters.metrics;

import net.catenax.traceability.assets.infrastructure.adapters.jpa.metrics.registrylookup.JpaRegistryLookupMetricRepository;
import net.catenax.traceability.assets.infrastructure.adapters.jpa.metrics.registrylookup.RegistryLookupMetricEntity;
import net.catenax.traceability.common.model.PageResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistentRegistryLookupMeterRegistry implements RegistryLookupMeterRegistry {

	private final JpaRegistryLookupMetricRepository jpaRegistryLookupMetricRepository;

	public PersistentRegistryLookupMeterRegistry(JpaRegistryLookupMetricRepository jpaRegistryLookupMetricRepository) {
		this.jpaRegistryLookupMetricRepository = jpaRegistryLookupMetricRepository;
	}

	@Override
	public void save(RegistryLookupMetric registryLookupMetric) {
		RegistryLookupMetricEntity registryLookupMetricEntity = registryLookupMetric.toEntity();

		jpaRegistryLookupMetricRepository.save(registryLookupMetricEntity);
	}

	@Override
	public PageResult<RegistryLookupMetric> getMetrics(Pageable pageable) {
		Pageable pageableWithSorting = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "startDate"));

		List<RegistryLookupMetric> registryLookupMetrics = jpaRegistryLookupMetricRepository.findAll(pageableWithSorting).stream()
			.map(this::toDTO)
			.toList();

		for (int i = 0; i < registryLookupMetrics.size(); i++) {
			RegistryLookupMetric registryLookupMetric = registryLookupMetrics.get(i);

			if (isLastElement(i, registryLookupMetrics)) {
				registryLookupMetric.firstElementDelta();
			} else {
				RegistryLookupMetric nextMetric = registryLookupMetrics.get(i + 1);

				registryLookupMetric.addDelta(nextMetric);
			}
		}

		return new PageResult<>(registryLookupMetrics);
	}

	private boolean isLastElement(int index, List<RegistryLookupMetric> registryLookupMetrics) {
		return index == registryLookupMetrics.size() - 1;
	}

	private RegistryLookupMetric toDTO(RegistryLookupMetricEntity entity) {
		return new RegistryLookupMetric(
			entity.getStartDate(),
			entity.getStatus(),
			entity.getSuccessShellDescriptorsFetchCount(),
			entity.getFailedShellDescriptorsFetchCount(),
			entity.getEndDate()
		);
	}
}
