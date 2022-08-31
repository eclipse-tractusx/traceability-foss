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

package net.catenax.traceability.assets.infrastructure.adapters.jpa.asset;

import net.catenax.traceability.assets.domain.model.QualityType;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.List;

@Entity
public class AssetEntity {
	@Id
	private String id;
	private String idShort;
	private String nameAtManufacturer;
	private String manufacturerPartId;
	private String manufacturerId;
	private String manufacturerName;
	private String nameAtCustomer;
	private String customerPartId;
	private Instant manufacturingDate;
	private String manufacturingCountry;
	private QualityType qualityType;
	@ElementCollection
	private List<ChildDescription> childDescriptors;

	public AssetEntity(String id, String idShort, String nameAtManufacturer,
					   String manufacturerPartId,  String manufacturerId,
					   String manufacturerName, String nameAtCustomer,
					   String customerPartId, Instant manufacturingDate,
					   String manufacturingCountry, List<ChildDescription> childDescriptors,
					   QualityType qualityType) {
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
		this.childDescriptors = childDescriptors;
		this.qualityType = qualityType;
	}

	public AssetEntity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String assetId) {
		this.id = assetId;
	}

	public List<ChildDescription> getChildDescriptors() {
		return childDescriptors;
	}

	public void setChildDescriptors(List<ChildDescription> specificAssetIds) {
		this.childDescriptors = specificAssetIds;
	}

	public String getIdShort() {
		return idShort;
	}

	public void setIdShort(String idShort) {
		this.idShort = idShort;
	}

	public String getNameAtManufacturer() {
		return nameAtManufacturer;
	}

	public void setNameAtManufacturer(String nameAtManufacturer) {
		this.nameAtManufacturer = nameAtManufacturer;
	}

	public String getManufacturerPartId() {
		return manufacturerPartId;
	}

	public void setManufacturerPartId(String manufacturerPartId) {
		this.manufacturerPartId = manufacturerPartId;
	}

	public String getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(String manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getNameAtCustomer() {
		return nameAtCustomer;
	}

	public void setNameAtCustomer(String nameAtCustomer) {
		this.nameAtCustomer = nameAtCustomer;
	}

	public String getCustomerPartId() {
		return customerPartId;
	}

	public void setCustomerPartId(String customerPartId) {
		this.customerPartId = customerPartId;
	}

	public Instant getManufacturingDate() {
		return manufacturingDate;
	}

	public void setManufacturingDate(Instant manufacturingDate) {
		this.manufacturingDate = manufacturingDate;
	}

	public String getManufacturingCountry() {
		return manufacturingCountry;
	}

	public void setManufacturingCountry(String manufacturingCountry) {
		this.manufacturingCountry = manufacturingCountry;
	}

	public QualityType getQualityType() {
		return qualityType;
	}

	public void setQualityType(QualityType qualityType) {
		this.qualityType = qualityType;
	}

	@Embeddable
	public static class ChildDescription {
		private String id;
		private String idShort;

		public ChildDescription() {
		}

		public ChildDescription(String id, String idShort) {
			this.id = id;
			this.idShort = idShort;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setIdShort(String idShort) {
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
