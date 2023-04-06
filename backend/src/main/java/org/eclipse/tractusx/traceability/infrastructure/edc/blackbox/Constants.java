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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import okhttp3.MediaType;

public class Constants {

	// validation message
	public static final String ASSET_NOT_MATCH = "Some assets are not available";
	public static final String DESCRIPTION_MUST_PRESENT = "Description must be present";
	public static final String PARTS_IDS_MUST_PRESENT = "PartIds must be present";

	public static final String STATUS_MUST_BE_PRESENT = "status must ne present";
	public static final String ID_MUST_PRESENT = "Id must be present";
	public static final String INVESTIGATION_NOT_FOUND = "Investigation not found";


	// EDC
	public static final String ASSET_KEY_NOTIFICATION_TYPE = "asset:prop:notificationtype";
	public static final String ASSET_VALUE_QUALITY_INVESTIGATION = "qualityinvestigation";
	public static final String ASSET_VALUE_NOTIFICATION_METHOD_UPDATE = "update";
    public static final String ASSET_VALUE_NOTIFICATION_METHOD_RECEIVE = "receive";
	public static final MediaType JSON = MediaType.get("application/json");


	// only for development purpose.
	public static final String PROVIDER_IDS_PORT = "8282";
	public static final String CONSUMER_DATA_PORT = "9191";


	private Constants() {
	}

}
