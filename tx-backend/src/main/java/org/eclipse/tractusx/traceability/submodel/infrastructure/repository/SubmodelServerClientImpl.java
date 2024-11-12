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

package org.eclipse.tractusx.traceability.submodel.infrastructure.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.submodel.domain.model.SubmodelRequest;
import org.eclipse.tractusx.traceability.submodel.domain.repository.SubmodelServerRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmodelServerClientImpl implements SubmodelServerRepository {

    private final SubmodelClient submodelClient;
    private final ObjectMapper objectMapper;

    @Override
    public String saveSubmodel(String submodel) {
        String submodelId;
        try {
            String submodelEnriched = SubmodelUtil.enrichWithSubmodelUuid(objectMapper, submodel);
            submodelId = SubmodelUtil.getSubmodelId(objectMapper, submodelEnriched);
            Map<String, Object> jsonData = objectMapper.readValue(submodelEnriched, new TypeReference<>() {
            });

            SubmodelRequest submodelRequest =
                    SubmodelRequest
                            .builder()
                            .submodelId(submodelId)
                            .data(jsonData)
                            .build();
            submodelClient.createSubmodel(submodelRequest);
            log.info("Submodel saved with ID: {}", submodelId);
        } catch (Exception e) {
            log.error("Error saving submodel: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return submodelId;

    }

    @Override
    public String getSubmodel(String submodelId) {
        return submodelClient.getSubmodel(submodelId);
    }

}
