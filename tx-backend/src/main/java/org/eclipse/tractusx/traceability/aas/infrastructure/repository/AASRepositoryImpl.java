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
package org.eclipse.tractusx.traceability.aas.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.aas.domain.model.TwinType;
import org.eclipse.tractusx.traceability.aas.domain.repository.AASRepository;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.AASEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AASRepositoryImpl implements AASRepository {

    private final JpaAASRepository jpaAASRepository;

    @Override
    public List<AAS> findExistingAasList(List<String> aasIds) {
        return AASEntity.toDomainList(jpaAASRepository.findExistingAasList(aasIds));
    }

    @Override
    public List<AAS> findByDigitalTwinType(final TwinType digitalTwinType) {
        return AASEntity.toDomainList(jpaAASRepository.findByDigitalTwinType(digitalTwinType.name()));
    }

    @Override
    public void save(List<AAS> aasList) {
        List<AASEntity> aasEntities = AASEntity.fromList(aasList);
        jpaAASRepository.saveAll(aasEntities);
    }

    @Override
    public void cleanExpiredEntries() {
        List<AASEntity> expiredEntries = jpaAASRepository.findExpiredEntries();
        if (!expiredEntries.isEmpty()) {
            jpaAASRepository.deleteAll(expiredEntries);
            log.info("Deleted {} expired AAS entries.", expiredEntries.size());
        } else {
            log.info("No expired AAS entries found.");
        }
    }
}
