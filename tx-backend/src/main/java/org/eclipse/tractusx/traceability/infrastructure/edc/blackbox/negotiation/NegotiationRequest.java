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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.edc.spi.types.domain.callback.CallbackAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * EDC negotiation request.
 */
@Value
@Builder(toBuilder = true)
@Jacksonized
public class NegotiationRequest {
    public static final String NEGOTIATION_CONNECTOR_ADDRESS = "https://w3id.org/edc/v0.0.1/ns/connectorAddress";
    public static final String NEGOTIATION_PROTOCOL = "https://w3id.org/edc/v0.0.1/ns/protocol";
    public static final String NEGOTIATION_CONNECTOR_ID = "https://w3id.org/edc/v0.0.1/ns/connectorId";
    public static final String NEGOTIATION_PROVIDER_ID = "https://w3id.org/edc/v0.0.1/ns/providerId";
    public static final String NEGOTIATION_CONSUMER_ID = "https://w3id.org/edc/v0.0.1/ns/consumerId";
    public static final String NEGOTIATION_OFFER = "https://w3id.org/edc/v0.0.1/ns/offer";
    public static final String NEGOTIATION_CALLBACK_ADDRESSES = "https://w3id.org/edc/v0.0.1/ns/callbackAddresses";
    public static final String NEGOTIATION_OFFER_ID = "https://w3id.org/edc/v0.0.1/ns/offerId";
    public static final String NEGOTIATION_ASSET_ID = "https://w3id.org/edc/v0.0.1/ns/assetId";
    public static final String NEGOTIATION_POLICY = "https://w3id.org/edc/v0.0.1/ns/policy";

    @NotBlank(message = "connectorAddress is mandatory")
    String connectorAddress;
    @NotBlank(message = "protocol is mandatory")
    String protocol;
    @NotBlank(message = "connectorId is mandatory")
    String connectorId;
    @NotNull(message = "offer cannot be null")
    ContractOfferDescription offer;
    String providerId;
    String consumerId;
    List<CallbackAddress> callbackAddresses = new ArrayList<>();
}

