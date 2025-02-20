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
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.irs.component.Tombstone;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobStatus;

import java.util.List;
import java.util.Objects;

@Slf4j
public class TombstoneMapper {

    public static List<AssetBase> mapTombstones(JobStatus jobstatus, List<Tombstone> tombstones, ObjectMapper objectMapper, String applicationBpn) {
        return tombstones.stream()
                .map(tombstone -> mapTombstone(jobstatus, tombstone, objectMapper, determineOwner(tombstone.getBusinessPartnerNumber(), applicationBpn, jobstatus)))
                .toList();
    }

    private static AssetBase mapTombstone(JobStatus jobstatus, Tombstone tombstone, ObjectMapper objectMapper, Owner owner) {
        String tombstoneString = "";
        try {
            tombstoneString = objectMapper.writeValueAsString(tombstone);
        } catch (JsonProcessingException e) {
            log.error("Could not process tombstone from IRS", e);
        }

        return AssetBase.builder()
                .id(tombstone.getCatenaXId())
                .tombstone(tombstoneString)
                .owner(owner)
                .semanticDataModel(getSemanticDataModelFrom(jobstatus))
                .importState(ImportState.ERROR)
                .build();
    }

    private static Owner determineOwner(String tombstoneBpn, String applicationBpn, JobStatus jobStatus) {
        if (StringUtils.isBlank(tombstoneBpn)) {
            return Owner.UNKNOWN;
        }
        if (Objects.equals(tombstoneBpn, applicationBpn)) {
            return Owner.OWN;
        }
        return jobStatus.parameter().direction().equalsIgnoreCase(Direction.DOWNWARD.name())
                ? Owner.SUPPLIER
                : Owner.CUSTOMER;
    }

    private static SemanticDataModel getSemanticDataModelFrom(JobStatus jobstatus) {
        return jobstatus.parameter().bomLifecycle().equals(BomLifecycle.AS_BUILT.getRealName())
                ? SemanticDataModel.TOMBSTONEASBUILT
                : SemanticDataModel.TOMBSTONEASPLANNED;
    }
}
