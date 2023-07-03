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

import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.spi.types.domain.callback.CallbackAddress;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model.TransferProcessRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.configuration.JsonLdConfiguration.NAMESPACE_EDC;

/**
 * Transformer to convert TransferProcessRequest to JSON-LD.
 */
public class JsonObjectFromTransferProcessRequestTransformer
        extends AbstractJsonLdTransformer<TransferProcessRequest, JsonObject> {
    private final JsonBuilderFactory jsonFactory;

    public JsonObjectFromTransferProcessRequestTransformer(final JsonBuilderFactory jsonFactory) {
        super(TransferProcessRequest.class, JsonObject.class);
        this.jsonFactory = jsonFactory;
    }

    @Override
    public @Nullable JsonObject transform(@NotNull final TransferProcessRequest dto, @NotNull final TransformerContext context) {
        final JsonObjectBuilder builder = this.jsonFactory.createObjectBuilder();
        builder.add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_ASSET_ID, dto.getAssetId())
                .add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_CONNECTOR_ADDRESS, dto.getConnectorAddress())
                .add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_CONTRACT_ID, dto.getContractId())
                .add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_DATA_DESTINATION,
                        context.transform(dto.getDataDestination(), JsonObject.class))
                .add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_PROTOCOL, dto.getProtocol())
                .add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_MANAGED_RESOURCES, dto.isManagedResources());

        Optional.ofNullable(dto.getConnectorId())
                .ifPresent(s -> builder.add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_CONNECTOR_ID,
                        dto.getConnectorId()));
        Optional.ofNullable(dto.getCallbackAddresses())
                .ifPresent(s -> builder.add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_CALLBACK_ADDRESSES,
                        asArray(dto.getCallbackAddresses(), context)));

        if (dto.getProperties() != null && !dto.getProperties().isEmpty()) {
            final JsonObjectBuilder objectBuilder = jsonFactory.createObjectBuilder();
            dto.getProperties().forEach((s, s1) -> objectBuilder.add(NAMESPACE_EDC + s, s1));
            builder.add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_PROPERTIES, objectBuilder);
        }

        if (dto.getPrivateProperties() != null && !dto.getPrivateProperties().isEmpty()) {
            final JsonObjectBuilder objectBuilder = jsonFactory.createObjectBuilder();
            dto.getPrivateProperties().forEach((s, s1) -> objectBuilder.add(NAMESPACE_EDC + s, s1));
            builder.add(TransferProcessRequest.EDC_TRANSFER_REQUEST_DTO_PRIVATE_PROPERTIES, objectBuilder);
        }
        return builder.build();
    }

    private JsonArrayBuilder asArray(final List<CallbackAddress> callbackAddresses, final TransformerContext context) {
        final JsonArrayBuilder arrayBuilder = Objects.requireNonNull(this.jsonFactory.createArrayBuilder());
        callbackAddresses.stream()
                .map(callbackAddress -> context.transform(callbackAddress, JsonObject.class))
                .forEach(arrayBuilder::add);
        return arrayBuilder;
    }
}
