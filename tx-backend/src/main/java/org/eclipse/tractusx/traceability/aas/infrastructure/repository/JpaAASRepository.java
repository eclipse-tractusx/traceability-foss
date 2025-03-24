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

import org.eclipse.tractusx.traceability.aas.infrastructure.model.AASEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAASRepository extends JpaRepository<AASEntity, String> {
    @Query("SELECT a FROM AASEntity a WHERE a.aasId IN :aasIds")
    List<AASEntity> findExistingAasList(@Param("aasIds") List<String> aasIds);

    @Query("SELECT a FROM AASEntity a WHERE a.digitalTwinType = :digitalTwinType")
    List<AASEntity> findByDigitalTwinType(@Param("digitalTwinType") String digitalTwinType);

    List<AASEntity> save(List<AASEntity> aasEntities);

    @Query("SELECT a FROM AASEntity a WHERE a.expiryDate < CURRENT_TIMESTAMP")
    List<AASEntity> findExpiredEntries();
}
