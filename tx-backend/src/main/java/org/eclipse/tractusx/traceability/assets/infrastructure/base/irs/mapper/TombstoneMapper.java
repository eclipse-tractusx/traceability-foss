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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.Tombstone;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;

import java.util.List;

@Slf4j
public class TombstoneMapper {

    public static List<AssetBase> mapTombstones(JobStatus jobstatus, List<Tombstone> tombstones, ObjectMapper objectMapper) {
        return tombstones.stream()
                .map(tombstone -> {
                    if (isOwnPart(tombstone.getCatenaXId(), jobstatus)) {
                        return mapOwnPartsTombstone(jobstatus, tombstone, objectMapper);
                    } else {
                        return mapOtherPartsTombstone(jobstatus, tombstone, objectMapper);
                    }
                }).toList();

    }

    private static AssetBase mapOwnPartsTombstone(JobStatus jobstatus, Tombstone tombstone, ObjectMapper objectMapper) {
        String tombstoneString = "";
        try {
            tombstoneString = objectMapper.writeValueAsString(tombstone);
        } catch (JsonProcessingException e) {
            log.error("Could not process tombstone from IRS", e);
        }
        return AssetBase.builder()
                .id(tombstone.getCatenaXId())
                .tombstone(tombstoneString)
                .owner(Owner.OWN)
                .semanticDataModel(getSemanticDataModelFrom(jobstatus))
                .importState(ImportState.ERROR)
                .build();
    }

    private static SemanticDataModel getSemanticDataModelFrom(JobStatus jobstatus) {
        return jobstatus.parameter().bomLifecycle().equals(BomLifecycle.AS_BUILT.getRealName()) ? SemanticDataModel.TOMBSTONEASBUILT : SemanticDataModel.TOMBSTONEASPLANNED;
    }

    private static AssetBase mapOtherPartsTombstone(JobStatus jobstatus, Tombstone tombstone, ObjectMapper objectMapper) {
        String tombstoneString = "";
        try {
            tombstoneString = objectMapper.writeValueAsString(tombstone);
        } catch (JsonProcessingException e) {
            log.error("Could not process tombstone from IRS", e);
        }
        return AssetBase.builder()
                .id(tombstone.getCatenaXId())
                .tombstone(tombstoneString)
                .owner(getOwnerFrom(jobstatus))
                .semanticDataModel(getSemanticDataModelFrom(jobstatus))
                .importState(ImportState.ERROR)
                .build();
    }

    private static boolean isOwnPart(String catenaxId, JobStatus jobStatus) {
        return catenaxId.equals(jobStatus.globalAssetId());
    }

    private static Owner getOwnerFrom(JobStatus jobStatus) {
        return jobStatus.parameter().direction().equalsIgnoreCase(Direction.DOWNWARD.name()) ? Owner.SUPPLIER : Owner.CUSTOMER;
    }

}
