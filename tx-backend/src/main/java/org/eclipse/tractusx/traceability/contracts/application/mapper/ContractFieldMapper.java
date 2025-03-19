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
package org.eclipse.tractusx.traceability.contracts.application.mapper;

import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ContractFieldMapper extends BaseRequestFieldMapper {
    private static final Map<String, String> SUPPORTED_CONTRACT_FILTER_FIELDS = Map.ofEntries(
            Map.entry("created", "created"),
            Map.entry("id", "id"),
            Map.entry("contractId", "contractAgreementId"),
            Map.entry("contractType", "type")
            );

    @Override
    public Map<String, String> getSupportedFields() {
        return SUPPORTED_CONTRACT_FILTER_FIELDS;
    }
}
