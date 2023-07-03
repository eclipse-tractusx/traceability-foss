/********************************************************************************
 * Copyright (c) 2021,2022,2023
 *       2022: ZF Friedrichshafen AG
 *       2022: ISTOS GmbH
 *       2022,2023: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *       2022,2023: BOSCH AG
 * Copyright (c) 2021,2022,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0. *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.callback.CallbackAddress;

import java.util.List;
import java.util.Map;

/**
 * EDC transfer process request.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class TransferProcessRequest {

    public static final String EDC_TRANSFER_REQUEST_DTO_ASSET_ID = "https://w3id.org/edc/v0.0.1/ns/assetId";
    public static final String EDC_TRANSFER_REQUEST_DTO_CONNECTOR_ADDRESS = "https://w3id.org/edc/v0.0.1/ns/connectorAddress";
    public static final String EDC_TRANSFER_REQUEST_DTO_CONNECTOR_ID = "https://w3id.org/edc/v0.0.1/ns/connectorId";
    public static final String EDC_TRANSFER_REQUEST_DTO_CONTRACT_ID = "https://w3id.org/edc/v0.0.1/ns/contractId";
    public static final String EDC_TRANSFER_REQUEST_DTO_DATA_DESTINATION = "https://w3id.org/edc/v0.0.1/ns/dataDestination";
    public static final String EDC_TRANSFER_REQUEST_DTO_PROTOCOL = "https://w3id.org/edc/v0.0.1/ns/protocol";
    public static final String EDC_TRANSFER_REQUEST_DTO_MANAGED_RESOURCES = "https://w3id.org/edc/v0.0.1/ns/managedResources";
    public static final String EDC_TRANSFER_REQUEST_DTO_CALLBACK_ADDRESSES = "https://w3id.org/edc/v0.0.1/ns/callbackAddresses";
    public static final String EDC_TRANSFER_REQUEST_DTO_PROPERTIES = "https://w3id.org/edc/v0.0.1/ns/properties";
    public static final String EDC_TRANSFER_REQUEST_DTO_PRIVATE_PROPERTIES = "https://w3id.org/edc/v0.0.1/ns/privateProperties";

    public static final String DEFAULT_PROTOCOL = "dataspace-protocol-http";
    public static final boolean DEFAULT_MANAGED_RESOURCES = false;

    private String assetId;
    private String connectorAddress;
    private String connectorId;
    private String contractId;
    private DataAddress dataDestination;
    private String protocol;
    private boolean managedResources;
    private List<CallbackAddress> callbackAddresses;
    private Map<String, String> properties;
    private Map<String, String> privateProperties;

}
