/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.bpn.domain.service;

import static java.lang.String.format;
import static org.eclipse.tractusx.traceability.bpn.domain.model.BpdmRequest.toBpdmRequest;
import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.EDC_CLIENT_REST_TEMPLATE;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.edr.EndpointDataReference;
import org.eclipse.tractusx.irs.edc.client.ContractNegotiationService;
import org.eclipse.tractusx.irs.edc.client.EDCCatalogFacade;
import org.eclipse.tractusx.irs.edc.client.model.CatalogItem;
import org.eclipse.tractusx.irs.edc.client.storage.EndpointDataReferenceStorage;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpdmRequest;
import org.eclipse.tractusx.traceability.bpn.infrastructure.model.BusinessPartnerResponse;
import org.eclipse.tractusx.traceability.common.properties.BpdmProperties;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.BadRequestException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.ContractNegotiationException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.NoEndpointDataReferenceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class BpnEdcFacade {

    private final BpdmProperties bpdmProperties;
    private final EDCCatalogFacade edcCatalogFacade;
    private final ContractNegotiationService contractNegotiationService;
    private final EndpointDataReferenceStorage endpointDataReferenceStorage;
    private final RestTemplate restTemplate;

    public BpnEdcFacade(
            BpdmProperties bpdmProperties,
            EDCCatalogFacade edcCatalogFacade, ContractNegotiationService contractNegotiationService,
            EndpointDataReferenceStorage endpointDataReferenceStorage,
            @Qualifier(EDC_CLIENT_REST_TEMPLATE) RestTemplate restTemplate) {
        this.bpdmProperties = bpdmProperties;
        this.edcCatalogFacade = edcCatalogFacade;
        this.contractNegotiationService = contractNegotiationService;
        this.endpointDataReferenceStorage = endpointDataReferenceStorage;
        this.restTemplate = restTemplate;
    }

    public BusinessPartnerResponse resolveBusinessPartnerByBpn(String bpn) {
        List<CatalogItem> catalogItems = edcCatalogFacade.fetchCatalogItems(
                CatalogRequest.Builder.newInstance()
                        .protocol("dataspace-protocol-http")
                        .counterPartyAddress(bpdmProperties.getProviderUrl())
                        .counterPartyId(bpdmProperties.getProviderBpnl())
                        .querySpec(QuerySpec.Builder.newInstance()
                                .filter(
                                        List.of(
                                                new Criterion("https://purl.org/dc/terms/subject", "=", "cx-taxo:ReadAccessPoolForCatenaXMember"),
                                                new Criterion("https://w3id.org/catenax/ontology/common/version", "=", "6.0"),
                                                new Criterion("https://w3id.org/edc/v0.0.1/ns/BusinessPartnerNumber", "=", bpn))
                                )
                                .build())
                        .build()
        );

        if (catalogItems.isEmpty()) {
            throw new IllegalStateException("No catalog entries found for BPDM service");
        }

        log.info("Found catalog items for BPDM service: {}", catalogItems);

        CatalogItem firstCatalogItem = catalogItems.get(0);

        log.info("Negotiating the contract for catalog item: {}", firstCatalogItem);

        String contractAgreementId = negotiateContractAgreement(
            bpdmProperties.getProviderUrl(),
            firstCatalogItem,
            bpdmProperties.getProviderBpnl());

        final EndpointDataReference dataReference = endpointDataReferenceStorage.get(contractAgreementId)
                .orElseThrow(() -> new NoEndpointDataReferenceException("No EndpointDataReference was found"));

        return sendRequest(toBpdmRequest(dataReference, bpn));
    }

    private String negotiateContractAgreement(String url, CatalogItem catalogItem, String bpn) {
        try {
            return Optional.ofNullable(contractNegotiationService.negotiate(url, catalogItem, null, bpn))
                    .orElseThrow()
                    .getContractId();
        } catch (Exception e) {
            throw new ContractNegotiationException("Failed to negotiate contract agreement: " + e.getMessage(), e);
        }
    }

    private BusinessPartnerResponse sendRequest(final BpdmRequest request) {
        try {
            ResponseEntity<BusinessPartnerResponse> response = restTemplate.exchange(
                request.getUrl(),
                HttpMethod.POST,
                new HttpEntity<>(request.getBody(), request.getHeaders()),
                BusinessPartnerResponse.class);

            log.info("Control plane responded with status: {}", response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BadRequestException(format("Control plane responded with: %s", response.getStatusCode()));
            }
            return response.getBody();
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

}
