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
package org.eclipse.tractusx.traceability.aas.domain.service;


import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.irs.registryclient.decentral.IdentifierKeyValuePairLite;
import org.eclipse.tractusx.irs.registryclient.decentral.LookupShellsFilter;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.aas.application.service.DTRService;
import org.eclipse.tractusx.traceability.aas.domain.model.DTR;
import org.eclipse.tractusx.traceability.aas.domain.model.TwinType;
import org.eclipse.tractusx.traceability.aas.domain.repository.DTRRepository;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DTRServiceImpl implements DTRService {
    private final DTRRepository dtrRepository;
    private static final String PLACEHOLDER_DIGITAL_TWIN_TYPE = "digitalTwinType";
    private static final String PLACEHOLDER_BPN = "manufacturerId";
    private final TraceabilityProperties traceabilityProperties;

    @Override
    public DTR lookupAASShells(TwinType digitalTwinType, String cursor, int limit) throws RegistryServiceException {
        IdentifierKeyValuePairLite digitalTwinTypeKeyValue = IdentifierKeyValuePairLite.builder()
                .name(PLACEHOLDER_DIGITAL_TWIN_TYPE)
                .value((digitalTwinType.getValue()))
                .build();

        // TODO TRACEX-466 Currently there is a problem while the edc is proxying the two query params which leads to an empty result - therefore we need to ignore the bpn for now.
       /* IdentifierKeyValuePairLite bpnKeyValue = IdentifierKeyValuePairLite.builder()
                .name(PLACEHOLDER_BPN)
                .value((traceabilityProperties.getBpn().value()))
                .build();*/

        LookupShellsFilter lookupShellsFilter = LookupShellsFilter.builder()
                .cursor(cursor)
                .limit(limit)
                .identifierKeyValuePairs(List.of(digitalTwinTypeKeyValue))
                .build();
        return dtrRepository.lookupShellIdentifiers(lookupShellsFilter);
    }
}
