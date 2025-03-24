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
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.aas.application.service.AASService;
import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.aas.domain.model.Actor;
import org.eclipse.tractusx.traceability.aas.domain.model.DTR;
import org.eclipse.tractusx.traceability.aas.domain.model.TwinType;
import org.eclipse.tractusx.traceability.aas.domain.repository.AASRepository;
import org.eclipse.tractusx.traceability.common.properties.AASProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AASServiceImpl implements AASService {

    private final AASRepository aasRepository;
    private final AASProperties aasProperties;


    @Override
    public void upsertAASList(DTR dtr) {

        List<String> aasList = dtr.getAasIds();
        log.info("Starting to upsert AAS objects. Total IDs to process: {}", aasList.size());

        List<AAS> existingAASIds = aasRepository.findExistingAasList(aasList);
        log.debug("Existing AAS IDs retrieved: {}", existingAASIds);

        List<AAS> aasForUpdate = new ArrayList<>();
        List<AAS> aasForInsert = new ArrayList<>();

        int ttl = aasProperties.getTtl();
        log.debug("TTL (Time to Live) value loaded from properties: {}", ttl);

        for (String aasId : aasList) {
            log.debug("Processing AAS ID: {}", aasId);

            Optional<AAS> existingAas = existingAASIds.stream()
                    .filter(aas -> aas.getAasId().equals(aasId))
                    .findFirst();

            if (existingAas.isPresent()) {
                AAS updatedAas = existingAas.get();
                updatedAas.setUpdated(LocalDateTime.now());
                aasForUpdate.add(updatedAas);
                log.debug("AAS ID {} is marked for update: {}", aasId, updatedAas);
            } else {
                AAS newAAS = AAS.builder()
                        .aasId(aasId)
                        .ttl(ttl)
                        .actor(Actor.SYSTEM)
                        .created(LocalDateTime.now())
                        .updated(LocalDateTime.now())
                        .digitalTwinType(dtr.getDigitalTwinType())
                        .bpn(AAS.bpnFromString(dtr.getBpn()))
                        .updated(LocalDateTime.now())
                        .build();
                aasForInsert.add(newAAS);
                log.debug("AAS ID {} is marked for creation: {}", aasId, newAAS);
            }
        }

        // Save updated entries
        if (!aasForUpdate.isEmpty()) {
            log.info("Saving {} updated AAS objects.", aasForUpdate.size());
            aasRepository.save(aasForUpdate);
        } else {
            log.info("No AAS objects found for updating.");
        }

        // Save new entries
        if (!aasForInsert.isEmpty()) {
            log.info("Saving {} new AAS objects.", aasForInsert.size());
            aasRepository.save(aasForInsert);
        } else {
            log.info("No new AAS objects found for creation.");
        }

        log.info("Completed upserting AAS objects.");
    }


    @Override
    public void cleanExpiredAASEntries() {
        aasRepository.cleanExpiredEntries();
    }

    @Override
    public List<AAS> findByDigitalTwinType(final TwinType digitalTwinType) {
        return aasRepository.findByDigitalTwinType(digitalTwinType);
    }
}
