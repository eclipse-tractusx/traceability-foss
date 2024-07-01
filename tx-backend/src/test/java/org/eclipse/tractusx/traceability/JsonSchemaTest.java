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
package org.eclipse.tractusx.traceability;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.application.importpoc.validation.exception.JsonFileProcessingException;
import org.eclipse.tractusx.traceability.assets.application.importpoc.validation.exception.NotSupportedSchemaException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Slf4j
public class JsonSchemaTest {
    public static final Map<String, String> SUPPORTED_SCHEMA_VALIDATION = Map.ofEntries(
            Map.entry("urn:samm:io.catenax.batch:3.0.0#Batch", "/schema/semantichub/Batch_3.0.0-schema.json"),
            Map.entry("urn:samm:io.catenax.just_in_sequence_part:3.0.0#JustInSequencePart", "/schema/semantichub/JustInSequencePart_3.0.0-schema.json"),
            Map.entry("urn:samm:io.catenax.serial_part:3.0.0#SerialPart", "/schema/semantichub/SerialPart_3.0.0-schema.json"),
            Map.entry("urn:samm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned", "/schema/semantichub/PartSiteInformationAsPlanned_1.0.0-schema.json"),
            Map.entry("urn:samm:io.catenax.single_level_bom_as_built:3.0.0#SingleLevelBomAsBuilt", "/schema/semantichub/SingleLevelBomAsBuilt_3.0.0-schema.json"),
            Map.entry("urn:samm:io.catenax.single_level_usage_as_built:3.0.0#SingleLevelUsageAsBuilt", "/schema/semantichub/SingleLevelUsageAsBuilt_3.0.0-schema.json"),
            Map.entry("urn:samm:io.catenax.traction_battery_code:2.0.0#TractionBatteryCode", "/schema/semantichub/TractionBatteryCode_2.0.0-schema.json"),
            Map.entry("urn:samm:io.catenax.part_as_planned:2.0.0#PartAsPlanned", "/schema/semantichub/PartAsPlanned_2.0.0-schema.json"),
            Map.entry("urn:samm:io.catenax.single_level_bom_as_planned:3.0.0#SingleLevelBomAsPlanned", "/schema/semantichub/SingleLevelBomAsPlanned_3.0.0-schema.json")
    );
    private final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

    @Test
    public void testLatestJsonSchema() {
        try {
            String highestVersionFile = findHighestVersionFile();
            log.info("File Version: " + highestVersionFile);
            InputStream file = JsonSchemaTest.class.getResourceAsStream("/testdata/jsonfiles/" + highestVersionFile);

            MockMultipartFile multipartFile = new MockMultipartFile(
                    "file",                 // Parameter name in the multipart request
                    "import-request.json",  // Original file name
                    "application/json",     // Content type
                    file
            );

            List<String> errors = isValid(multipartFile);
            for (String string : errors) {
                log.info(string);
            }

            assertEquals(1, errors.size());
        } catch (Exception e) {
            log.error("Exception encountered", e);
            fail("Exception encountered: " + e.getMessage());
        }

    }

    private String findHighestVersionFile() throws URISyntaxException, IOException {
        Path dir = Path.of(JsonSchemaTest.class.getResource("/testdata/jsonfiles").toURI());
        Pattern pattern = Pattern.compile("CX_Testdata_MessagingTest_v(\\d+\\.\\d+\\.\\d+)\\.json");

        String highestVersionFile = null;
        String highestVersion = null;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
            for (Path entry : stream) {
                Matcher matcher = pattern.matcher(entry.getFileName().toString());
                if (matcher.matches()) {
                    String version = matcher.group(1);
                    if (highestVersion == null || compareVersions(version, highestVersion) > 0) {
                        highestVersion = version;
                        highestVersionFile = entry.getFileName().toString();
                    }
                }
            }
        }
        return highestVersionFile;
    }

    private int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        try {
            for (int i = 0; i < parts1.length; i++) {
                int part1 = Integer.parseInt(parts1[i]);
                int part2 = Integer.parseInt(parts2[i]);

                if (part1 != part2) {
                    return Integer.compare(part1, part2);
                }
            }
        } catch (NumberFormatException e) {
            log.info("Error:" + e);
        }

        return 0;
    }
    public List<String> isValid(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return List.of();
        }
        String fileName = file.getOriginalFilename();
        if (isNull(fileName)) {
            throw new IllegalStateException();
        }
        String[] fileNameSplit = fileName.split("\\.");
        if (!Objects.equals(fileNameSplit[fileNameSplit.length - 1], "json")) {
            return List.of("Supported file is *.json");
        }


        final List<String> errors = new ArrayList<>();

        errors.addAll(validateAspectPayload(file));
        return errors;
    }

    private List<String> validateAspectPayload(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(file.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        JsonNode assetsNode = rootNode.get("submodels");
        if (isNull(assetsNode)) {
            errors.add("Could not find submodels");
            return errors;
        }
        Map<String, List<ProcessingMessage>> processingMessages = new HashMap<>();
        for (JsonNode asset : assetsNode) {
            String assetId = asset.get("catenaXId").asText();


            Map<String, JsonNode> map = SUPPORTED_SCHEMA_VALIDATION.keySet()
                    .stream()
                    .filter(key -> !isNull(asset.get(key)))
                    .map(key -> new AbstractMap.SimpleEntry(key, asset.get(key))
                    )
                    .collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (JsonNode) entry.getValue()));

            for (Map.Entry<String, JsonNode> submodel : map.entrySet()) {

                try {
                    final JsonSchema schema = factory.getJsonSchema(JsonLoader.fromURL(getSchemaUrl(submodel.getKey())));

                    submodel.getValue().forEach(entry -> {
                        ProcessingReport report = null;
                        try {
                            report = schema.validate(JsonLoader.fromString(entry.toPrettyString()));
                        } catch (ProcessingException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        List<ProcessingMessage> payloadProcessingMessages = StreamSupport.stream(report.spliterator(), false)
                                .filter(processingMessage -> !processingMessage.getLogLevel().equals(LogLevel.WARNING))
                                .filter(processingMessage -> !processingMessage.getLogLevel().equals(LogLevel.INFO))
                                .toList();
                        processingMessages.put("For Asset with ID: " + assetId + " And aspectType: " + submodel.getKey(), payloadProcessingMessages);
                    });


                } catch (NotSupportedSchemaException e) {
                    errors.add(e.getMessage());
                } catch (ProcessingException | IOException e) {
                    throw new JsonFileProcessingException(e);
                }

            }
        }

        List<String> constructedErrorMessages = processingMessages.entrySet().stream().map(entry ->
                        entry.getValue().stream().map(processingMessage -> entry.getKey() + " Following error occurred: " + processingMessage.getMessage()).toList()
                ).flatMap(Collection::stream)
                .toList();
        errors.addAll(constructedErrorMessages);
        return errors;
    }

    private URL getSchemaUrl(String schemaName) {
        String schemaPath = SUPPORTED_SCHEMA_VALIDATION.get(schemaName);
        if (isNull(schemaPath)) {
            throw new NotSupportedSchemaException(schemaName);
        }

        return org.eclipse.tractusx.traceability.assets.application.importpoc.validation.JsonFileValidator.class.getResource(schemaPath);
    }

}


