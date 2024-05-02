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

package org.eclipse.tractusx.traceability.bpn.infrastructure.repository;

import bpn.request.BpnMappingRequest;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnEdcMapping;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnNotFoundException;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnService;
import org.eclipse.tractusx.traceability.bpn.infrastructure.client.BpdmClient;
import org.eclipse.tractusx.traceability.bpn.infrastructure.model.BpnEntity;
import org.eclipse.tractusx.traceability.bpn.infrastructure.model.BusinessPartnerResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BpnServiceImpl implements BpnService {

    private final BpnRepository bpnRepository;
    private final BpdmClient bpdmClient;

    public BpnServiceImpl(BpnRepository bpnRepository, BpdmClient bpdmClient) {
        this.bpnRepository = bpnRepository;
        this.bpdmClient = bpdmClient;
    }

    @Override
    public String findByBpn(String bpn) {
        String manufacturerName = bpnRepository.findManufacturerName(bpn);
        if (manufacturerName == null && bpn != null) {
            BusinessPartnerResponse businessPartner = bpdmClient.getBusinessPartner(bpn);
            BpnEntity bpnEntity = bpnRepository.save(businessPartner);
            manufacturerName = bpnEntity.getManufacturerName();
        }
        return manufacturerName;
    }

    @Override
    public List<BpnEdcMapping> findAllBpnMappings() {
        return bpnRepository.findAllWhereUrlNotNull();
    }

    @Override
    public List<BpnEdcMapping> saveAllBpnEdcMappings(List<BpnMappingRequest> bpnEdcMappings) {
        return bpnRepository.saveAll(bpnEdcMappings);
    }

    @Override
    public List<BpnEdcMapping> updateAllBpnMappings(List<BpnMappingRequest> bpnEdcMappings) {
        bpnEdcMappings.forEach(bpnEdcMappingRequest -> {
            if (!bpnRepository.existsWhereUrlNotNull(bpnEdcMappingRequest.bpn())) {
                log.warn("Cannot update mapping of bpn {}, therefore will be created", bpnEdcMappingRequest.bpn());
            }
        });
        return bpnRepository.saveAll(bpnEdcMappings);
    }

    @Override
    public void deleteBpnMapping(String bpn) {
        if (bpnRepository.existsWhereUrlNotNull(bpn)) {
            bpnRepository.deleteById(bpn);
            return;
        }
        throw new BpnNotFoundException("Could not find BPN EDC Mapping for BPN " + bpn);
    }

}
