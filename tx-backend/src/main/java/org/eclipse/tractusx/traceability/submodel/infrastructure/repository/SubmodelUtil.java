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
package org.eclipse.tractusx.traceability.submodel.infrastructure.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class SubmodelUtil {

    public static String enrichWithSubmodelUuid(final ObjectMapper objectMapper, String jsonString) throws Exception {
        String submodelId = UUID.randomUUID().toString();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        if (rootNode instanceof ObjectNode objectNode) {
            // Add the submodelId
            objectNode.put("submodelId", submodelId);
        }

        return objectMapper.writeValueAsString(rootNode);
    }

    public static String getSubmodelId(final ObjectMapper objectMapper, String jsonString) throws Exception {
        JsonNode rootNode = objectMapper.readTree(jsonString);
        JsonNode submodelIdNode = rootNode.get("submodelId");
        return submodelIdNode != null ? submodelIdNode.asText() : null;
    }
}
