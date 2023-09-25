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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SemanticDataModelTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned",
            "urn:bamm:io.catenax.part_as_planned:1.0.0#PartAsPlanned",
            "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned"
    })
    void test_IsAsPlanned(String aspectType) {
        //GIVEN
        SemanticDataModel semanticDataModel = new SemanticDataModel();
        semanticDataModel.setAspectType(aspectType);
        //WHEN
        boolean asPlanned = semanticDataModel.isAsPlanned();
        //THEN
        assertTrue(asPlanned);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "urn:bamm:io.catenax.just_in_sequence_part:1.0.0#JustInSequencePart",
            "urn:bamm:com.catenax.batch:1.0.0#Batch",
            "urn:bamm:io.catenax.batch:1.0.0#Batch",
            "urn:bamm:io.catenax.batch:1.0.2#Batch",
            "urn:samm:io.catenax.batch:2.0.0#Batch",
            "urn:samm:io.catenax.serial_part:1.0.0#SerialPart",
            "urn:bamm:io.catenax.serial_part:1.0.0#SerialPart",
            "urn:bamm:io.catenax.serial_part:1.1.0#SerialPart",
            "urn:bamm:io.catenax.serial_part:1.0.1#SerialPart"})
    void test_IsAsBuilt(String aspectType) {
        //GIVEN
        SemanticDataModel semanticDataModel = new SemanticDataModel();
        semanticDataModel.setAspectType(aspectType);
        //WHEN
        boolean asBuilt = semanticDataModel.isAsBuilt();
        //THEN
        assertTrue(asBuilt);
    }
}
