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

package org.eclipse.tractusx.traceability.assets.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.time.Instant;
import java.util.List;

public final class Asset {
	@ApiModelProperty(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd")
	private final String id;
	@ApiModelProperty(example = "--")
	private final String idShort;
	@ApiModelProperty(example = "Door f-r")
	private final String nameAtManufacturer;
	@ApiModelProperty(example = "33740332-54")
	private final String manufacturerPartId;
	@ApiModelProperty(example = "NO-297452866581906730261974")
	private final String partInstanceId;
	@ApiModelProperty(example = "BPNL00000003CSGV")
	private final String manufacturerId;
	@ApiModelProperty(example = "--")
	private final String batchId;
	@ApiModelProperty(example = "Tier C")
	private String manufacturerName;
	@ApiModelProperty(example = "Door front-right")
	private final String nameAtCustomer;
	@ApiModelProperty(example = "33740332-54")
	private final String customerPartId;
	@ApiModelProperty(example = "2022-02-04T13:48:54Z")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private final Instant manufacturingDate;
	@ApiModelProperty(example = "DEU")
	private final String manufacturingCountry;
	@ApiModelProperty(example = "true")
	private final boolean supplierPart;

	private List<ChildDescriptions> childDescriptions;
	@ApiModelProperty(example = "false")
	private boolean underInvestigation;
	@ApiModelProperty(example = "Ok")
	private QualityType qualityType;
	@ApiModelProperty(example = "--")
	private String van;

	public Asset(
		String id,
		String idShort,
		String nameAtManufacturer,
		String manufacturerPartId,
		String partInstanceId,
		String manufacturerId,
		String batchId,
		String manufacturerName,
		String nameAtCustomer,
		String customerPartId,
		Instant manufacturingDate,
		String manufacturingCountry,
		boolean supplierPart,
		List<ChildDescriptions> childDescriptions,
		boolean underInvestigation,
		QualityType qualityType,
		String van
	) {
		this.id = id;
		this.idShort = idShort;
		this.nameAtManufacturer = nameAtManufacturer;
		this.manufacturerPartId = manufacturerPartId;
		this.partInstanceId = partInstanceId;
		this.manufacturerId = manufacturerId;
		this.batchId = batchId;
		this.manufacturerName = manufacturerName;
		this.nameAtCustomer = nameAtCustomer;
		this.customerPartId = customerPartId;
		this.manufacturingDate = manufacturingDate;
		this.manufacturingCountry = manufacturingCountry;
		this.supplierPart = supplierPart;
		this.childDescriptions = childDescriptions;
		this.underInvestigation = underInvestigation;
		this.qualityType = qualityType;
		this.van = van;
	}

	public String getBatchId() {
		return batchId;
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

	public String getManufacturerPartId() {
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

	public boolean isSupplierPart() {
		return supplierPart;
	}

	public String getPartInstanceId() {
		return partInstanceId;
	}

	public List<ChildDescriptions> getChildDescriptions() {
		return childDescriptions;
	}

	public QualityType getQualityType() {
		return qualityType;
	}

	public boolean isUnderInvestigation() {
		return underInvestigation;
	}

	public record ChildDescriptions(
		@ApiModelProperty(example = "urn:uuid:a4a26b9c-9460-4cc5-8645-85916b86adb0") String id,
		@ApiModelProperty(example = "null") String idShort) {
	}

	public String getVan() {
		return van;
	}
}
