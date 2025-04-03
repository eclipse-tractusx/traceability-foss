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
package org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.repository;

import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaAssetAsPlannedRepository extends JpaRepository<AssetAsPlannedEntity, String>, JpaSpecificationExecutor<AssetAsPlannedEntity> {
    List<AssetAsPlannedEntity> findByIdIn(List<String> assetIds);

    @Query("SELECT COUNT(asset) FROM AssetAsPlannedEntity asset WHERE asset.owner = :owner")
    long countAssetsByOwner(@Param("owner") Owner owner);

    List<AssetAsPlannedEntity> findByImportStateIn(ImportState... importState);

    @Query("SELECT a FROM AssetAsPlannedEntity a WHERE a.expirationDate < CURRENT_TIMESTAMP OR a.expirationDate IS NULL")
    List<AssetAsPlannedEntity> findAllExpired();
}
