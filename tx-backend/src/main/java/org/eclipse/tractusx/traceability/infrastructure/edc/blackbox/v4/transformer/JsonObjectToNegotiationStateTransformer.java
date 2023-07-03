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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.transformer;

import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model.NegotiationState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Transformer to convert JSON-LD to NegotiationState.
 */
public class JsonObjectToNegotiationStateTransformer extends AbstractJsonLdTransformer<JsonObject, NegotiationState> {
    public static final String CONTRACT_NEGOTIATION_STATE = "https://w3id.org/edc/v0.0.1/ns/state";

    protected JsonObjectToNegotiationStateTransformer() {
        super(JsonObject.class, NegotiationState.class);
    }

    @Override
    public @Nullable NegotiationState transform(@NotNull final JsonObject jsonObject,
                                                @NotNull final TransformerContext transformerContext) {
        final NegotiationState.NegotiationStateBuilder builder = NegotiationState.builder();
        jsonObject.forEach((key, value) -> {
            if (CONTRACT_NEGOTIATION_STATE.equals(key)) {
                builder.state(this.transformString(value, transformerContext));
            }
        });
        return builder.build();
    }
}
