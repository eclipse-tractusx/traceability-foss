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

public final class Asset {
	private final String id;
	private final String idShort;
	private final String nameAtManufacturer;
	private final String manufacturerPartId;
	private final String manufacturerId;
	private String manufacturerName;
	private final String nameAtCustomer;
	private final String customerPartId;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private final Instant manufacturingDate;
	private final String manufacturingCountry;
	private final Map<String, String> specificAssetIds;
	private List<ChildDescriptions> childDescriptions;
	private QualityType qualityType;

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
		Map<String, String> specificAssetIds,
		List<ChildDescriptions> childDescriptions,
		QualityType qualityType
	) {
		this.id = id;
		this.idShort = idShort;
		this.nameAtManufacturer = nameAtManufacturer;
		this.manufacturerPartId = manufacturerPartId;
		this.manufacturerId = manufacturerId;
		this.manufacturerName = manufacturerName;
		this.nameAtCustomer = nameAtCustomer;
		this.customerPartId = customerPartId;
		this.manufacturingDate = manufacturingDate;
		this.manufacturingCountry = manufacturingCountry;
		this.specificAssetIds = specificAssetIds;
		this.childDescriptions = childDescriptions;
		this.qualityType = qualityType;
	}

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

	public void updateChildDescriptions(List<ChildDescriptions> childDescriptions) {
		if (childDescriptions == null) {
			childDescriptions = Collections.emptyList();
		}
		this.childDescriptions = childDescriptions;
	}

	public ChildDescriptions getChild(String childId) {
		return childDescriptions.stream()
			.filter(description -> description.id.equals(childId))
			.findFirst()
			.orElse(null);
	}

	public void updateManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public void updateQualityType(QualityType qualityType) {
		this.qualityType = qualityType;
	}

	public String getId() {
		return id;
	}

	public String getIdShort() {
		return idShort;
	}

	public String getNameAtManufacturer() {
		return nameAtManufacturer;
	}

	public String getNanufacturerPartId() {
		return manufacturerPartId;
	}

	public String getManufacturerId() {
		return manufacturerId;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public String getNameAtCustomer() {
		return nameAtCustomer;
	}

	public String getCustomerPartId() {
		return customerPartId;
	}

	public Instant getManufacturingDate() {
		return manufacturingDate;
	}

	public String getManufacturingCountry() {
		return manufacturingCountry;
	}

	public Map<String, String> getSpecificAssetIds() {
		return specificAssetIds;
	}

	public List<ChildDescriptions> getChildDescriptions() {
		return childDescriptions;
	}

	public QualityType getQualityType() {
		return qualityType;
	}

	public static final class ChildDescriptions {
		private final String id;
		private final String idShort;

		public ChildDescriptions(String id, String idShort) {
			this.id = id;
			this.idShort = idShort;
		}

		public String getId() {
			return id;
		}

		public String getIdShort() {
			return idShort;
		}

	}

}
