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

import com.apicatalog.jsonld.document.JsonDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.connector.core.transform.TransformerContextImpl;
import org.eclipse.edc.connector.core.transform.TypeTransformerRegistryImpl;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromAssetTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromCatalogTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromCriterionTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromDataServiceTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromDatasetTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromDistributionTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromPolicyTransformer;
import org.eclipse.edc.jsonld.transformer.from.JsonObjectFromQuerySpecTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToActionTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToAssetTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToCatalogTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToConstraintTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToCriterionTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToDataServiceTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToDatasetTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToDistributionTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToDutyTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToPermissionTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToPolicyTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToProhibitionTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonObjectToQuerySpecTransformer;
import org.eclipse.edc.jsonld.transformer.to.JsonValueToGenericTypeTransformer;
import org.eclipse.edc.protocol.dsp.transferprocess.transformer.type.from.JsonObjectFromDataAddressTransformer;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model.ContractOfferDescription;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model.negotiation.NegotiationRequest;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model.negotiation.NegotiationResponse;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model.negotiation.NegotiationState;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model.transferprocess.TransferProcessRequest;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Transformer to convert between EDC models and JSON-LD.
 */
@Component
@SuppressWarnings("PMD.ExcessiveImports")
public class EdcTransformer {
    private final JsonObjectToCatalogTransformer jsonObjectToCatalogTransformer;
    private final JsonObjectFromNegotiationInitiateDtoTransformer jsonObjectFromNegotiationInitiateDtoTransformer;
    private final JsonObjectFromTransferProcessRequestTransformer jsonObjectFromTransferProcessRequestTransformer;
    private final JsonObjectFromContractOfferDescriptionTransformer jsonObjectFromContractOfferDescriptionTransformer;
    private final JsonObjectFromCatalogRequestTransformer jsonObjectFromCatalogRequestTransformer;
    private final TitaniumJsonLd titaniumJsonLd;
    private final TransformerContextImpl transformerContext;
    private final JsonObjectToNegotiationResponseTransformer jsonObjectToNegotiationResponseTransformer;
    private final JsonObjectToNegotiationStateTransformer jsonObjectToNegotiationStateTransformer;

    public EdcTransformer(final ObjectMapper objectMapper, final TitaniumJsonLd titaniumJsonLd) {
        this.titaniumJsonLd = titaniumJsonLd;
        final JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(Map.of());

        jsonObjectFromNegotiationInitiateDtoTransformer = new JsonObjectFromNegotiationInitiateDtoTransformer(
                jsonBuilderFactory);
        jsonObjectToCatalogTransformer = new JsonObjectToCatalogTransformer();
        jsonObjectFromTransferProcessRequestTransformer = new JsonObjectFromTransferProcessRequestTransformer(
                jsonBuilderFactory);
        jsonObjectFromContractOfferDescriptionTransformer = new JsonObjectFromContractOfferDescriptionTransformer(
                jsonBuilderFactory);
        jsonObjectFromCatalogRequestTransformer = new JsonObjectFromCatalogRequestTransformer(jsonBuilderFactory);
        jsonObjectToNegotiationResponseTransformer = new JsonObjectToNegotiationResponseTransformer();
        jsonObjectToNegotiationStateTransformer = new JsonObjectToNegotiationStateTransformer();

        final TypeTransformerRegistry typeTransformerRegistry = new TypeTransformerRegistryImpl();
        transformerContext = new TransformerContextImpl(typeTransformerRegistry);

        // JSON to Object
        typeTransformerRegistry.register(jsonObjectToCatalogTransformer);
        typeTransformerRegistry.register(new JsonValueToGenericTypeTransformer(objectMapper));
        typeTransformerRegistry.register(new JsonObjectToDataServiceTransformer());
        typeTransformerRegistry.register(new JsonObjectToConstraintTransformer());
        typeTransformerRegistry.register(new JsonObjectToDatasetTransformer());
        typeTransformerRegistry.register(new JsonObjectToPolicyTransformer());
        typeTransformerRegistry.register(new JsonObjectToPermissionTransformer());
        typeTransformerRegistry.register(new JsonObjectToActionTransformer());
        typeTransformerRegistry.register(new JsonObjectToDistributionTransformer());
        typeTransformerRegistry.register(new JsonObjectToProhibitionTransformer());
        typeTransformerRegistry.register(new JsonObjectToDutyTransformer());
        typeTransformerRegistry.register(new JsonObjectToAssetTransformer());
        typeTransformerRegistry.register(new JsonObjectToQuerySpecTransformer());
        typeTransformerRegistry.register(new JsonObjectToCriterionTransformer());
        typeTransformerRegistry.register(jsonObjectToNegotiationResponseTransformer);
        // JSON from Object
        typeTransformerRegistry.register(jsonObjectFromNegotiationInitiateDtoTransformer);
        typeTransformerRegistry.register(jsonObjectFromCatalogRequestTransformer);
        typeTransformerRegistry.register(jsonObjectFromTransferProcessRequestTransformer);
        typeTransformerRegistry.register(jsonObjectFromContractOfferDescriptionTransformer);
        typeTransformerRegistry.register(new JsonObjectFromQuerySpecTransformer(jsonBuilderFactory));
        typeTransformerRegistry.register(new JsonObjectFromCatalogTransformer(jsonBuilderFactory, objectMapper));
        typeTransformerRegistry.register(new JsonObjectFromDatasetTransformer(jsonBuilderFactory, objectMapper));
        typeTransformerRegistry.register(new JsonObjectFromPolicyTransformer(jsonBuilderFactory));
        typeTransformerRegistry.register(new JsonObjectFromDistributionTransformer(jsonBuilderFactory));
        typeTransformerRegistry.register(new JsonObjectFromDataServiceTransformer(jsonBuilderFactory));
        typeTransformerRegistry.register(new JsonObjectFromAssetTransformer(jsonBuilderFactory, objectMapper));
        typeTransformerRegistry.register(new JsonObjectFromCriterionTransformer(jsonBuilderFactory, objectMapper));
        typeTransformerRegistry.register(new JsonObjectFromDataAddressTransformer(jsonBuilderFactory));
    }

    public Catalog transformCatalog(final String jsonString, final Charset charset) {
        final Result<JsonObject> expand;
        try (JsonReader reader = Json.createReader(new ByteArrayInputStream(jsonString.getBytes(charset)))) {

            expand = titaniumJsonLd.expand(
                    JsonDocument.of(reader.read()).getJsonContent().orElseThrow().asJsonObject());
        }
        return jsonObjectToCatalogTransformer.transform(expand.getContent(), transformerContext);
    }

    public NegotiationResponse transformJsonToNegotiationResponse(final String jsonString, final Charset charset) {
        final Result<JsonObject> expand;
        try (JsonReader reader = Json.createReader(new ByteArrayInputStream(jsonString.getBytes(charset)))) {
            expand = titaniumJsonLd.expand(
                    JsonDocument.of(reader.read()).getJsonContent().orElseThrow().asJsonObject());
        }
        return jsonObjectToNegotiationResponseTransformer.transform(expand.getContent(), transformerContext);
    }

    public NegotiationState transformJsonToNegotiationState(final String jsonString, final Charset charset) {
        final Result<JsonObject> expand;
        try (JsonReader reader = Json.createReader(new ByteArrayInputStream(jsonString.getBytes(charset)))) {
            expand = titaniumJsonLd.expand(
                    JsonDocument.of(reader.read()).getJsonContent().orElseThrow().asJsonObject());
        }
        return jsonObjectToNegotiationStateTransformer.transform(expand.getContent(), transformerContext);
    }

    public JsonObject transformNegotiationRequestToJson(final NegotiationRequest negotiationRequest) {
        final JsonObject transform = jsonObjectFromNegotiationInitiateDtoTransformer.transform(negotiationRequest,
                transformerContext);
        return titaniumJsonLd.compact(transform).asOptional().orElseThrow();
    }

    public JsonObject transformTransferProcessRequestToJson(final TransferProcessRequest transferProcessRequest) {
        final JsonObject transform = jsonObjectFromTransferProcessRequestTransformer.transform(transferProcessRequest,
                transformerContext);
        return titaniumJsonLd.compact(transform).asOptional().orElseThrow();
    }

    public JsonObject transformContractOfferDescriptionToJson(final ContractOfferDescription contractOfferDescription) {

        final JsonObject transform = jsonObjectFromContractOfferDescriptionTransformer.transform(
                contractOfferDescription, transformerContext);
        return titaniumJsonLd.compact(transform).asOptional().orElseThrow();
    }

    public JsonObject transformCatalogRequestToJson(final CatalogRequest catalogRequest) {
        final JsonObject transform = jsonObjectFromCatalogRequestTransformer.transform(catalogRequest,
                transformerContext);
        return titaniumJsonLd.compact(transform).asOptional().orElseThrow();
    }
}
