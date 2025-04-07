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
import org.eclipse.tractusx.traceability.aas.application.service.DTRService;
import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.aas.domain.model.Actor;
import org.eclipse.tractusx.traceability.aas.domain.model.DTR;
import org.eclipse.tractusx.traceability.aas.domain.model.TwinType;
import org.eclipse.tractusx.traceability.aas.domain.repository.AASRepository;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class AASServiceImpl implements AASService {

    private final AASRepository aasRepository;
    private final DTRService dtrService;

    @Override
    public void aasCleanup() {
        aasRepository.cleanExpiredEntries();
    }

    @Override
    public void aasLookup(final TriggerConfiguration triggerConfiguration) {
        aasLookupByType(TwinType.PART_TYPE, triggerConfiguration);
        aasLookupByType(TwinType.PART_INSTANCE, triggerConfiguration);
    }

    private void aasLookupByType(TwinType twinType, TriggerConfiguration latestTriggerConfiguration) {
        DTR result = queryAndStoreAAS(null, twinType, latestTriggerConfiguration);
        if (result == null) {
            return;
        }
        String cursor = result.getNextCursor();
        while (cursor != null) {
            DTR lookupDTRs = queryAndStoreAAS(cursor, twinType, latestTriggerConfiguration);
            cursor = lookupDTRs.getNextCursor();
        }
    }

    private DTR queryAndStoreAAS(final String nextCursor, final TwinType digitalTwinType, final TriggerConfiguration triggerConfiguration) {
        log.info("Processing AAS Lookup with cursor: {} and limit: {}", nextCursor, triggerConfiguration.getAasLimit());
        DTR dtr;

        try {
            dtr = dtrService.lookupAASShells(digitalTwinType, nextCursor, triggerConfiguration.getAasLimit());
        } catch (Exception e) {
            log.error("Failed to query AAS shells for cursor: {}", nextCursor, e);
            return null;
        }
        upsertAASList(dtr, triggerConfiguration.getAasTTL());
        return dtr;
    }


    protected void upsertAASList(final DTR dtr, int aas_ttl) {

        List<String> aasList = dtr.getAasIds();
        log.info("Starting to upsert AAS objects. Total IDs to process: {}", aasList.size());

        List<AAS> existingAASIds = aasRepository.findExistingAasList(aasList);
        log.debug("Existing AAS IDs retrieved: {}", existingAASIds);

        List<AAS> aasForUpdate = new ArrayList<>();
        List<AAS> aasForInsert = new ArrayList<>();


        log.debug("TTL (Time to Live) value loaded from properties: {}", aas_ttl);

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
                        .ttl(aas_ttl)
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


}
