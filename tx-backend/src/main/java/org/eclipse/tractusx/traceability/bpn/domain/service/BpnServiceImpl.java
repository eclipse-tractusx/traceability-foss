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

package org.eclipse.tractusx.traceability.bpn.domain.service;

import bpn.request.BpnMappingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnEdcMapping;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnNotFoundException;
import org.eclipse.tractusx.traceability.bpn.infrastructure.client.BpdmEdcClient;
import org.eclipse.tractusx.traceability.bpn.infrastructure.model.BpnEntity;
import org.eclipse.tractusx.traceability.bpn.infrastructure.model.BusinessPartnerResponse;
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BpnServiceImpl implements BpnService {

    private final BpnRepository bpnRepository;
    private final BpdmEdcClient bpdmEdcClient;

    @Override
    public String findByBpn(String bpn) {

        if (StringUtils.isEmpty(bpn)) {
            return null;
        }

        try {
            BusinessPartnerResponse businessPartner = bpdmEdcClient.getBusinessPartnerLegalName(bpn);
            BpnEntity bpnEntity = bpnRepository.save(businessPartner);
            return bpnEntity.getManufacturerName();
        } catch (Exception e) {
            log.error("Error resolving business partner data: {}", e.getMessage());
            return null;
        }
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
