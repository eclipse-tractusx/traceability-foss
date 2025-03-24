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
package org.eclipse.tractusx.traceability.aas.application.cron;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.aas.application.service.AASService;
import org.eclipse.tractusx.traceability.aas.application.service.DTRService;
import org.eclipse.tractusx.traceability.aas.domain.model.DTR;
import org.eclipse.tractusx.traceability.aas.domain.model.TwinType;
import org.eclipse.tractusx.traceability.common.config.ApplicationProfiles;
import org.eclipse.tractusx.traceability.common.properties.AASProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile(ApplicationProfiles.NOT_TESTS)
@RequiredArgsConstructor
public class AASLookup {
    private final AASService aasService;
    private final DTRService dtrService;
    private final AASProperties aasProperties;

    @Scheduled(cron = "${aas.dtr-lookup-cron-expression}", zone = "${aas.cron-zone}")
    public void automaticDTRLookup() throws RegistryServiceException {
        aasLookupByType(TwinType.PART_TYPE);
        aasLookupByType(TwinType.PART_INSTANCE);
    }

    public void aasLookupByType(TwinType digitalTwinType) throws RegistryServiceException {
        DTR result = queryAndStoreAAS(null, digitalTwinType);
        String cursor = result.getNextCursor();
        while (cursor != null) {
            DTR lookupDTRs = queryAndStoreAAS(cursor, digitalTwinType);
            cursor = lookupDTRs.getNextCursor();
        }
    }

    private DTR queryAndStoreAAS(String nextCursor, TwinType digitalTwinType) throws RegistryServiceException {
        log.info("Processing AAS Lookup with cursor: {} and limit: {}", nextCursor, aasProperties.getLimit());
        DTR dtr;

        try {
            dtr = dtrService.lookupAASShells(digitalTwinType, nextCursor, aasProperties.getLimit());
        } catch (Exception e) {
            log.error("Failed to query AAS shells for cursor: {}", nextCursor, e);
            throw e;
        }
        aasService.upsertAASList(dtr);
        return dtr;
    }

}
