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
package org.eclipse.tractusx.traceability.assets.domain.importpoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
class ImportRequestTest {

    @Test
    void testMapper() throws IOException {
        // Specify the path to your JSON file
        String filePath = "src/test/resources/testdata/import-request.json";

        // Read the JSON file into a String
        String jsonString = Files.readString(Path.of(filePath));


        // Parse the JSON string into a JsonNode using Jackson ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        ImportRequest importRequest = objectMapper.readValue(jsonString, ImportRequest.class);

        // Your test logic goes here
        Assertions.assertNotNull(importRequest);
    }


}
