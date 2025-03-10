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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.eclipse.edc.spi.types.domain.edr.EndpointDataReference;
import org.eclipse.tractusx.irs.edc.client.ContractNegotiationService;
import org.eclipse.tractusx.irs.edc.client.EDCCatalogFacade;
import org.eclipse.tractusx.irs.edc.client.exceptions.ContractNegotiationException;
import org.eclipse.tractusx.irs.edc.client.exceptions.TransferProcessException;
import org.eclipse.tractusx.irs.edc.client.exceptions.UsagePolicyExpiredException;
import org.eclipse.tractusx.irs.edc.client.exceptions.UsagePolicyPermissionException;
import org.eclipse.tractusx.irs.edc.client.model.CatalogItem;
import org.eclipse.tractusx.irs.edc.client.model.TransferProcessResponse;
import org.eclipse.tractusx.irs.edc.client.storage.EndpointDataReferenceStorage;
import org.eclipse.tractusx.traceability.bpn.infrastructure.model.BusinessPartnerResponse;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class BpnEdcFacadeTest {

    @Mock
    private EdcProperties edcProperties;

    @Mock
    private TraceabilityProperties traceabilityProperties;

    @Mock
    private EDCCatalogFacade edcCatalogFacade;

    @Mock
    private ContractNegotiationService contractNegotiationService;

    @Mock
    private EndpointDataReferenceStorage endpointDataReferenceStorage;

    @Mock
    private RestTemplate restTemplate;

    private BpnEdcFacade bpnEdcFacade;

    @BeforeEach
    void setUp() {
        bpnEdcFacade = new BpnEdcFacade(edcProperties, edcCatalogFacade, contractNegotiationService, endpointDataReferenceStorage, restTemplate, traceabilityProperties);
    }

    @Test
    void shouldResolveBusinessPartnerByBpn()
            throws TransferProcessException, UsagePolicyExpiredException, UsagePolicyPermissionException, ContractNegotiationException {
        // given
        final String bpn = "bpn";
        final String legalName = "legalName";
        final String providerUrl = "providerUrl";
        final String dspUrl = "/api/v1/dsp";
        final String contractAgreementId = "contractAgreementId";
        final String endpoint = "endpoint";

        when(traceabilityProperties.getBpn()).thenReturn(BPN.of(bpn));
        when(edcProperties.getProviderEdcUrl()).thenReturn(providerUrl);
        when(edcProperties.getIdsPath()).thenReturn(dspUrl);

        BusinessPartnerResponse businessPartnerResponse = BusinessPartnerResponse.builder()
                .legalName(legalName)
                .build();

        when(edcCatalogFacade.fetchCatalogItems(any())).thenReturn(List.of(CatalogItem.builder().build()));

        when(contractNegotiationService.negotiate(
                any(),
                any(),
                any(),
                any()))
                .thenReturn(TransferProcessResponse.builder()
                        .contractId(contractAgreementId)
                        .build());

        EndpointDataReference edr = EndpointDataReference.Builder.newInstance()
                .authKey("authKey")
                .authCode("authCode")
                .endpoint(endpoint)
                .id("id")
                .contractId("contractId")
                .build();

        when(endpointDataReferenceStorage.get(eq(contractAgreementId))).thenReturn(Optional.of(edr));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(BusinessPartnerResponse.class)))
                .thenReturn(ResponseEntity.ok(businessPartnerResponse));

        // when
        BusinessPartnerResponse response = bpnEdcFacade.resolveBusinessPartnerByBpn(bpn);

        // then
        assertThat(legalName).isEqualTo(response.getLegalName());
        verify(restTemplate).exchange(
                eq("endpoint/members/legal-entities/search"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(BusinessPartnerResponse.class));
    }

}
