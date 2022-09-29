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

import net.catenax.traceability.assets.domain.model.InvestigationStatus;
import net.catenax.traceability.assets.domain.model.QualityType;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
public class AssetEntity {
	@Id
	private String id;
	private String idShort;
	private String nameAtManufacturer;
	private String manufacturerPartId;
	private String manufacturerId;
	private String batchId;
	private String manufacturerName;
	private String nameAtCustomer;
	private String customerPartId;
	private Instant manufacturingDate;
	private String manufacturingCountry;
	private boolean supplierPart;
	private QualityType qualityType;
	@ElementCollection(fetch = FetchType.EAGER)
	private List<ChildDescription> childDescriptors;
	@OneToOne(cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	private PendingInvestigation pendingInvestigation;

	public AssetEntity(String id, String idShort, String nameAtManufacturer,
					   String manufacturerPartId,  String manufacturerId, String batchId,
					   String manufacturerName, String nameAtCustomer,
					   String customerPartId, Instant manufacturingDate,
					   String manufacturingCountry, boolean supplierPart,
					   List<ChildDescription> childDescriptors, QualityType qualityType) {
		this.id = id;
		this.idShort = idShort;
		this.nameAtManufacturer = nameAtManufacturer;
		this.manufacturerPartId = manufacturerPartId;
		this.manufacturerId = manufacturerId;
		this.batchId = batchId;
		this.manufacturerName = manufacturerName;
		this.nameAtCustomer = nameAtCustomer;
		this.customerPartId = customerPartId;
		this.manufacturingDate = manufacturingDate;
		this.manufacturingCountry = manufacturingCountry;
		this.supplierPart = supplierPart;
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

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
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

	public PendingInvestigation getPendingInvestigation() {
		return pendingInvestigation;
	}

	public void setPendingInvestigation(PendingInvestigation pendingInvestigation) {
		this.pendingInvestigation = pendingInvestigation;
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

	public boolean isSupplierPart() {
		return supplierPart;
	}

	public void setSupplierPart(boolean supplierPart) {
		this.supplierPart = supplierPart;
	}

	public QualityType getQualityType() {
		return qualityType;
	}

	public void setQualityType(QualityType qualityType) {
		this.qualityType = qualityType;
	}

	public boolean isOnInvestigation() {
		return pendingInvestigation != null && pendingInvestigation.status == InvestigationStatus.PENDING;
	}

	@Entity
	@Table(name = "pending_investigation")
	public static class PendingInvestigation {
		@Id
		private String assetId;
		private InvestigationStatus status;
		private String description;
		private ZonedDateTime created;
		private ZonedDateTime updated;

		public PendingInvestigation() {
		}

		private PendingInvestigation(String assetId, String description, InvestigationStatus status, ZonedDateTime created, ZonedDateTime updated) {
			this.assetId = assetId;
			this.status = status;
			this.description = description;
			this.created = created;
			this.updated = updated;
		}

		public static PendingInvestigation newInvestigation(String assetId, String description) {
			ZonedDateTime now = ZonedDateTime.now();
			return new PendingInvestigation(assetId, description, InvestigationStatus.PENDING, now, now);
		}

		public String getAssetId() {
			return assetId;
		}

		public void setAssetId(String assetId) {
			this.assetId = assetId;
		}

		public InvestigationStatus getStatus() {
			return status;
		}

		public ZonedDateTime getCreated() {
			return created;
		}

		public void setCreated(ZonedDateTime created) {
			this.created = created;
		}

		public ZonedDateTime getUpdated() {
			return updated;
		}

		public void setUpdated(ZonedDateTime updated) {
			this.updated = updated;
		}

		public void setStatus(InvestigationStatus status) {
			this.status = status;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
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
