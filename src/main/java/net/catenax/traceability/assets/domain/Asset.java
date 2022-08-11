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

package net.catenax.traceability.assets.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record Asset(
	String id,
	String idShort,
	String nameAtManufacturer,
	String manufacturerPartId,
	String manufacturerId,
	String manufacturerName,
	String nameAtCustomer,
	String customerPartId,
	@JsonFormat(shape = JsonFormat.Shape.STRING) Instant manufacturingDate,
	String manufacturingCountry,
	Map<String, String> specificAssetIds,
	List<ChildDescriptions> childDescriptions,
	QualityType qualityType
) {

	public Asset(
		String id,
		String idShort,
		String nameAtManufacturer,
		String manufacturerPartId,
		String manufacturerId,
		String manufacturerName,
		String nameAtCustomer,
		String customerPartId,
		Instant manufacturingDate,
		String manufacturingCountry,
		QualityType qualityType
	) {
		this(
			id,
			idShort,
			nameAtManufacturer,
			manufacturerPartId,
			manufacturerId,
			manufacturerName,
			nameAtCustomer,
			customerPartId,
			manufacturingDate,
			manufacturingCountry,
			Collections.emptySortedMap(),
			Collections.emptyList(),
			qualityType
		);
	}

	public Asset withChildDescriptions(List<ChildDescriptions> childDescriptions) {
		return new Asset(
			id,
			idShort,
			nameAtManufacturer,
			manufacturerPartId,
			manufacturerId,
			manufacturerName,
			nameAtCustomer,
			customerPartId,
			manufacturingDate,
			manufacturingCountry,
			Collections.emptySortedMap(),
			childDescriptions,
			qualityType
		);
	}

	public Asset withManufacturerName(String manufacturerName) {
		return new Asset(
			id,
			idShort,
			nameAtManufacturer,
			manufacturerPartId,
			manufacturerId,
			manufacturerName,
			nameAtCustomer,
			customerPartId,
			manufacturingDate,
			manufacturingCountry,
			specificAssetIds,
			childDescriptions,
			qualityType
		);
	}

	public Asset update(QualityType qualityType) {
		return new Asset(
			id,
			idShort,
			nameAtManufacturer,
			manufacturerPartId,
			manufacturerId,
			manufacturerName,
			nameAtCustomer,
			customerPartId,
			manufacturingDate,
			manufacturingCountry,
			specificAssetIds,
			childDescriptions,
			qualityType
		);
	}

	public record ChildDescriptions(String id, String idShort){}

}
