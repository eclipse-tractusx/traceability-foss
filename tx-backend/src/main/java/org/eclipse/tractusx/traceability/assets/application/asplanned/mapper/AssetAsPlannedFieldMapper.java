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

package org.eclipse.tractusx.traceability.assets.application.asplanned.mapper;

import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AssetAsPlannedFieldMapper extends BaseRequestFieldMapper {
    private static final Map<String, String> SUPPORTED_ASSETS_AS_PLANNED_FILTER_FIELDS = Map.ofEntries(
            Map.entry("validityPeriodFrom", "validityPeriodFrom"),
            Map.entry("validityPeriodTo", "validityPeriodTo"),
            Map.entry("functionValidUntil", "functionValidUntil"),
            Map.entry("function", "function"),
            Map.entry("functionValidFrom", "functionValidFrom"),
            Map.entry("catenaxSiteId", "catenaxSiteId"),
            Map.entry("id", "id"),
            Map.entry("idShort", "idShort"),
            Map.entry("owner", "owner"),
            Map.entry("classification", "classification"),
            Map.entry("qualityType", "qualityType"),
            Map.entry("manufacturerPartId", "manufacturerPartId"),
            Map.entry("manufacturerName", "manufacturerName"),
            Map.entry("nameAtManufacturer", "nameAtManufacturer"),
            Map.entry("semanticDataModel", "semanticDataModel"),
            Map.entry("semanticModelId", "semanticModelId"),
            Map.entry("businessPartner", "manufacturerId"),
            Map.entry("van", "van"),
            Map.entry("importState", "importState"),
            Map.entry("importNote", "importNote")

    );

    @Override
    protected Map<String, String> getSupportedFields() {
        return SUPPORTED_ASSETS_AS_PLANNED_FILTER_FIELDS;
    }
}
