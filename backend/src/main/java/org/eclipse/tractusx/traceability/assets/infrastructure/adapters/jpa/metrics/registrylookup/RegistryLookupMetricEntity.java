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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.metrics.registrylookup;

import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.metrics.RegistryLookupStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "registry_lookup_metrics")
public class RegistryLookupMetricEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Instant startDate;

	private RegistryLookupStatus status;

	private Long successShellDescriptorsFetchCount;

	private Long failedShellDescriptorsFetchCount;

	private Instant endDate;

	public RegistryLookupMetricEntity() {
	}

	public RegistryLookupMetricEntity(Instant startDate,
									  RegistryLookupStatus status,
									  Long successShellDescriptorsFetchCount,
									  Long failedShellDescriptorsFetchCount,
									  Instant endDate) {
		this.startDate = startDate;
		this.status = status;
		this.successShellDescriptorsFetchCount = successShellDescriptorsFetchCount;
		this.failedShellDescriptorsFetchCount = failedShellDescriptorsFetchCount;
		this.endDate = endDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instant getStartDate() {
		return startDate;
	}

	public void setStartDate(Instant startDate) {
		this.startDate = startDate;
	}

	public RegistryLookupStatus getStatus() {
		return status;
	}

	public void setStatus(RegistryLookupStatus registryLookupStatus) {
		this.status = registryLookupStatus;
	}

	public Long getSuccessShellDescriptorsFetchCount() {
		return successShellDescriptorsFetchCount;
	}

	public void setSuccessShellDescriptorsFetchCount(Long successShellDescriptorsFetchCount) {
		this.successShellDescriptorsFetchCount = successShellDescriptorsFetchCount;
	}

	public Long getFailedShellDescriptorsFetchCount() {
		return failedShellDescriptorsFetchCount;
	}

	public void setFailedShellDescriptorsFetchCount(Long failedShellDescriptorsFetchCount) {
		this.failedShellDescriptorsFetchCount = failedShellDescriptorsFetchCount;
	}

	public Instant getEndDate() {
		return endDate;
	}

	public void setEndDate(Instant endDate) {
		this.endDate = endDate;
	}
}
