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
package org.eclipse.tractusx.traceability.assets.application.importpoc.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.ListValidationException;
import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.SchemaStore;
import net.jimblackler.jsonschemafriend.ValidationError;
import net.jimblackler.jsonschemafriend.ValidationException;
import net.jimblackler.jsonschemafriend.Validator;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;

@Component
public class JsonFileValidator {

    public static final Map<String, String> SUPPORTED_SCHEMA_VALIDATION = Map.ofEntries(
            Map.entry("base", "/validation/schema_V1.json"),
            Map.entry("urn:samm:io.catenax.batch:2.0.0#Batch", "/validation/Batch_2.0.0-schema.json"),
            Map.entry("urn:bamm:io.catenax.just_in_sequence_part:1.0.0#JustInSequencePart", "/validation/JustInSequencePart_1.0.0-schema.json"),
            Map.entry("urn:bamm:io.catenax.serial_part:1.0.1#SerialPart", "/validation/SerialPart_1.0.1-schema.json"),
            Map.entry("urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned", "/validation/PartSiteInformationAsPlanned_1.0.0-schema.json"),
            Map.entry("urn:bamm:io.catenax.single_level_bom_as_built:2.0.0#SingleLevelBomAsBuilt", "/validation/SingleLevelBomAsBuilt_2.0.0-schema.json"),
            Map.entry("urn:bamm:io.catenax.single_level_usage_as_built:2.0.0#SingleLevelUsageAsBuilt", "/validation/SingleLevelUsageAsBuilt_2.0.0-schema.json"),
            Map.entry("urn:bamm:io.catenax.traction_battery_code:1.0.0#TractionBatteryCode", "/validation/TractionBatteryCode_1.0.0-schema.json"),
            Map.entry("urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned", "/validation/PartAsPlanned_1.0.1-schema.json"),
            Map.entry("urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned", "/validation/SingleLevelBomAsPlanned_2.0.0-schema.json")
    );

    private final SchemaStore schemaStore = new SchemaStore();

    public List<String> isValid(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return List.of();
        }
        String fileName = file.getOriginalFilename();
        if (isNull(fileName)){
            throw new IllegalStateException();
        }
        String[] fileNameSplit = fileName.split("\\.");
        if (!Objects.equals(fileNameSplit[fileNameSplit.length - 1], "json")) {
            return List.of("Supported file is *.json");
        }

        final List<String> errors = new ArrayList<>();
        Validator validator = new Validator();
        try {
            validator.validate(getSchema("base"), file.getInputStream());
        } catch (ListValidationException e) {
            errors.addAll(e.getErrors().stream().map(ValidationError::getMessage).toList());
        } catch (GenerationException | IOException | ValidationException e) {
            throw new IllegalStateException(e);
        }
        errors.addAll(validateAspectPayload(file, validator));
        return errors;
    }

    private List<String> validateAspectPayload(MultipartFile file, Validator validator) {
        List<String> errors = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(file.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        JsonNode assetsNode = rootNode.get("assets");
        for (JsonNode asset : assetsNode) {
            JsonNode submodels = asset.get("submodels");
            for (JsonNode submodel : submodels) {
                JsonNode aspectTypeNode = submodel.get("aspectType");
                if (isNull(aspectTypeNode)) {
                    errors.add("Missing property aspectType");
                    continue;
                }
                String aspectType = aspectTypeNode.asText();
                String payload = submodel.get("payload").toString();

                try {
                    validator.validateJson(getSchema(aspectType), payload);
                } catch (ListValidationException e) {
                    errors.addAll(e.getErrors().stream().map(ValidationError::getMessage).toList());
                } catch (GenerationException | ValidationException e) {
                    throw new IllegalStateException(e);
                } catch (NotSupportedSchemaException e) {
                    errors.add(e.getMessage());
                }

            }
        }

        return errors;
    }


    private Schema getSchema(String schemaName) throws GenerationException {
        String schemaPath = SUPPORTED_SCHEMA_VALIDATION.get(schemaName);
        if (isNull(schemaPath)) {
            throw new NotSupportedSchemaException(schemaName);
        }

        URL url = this.getClass().getResource(schemaPath);
        return schemaStore.loadSchema(url);
    }

}
