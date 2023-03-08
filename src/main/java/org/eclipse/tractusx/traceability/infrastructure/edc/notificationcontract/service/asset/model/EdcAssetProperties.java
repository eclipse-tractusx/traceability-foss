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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EdcAssetProperties {

	@JsonProperty("asset:prop:id")
	private final String assetId;

	@JsonProperty("asset:prop:name")
	private final String assetName;

	@JsonProperty("asset:prop:contenttype")
	private final String contentType;

	@JsonProperty("asset:prop:policy-id")
	private final String policyId;

	@JsonProperty("asset:prop:type")
	private final String type;

	@JsonProperty("asset:prop:notificationtype")
	private final String notiifcationType;

	@JsonProperty("asset:prop:notificationmethod")
	private final String notificationMethod;

	public EdcAssetProperties(String assetId,
							  String assetName,
							  String contentType,
							  String policyId,
							  String type,
							  String notiifcationType,
							  String notificationMethod) {
		this.assetId = assetId;
		this.assetName = assetName;
		this.contentType = contentType;
		this.policyId = policyId;
		this.type = type;
		this.notiifcationType = notiifcationType;
		this.notificationMethod = notificationMethod;
	}
}
