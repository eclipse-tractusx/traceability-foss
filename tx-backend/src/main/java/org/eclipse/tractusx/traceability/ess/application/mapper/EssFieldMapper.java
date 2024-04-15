/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.eclipse.tractusx.traceability.ess.application.mapper;

import java.util.Map;
import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.springframework.stereotype.Component;

@Component
public class EssFieldMapper extends BaseRequestFieldMapper  {

    private static final Map<String, String> SUPPORTED_ESS_FILTER_FIELDS = Map.ofEntries(
            Map.entry("id","id"),
            Map.entry("manufacturerPartId","manufacturerPartId"),
            Map.entry("nameAtManufacturer","nameAtManufacturer"),
            Map.entry("bpn","bpn"),
            Map.entry("companyName","companyName"),
            Map.entry("status","status"),
            Map.entry("impacted","impacted"),
            Map.entry("created","created"),
            Map.entry("updated","updated")
    );

    @Override
    protected Map<String, String> getSupportedFields() {
        return SUPPORTED_ESS_FILTER_FIELDS;
    }

}
