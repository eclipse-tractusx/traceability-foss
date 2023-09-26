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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnEdcMapping;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnNotFoundException;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.eclipse.tractusx.traceability.bpn.infrastructure.model.BpnEntity;
import org.eclipse.tractusx.traceability.bpn.infrastructure.rest.BpnMappingRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersistentBpnRepository implements BpnRepository {

    private final JpaBpnRepository repository;

    @Override
    public BpnEdcMapping findByIdOrThrowNotFoundException(String bpn) {
        return repository.findById(bpn)
                .map(this::toDTO)
                .orElseThrow(() -> new BpnNotFoundException("EDC URL mapping with BPN %s was not found."
                        .formatted(bpn)));
    }

    @Override
    public boolean existsWhereUrlNotNull(String manufacturerId) {
        return repository.existsByManufacturerIdAndUrlIsNotNull(manufacturerId);
    }

    @Override
    public List<BpnEdcMapping> findAllWhereUrlNotNull() {
        return repository.findAllByUrlNotNull().stream().map(this::toDTO).toList();
    }

    @Override
    public void deleteById(String bpn) {
        repository.deleteById(bpn);
    }

    @Override
    public Optional<String> findManufacturerName(String manufacturerId) {
        return repository.findById(manufacturerId).map(BpnEntity::getManufacturerName);
    }

    @Override
    public void updateManufacturers(Map<String, String> bpns) {
        List<BpnEntity> entities = bpns.entrySet().stream()
                .map(entry -> BpnEntity.builder()
                        .manufacturerId(entry.getKey())
                        .manufacturerName(entry.getValue())
                        .build())
                .toList();
        try {
            entities.forEach(bpnEntity -> repository.findById(bpnEntity.getManufacturerId()).ifPresentOrElse(persistedBpnEntity -> {
                persistedBpnEntity.setManufacturerName(bpnEntity.getManufacturerName());
                repository.save(persistedBpnEntity);
            }, () -> repository.save(bpnEntity)));
        } catch (Exception e) {
            log.warn("Exception in bpn mapping storage", e);
        }
    }

    @Override
    public List<BpnEdcMapping> saveAll(List<BpnMappingRequest> bpnEdcMappings) {
        List<BpnEntity> bpnEdcMappingEntities = bpnEdcMappings.stream().map(this::toEntity).toList();
        return repository.saveAll(bpnEdcMappingEntities).stream().map(this::toDTO).toList();
    }

    private BpnEdcMapping toDTO(BpnEntity entity) {
        return new BpnEdcMapping(
                entity.getManufacturerId(),
                entity.getUrl()
        );
    }

    private BpnEntity toEntity(BpnMappingRequest bpnEdcMappings) {
        return BpnEntity.builder()
                .manufacturerId(bpnEdcMappings.bpn())
                .url(bpnEdcMappings.url())
                .build();
    }

}
