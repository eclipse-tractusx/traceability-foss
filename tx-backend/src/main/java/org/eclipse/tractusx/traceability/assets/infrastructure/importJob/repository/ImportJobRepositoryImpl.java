/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.infrastructure.importJob.repository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.ImportJobNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportJob;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportJobStatus;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.repository.ImportJobRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.importJob.model.ImportJobEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;

@RequiredArgsConstructor
@Component
@Slf4j
public class ImportJobRepositoryImpl implements ImportJobRepository {

    private final JpaImportJobRepository importJobRepository;

    @Override
    public ImportJob createJob() {
        log.info("Creating importJob...");
        ImportJobEntity importJob = ImportJobEntity
                .builder()
                .startedOn(Instant.now())
                .importJobStatus(ImportJobStatus.RUNNING)
                .assetsAsBuilt(new ArrayList<>())
                .assetsAsPlanned(new ArrayList<>())
                .build();
        importJobRepository.save(importJob);
        log.info("Successfully created importJob {}", importJob.getId());
        return importJob.toDomain();
    }

    @Override
    public void save(ImportJobEntity importJobEntity) {
        importJobRepository.save(importJobEntity);
    }

    @Override
    public ImportJob getImportJob(String importJobId) {
        try {
            ImportJobEntity importJobEntity = importJobRepository.getReferenceById(importJobId);
            return importJobEntity.toDomain();
        } catch (EntityNotFoundException entityNotFoundException) {
            throw new ImportJobNotFoundException("Could not find import job with id " + importJobId, entityNotFoundException);
        }
    }
}
