/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.rest;

import io.github.resilience4j.core.functions.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.Shell;
import org.eclipse.tractusx.irs.registryclient.decentral.DecentralDigitalTwinRegistryService;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.repository.DecentralRegistryRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DecentralRegistryRepositoryImpl implements DecentralRegistryRepository {

    private final DecentralDigitalTwinRegistryService decentralDigitalTwinRegistryService;

    @Override
    public List<Shell> retrieveShellDescriptorsByBpn(String bpn) {
        try {
            List<Either<Exception, Shell>> list = decentralDigitalTwinRegistryService.lookupShellsByBPN(bpn).stream().toList();
            return list.stream().map(Either::get).toList();
        } catch (RegistryServiceException exception) {
            log.error("Could not retrieve globalAssetIds by bpn " + bpn, exception);
            return Collections.emptyList();
        }
    }

}
