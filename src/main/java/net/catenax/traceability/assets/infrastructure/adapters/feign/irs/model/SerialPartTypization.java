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

package net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Optional;

record SerialPartTypization(
	String catenaXId,
	PartTypeInformation partTypeInformation,
	ManufacturingInformation manufacturingInformation,
	List<LocalId> localIdentifiers
) {
	public Optional<String> getLocalId(LocalIdType type) {
		return localIdentifiers.stream()
			.filter(localId -> localId.type() == type)
			.findFirst()
			.map(LocalId::value);
	}
}

record ManufacturingInformation(
	String country,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "CET") Date date
) {}

record PartTypeInformation(
	String nameAtManufacturer,
	String nameAtCustomer,
	String manufacturerPartID,
	String customerPartId
) {}

record LocalId(
	@JsonProperty("key") LocalIdType type,
	String value
) {}

enum LocalIdType {
	@JsonProperty("ManufacturerID")
	MANUFACTURER_ID,
	@JsonProperty("BatchID")
	BATCH_ID,
	@JsonEnumDefaultValue UNKNOWN
}
