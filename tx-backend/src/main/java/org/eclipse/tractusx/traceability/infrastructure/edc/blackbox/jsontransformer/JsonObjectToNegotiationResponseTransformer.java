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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.jsontransformer;

import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.NegotiationResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Transformer to convert JSON-LD to NegotiationResponse.
 */
public class JsonObjectToNegotiationResponseTransformer
        extends AbstractJsonLdTransformer<JsonObject, NegotiationResponse> {

    public static final String CONTRACT_NEGOTIATION_AGREEMENT_ID = "https://w3id.org/edc/v0.0.1/ns/contractAgreementId";
    public static final String CONTRACT_NEGOTIATION_COUNTERPARTY_ADDR = "https://w3id.org/edc/v0.0.1/ns/counterPartyAddress";
    public static final String CONTRACT_NEGOTIATION_ERRORDETAIL = "https://w3id.org/edc/v0.0.1/ns/errorDetail";
    public static final String CONTRACT_NEGOTIATION_PROTOCOL = "https://w3id.org/edc/v0.0.1/ns/protocol";
    public static final String CONTRACT_NEGOTIATION_STATE = "https://w3id.org/edc/v0.0.1/ns/state";
    public static final String CONTRACT_NEGOTIATION_NEG_TYPE = "https://w3id.org/edc/v0.0.1/ns/type";
    public static final String CONTRACT_NEGOTIATION_CALLBACK_ADDR = "https://w3id.org/edc/v0.0.1/ns/callbackAddresses";
    public static final String CONTRACT_NEGOTIATION_ID = "@id";

    public JsonObjectToNegotiationResponseTransformer() {
        super(JsonObject.class, NegotiationResponse.class);
    }

    @Override
    public @Nullable NegotiationResponse transform(@NotNull final JsonObject jsonObject,
                                                   @NotNull final TransformerContext transformerContext) {
        final NegotiationResponse.NegotiationResponseBuilder builder = NegotiationResponse.builder();
        jsonObject.forEach((key, value) -> {
            switch (key) {
                case CONTRACT_NEGOTIATION_ID -> builder.responseId(this.transformString(value, transformerContext));
                case CONTRACT_NEGOTIATION_NEG_TYPE -> builder.type(this.transformString(value, transformerContext));
                case CONTRACT_NEGOTIATION_PROTOCOL -> builder.protocol(this.transformString(value, transformerContext));

                case CONTRACT_NEGOTIATION_STATE -> builder.state(this.transformString(value, transformerContext));

                case CONTRACT_NEGOTIATION_COUNTERPARTY_ADDR ->
                        builder.counterPartyAddress(this.transformString(value, transformerContext));

                case CONTRACT_NEGOTIATION_AGREEMENT_ID ->
                        builder.contractAgreementId(this.transformString(value, transformerContext));
                case CONTRACT_NEGOTIATION_CALLBACK_ADDR ->
                        builder.callbackAddresses(this.transformArray(value, String.class, transformerContext));
                case CONTRACT_NEGOTIATION_ERRORDETAIL ->
                        builder.errorDetail(this.transformString(value, transformerContext));
                default -> {
                    // Do nothing in case no key matches
                }
            }
        });
        return builder.build();
    }
}
