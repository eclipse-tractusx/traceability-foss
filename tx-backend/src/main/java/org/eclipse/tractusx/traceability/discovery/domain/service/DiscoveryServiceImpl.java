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

package org.eclipse.tractusx.traceability.discovery.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepository;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.repository.DiscoveryRepository;
import org.eclipse.tractusx.traceability.discovery.infrastructure.exception.DiscoveryFinderException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.discovery.domain.model.Discovery.mergeDiscoveriesAndRemoveDuplicates;
import static org.eclipse.tractusx.traceability.discovery.domain.model.Discovery.toDiscovery;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscoveryServiceImpl implements DiscoveryService {

    private final DiscoveryRepository discoveryRepository;

    private final BpnRepository bpnRepository;

    private final EdcProperties edcProperties;


    @Override
    public Discovery getDiscoveryByBPN(String bpn) {
        List<Discovery> discoveryList = new ArrayList<>();
        try {
            Optional<Discovery> optionalDiscoveryFromDiscoveryService = getOptionalDiscoveryByBpnFromDiscoveryService(bpn);
            optionalDiscoveryFromDiscoveryService.ifPresent(discovery -> {
                discovery.setReceiverUrls(
                        discovery.getReceiverUrls().stream().map(
                                DiscoveryServiceImpl::removeTrailingSlash
                        ).toList()
                );
                log.info("Retrieved discovery by bpn from edcDiscoveryService receiverUrls: {}, senderUrls: {}", discovery.getReceiverUrls().toString(), discovery.getSenderUrl());
                discoveryList.add(discovery);
            });
            optionalDiscoveryFromDiscoveryService.ifPresent(discoveryList::add);
        } catch (Exception e) {
            throw new DiscoveryFinderException("DiscoveryFinder could not determine result.");
        }

        Optional<Discovery> optionalDiscoveryFromBpnDatabase = getOptionalDiscoveryFromBpnDatabase(bpn);
        optionalDiscoveryFromBpnDatabase.ifPresent(discovery -> log.info("Retrieved discovery by bpn from BPN Mapping Table receiverUrls: {}, senderUrls: {}", discovery.getReceiverUrls().toString(), discovery.getSenderUrl()));
        optionalDiscoveryFromBpnDatabase.ifPresent(discoveryList::add);
        return mergeDiscoveriesAndRemoveDuplicates(discoveryList);
    }

    private static String removeTrailingSlash(String inputString) {
        if (inputString.endsWith("/")) {
            return inputString.substring(0, inputString.length() - 1);
        }
        return inputString;
    }

    @NotNull
    private Optional<Discovery> getOptionalDiscoveryByBpnFromDiscoveryService(String bpn) {
        return discoveryRepository.retrieveDiscoveryByFinderAndEdcDiscoveryService(bpn);
    }

    @NotNull
    private Optional<Discovery> getOptionalDiscoveryFromBpnDatabase(String bpn) {
        if (bpnRepository.existsWhereUrlNotNull(bpn)) {
            String receiverUrl = bpnRepository.findByIdOrThrowNotFoundException(bpn).url();
            Discovery discovery = toDiscovery(receiverUrl, edcProperties.getProviderEdcUrl());
            return Optional.of(discovery);
        }
        return Optional.empty();
    }


}
