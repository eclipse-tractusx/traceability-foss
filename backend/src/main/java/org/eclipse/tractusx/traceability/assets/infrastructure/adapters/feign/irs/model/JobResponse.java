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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record JobResponse(
	JobStatus jobStatus,
	List<Shell> shells,
	List<SerialPartTypization> serialPartTypizations,
	List<Relationship> relationships,
	Map<String, String> bpns
) {

	@JsonCreator
	static JobResponse of(
		@JsonProperty("job") JobStatus jobStatus,
		@JsonProperty("relationships")  List<Relationship> relationships,
		@JsonProperty("shells") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Shell> shells,
		@JsonProperty("submodels") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Submodel> submodels,
		@JsonProperty("bpns") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Bpn> bpns
	) {
		Map<String, String> bpnsMap = bpns.stream()
			.collect(Collectors.toMap(Bpn::manufacturerId, Bpn::manufacturerName));

		List<SerialPartTypization> serialPartTypizations = submodels.stream()
			.map(Submodel::getPayload)
			.filter(SerialPartTypization.class::isInstance)
			.map(SerialPartTypization.class::cast)
			.toList();

		return new JobResponse(
			jobStatus,
			shells,
			serialPartTypizations,
			relationships,
			bpnsMap
		);
	}

	public boolean isRunning() {
		return "RUNNING".equals(jobStatus.state());
	}

	public boolean isCompleted() {
		return "COMPLETED".equals(jobStatus.state());
	}

}

record Bpn(
	String manufacturerId,
	String manufacturerName
) {}
