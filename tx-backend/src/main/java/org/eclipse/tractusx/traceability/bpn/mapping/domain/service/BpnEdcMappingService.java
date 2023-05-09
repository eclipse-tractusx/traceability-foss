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

package org.eclipse.tractusx.traceability.bpn.mapping.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.model.BpnEdcMapping;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.model.BpnEdcMappingNotFoundException;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.ports.BpnEdcMappingRepository;
import org.eclipse.tractusx.traceability.bpn.mapping.infrastructure.adapters.rest.BpnEdcMappingRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BpnEdcMappingService {

    private final BpnEdcMappingRepository bpnEdcMappingRepository;

    public BpnEdcMappingService(BpnEdcMappingRepository bpnEdcMappingRepository) {
        this.bpnEdcMappingRepository = bpnEdcMappingRepository;
    }

    public List<BpnEdcMapping> findAllBpnEdcMappings() {
        return bpnEdcMappingRepository.findAll();
    }

    public List<BpnEdcMapping> saveAllBpnEdcMappings(List<BpnEdcMappingRequest> bpnEdcMappings) {
        return bpnEdcMappingRepository.saveAll(bpnEdcMappings);
    }

    public List<BpnEdcMapping> updateAllBpnEdcMappings(List<BpnEdcMappingRequest> bpnEdcMappings) {
        bpnEdcMappings.forEach(bpnEdcMappingRequest -> {
            if (!bpnEdcMappingRepository.exists(bpnEdcMappingRequest.bpn())) {
                log.warn("Cannot update mapping of bpn {}, therefore will be created", bpnEdcMappingRequest.bpn());
            }
        });
        return bpnEdcMappingRepository.saveAll(bpnEdcMappings);
    }

    public void deleteBpnEdcMapping(String bpn) {
        if (bpnEdcMappingRepository.exists(bpn)) {
            bpnEdcMappingRepository.deleteById(bpn);
            return;
        }
        throw new BpnEdcMappingNotFoundException("Could not find BPN EDC Mapping for BPN " + bpn);
    }

}
