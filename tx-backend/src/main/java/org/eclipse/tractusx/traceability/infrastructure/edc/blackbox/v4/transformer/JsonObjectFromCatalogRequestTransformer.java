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

import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.jsonld.spi.JsonLdKeywords;
import org.eclipse.edc.jsonld.spi.transformer.AbstractJsonLdTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Transformer to convert CatalogRequest to JSON-LD.
 */
public class JsonObjectFromCatalogRequestTransformer extends AbstractJsonLdTransformer<CatalogRequest, JsonObject> {
    private final JsonBuilderFactory jsonFactory;

    public JsonObjectFromCatalogRequestTransformer(final JsonBuilderFactory jsonFactory) {
        super(CatalogRequest.class, JsonObject.class);
        this.jsonFactory = jsonFactory;
    }

    @Override
    public @Nullable JsonObject transform(@NotNull final CatalogRequest dto,
                                          @NotNull final TransformerContext context) {
        final JsonObjectBuilder builder = this.jsonFactory.createObjectBuilder();
        builder.add(JsonLdKeywords.TYPE, CatalogRequest.EDC_CATALOG_REQUEST_TYPE)
                .add(CatalogRequest.EDC_CATALOG_REQUEST_PROVIDER_URL, dto.getProviderUrl())
                .add(CatalogRequest.EDC_CATALOG_REQUEST_PROTOCOL, dto.getProtocol());
        Optional.ofNullable(dto.getQuerySpec())
                .ifPresent(s -> builder.add(CatalogRequest.EDC_CATALOG_REQUEST_QUERY_SPEC,
                        context.transform(dto.getQuerySpec(), JsonObject.class)));

        return builder.build();
    }
}
