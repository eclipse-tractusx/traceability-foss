/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;

class Submodel {
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            defaultImpl = Void.class,
            property = "aspectType")
    @JsonSubTypes({
            @Type(value = SemanticDataModel.class, names = {
                    "urn:samm:io.catenax.serial_part:1.0.0#SerialPart",
                    "urn:bamm:io.catenax.serial_part:1.0.0#SerialPart",
                    "urn:bamm:io.catenax.serial_part:1.1.0#SerialPart",
                    "urn:bamm:io.catenax.serial_part:1.0.1#SerialPart"

            }),
            @Type(value = SemanticDataModel.class, names = {
                    "urn:bamm:com.catenax.batch:1.0.0#Batch",
                    "urn:bamm:io.catenax.batch:1.0.0#Batch",
                    "urn:bamm:io.catenax.batch:1.0.2#Batch",
                    "urn:samm:io.catenax.batch:2.0.0#Batch"
            }),
            @Type(value = SemanticDataModel.class, names = {
                    "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned",
                    "urn:bamm:io.catenax.part_as_planned:1.0.0#PartAsPlanned"
            }),
            @Type(value = SemanticDataModel.class, names = {
                    "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned"
            }),
            @Type(value = SemanticDataModel.class, names = {
                    "urn:bamm:io.catenax.just_in_sequence_part:1.0.0#JustInSequencePart"
            }),
            @Type(value = DetailAspectDataTractionBatteryCode.class, names = {
                    "urn:bamm:io.catenax.traction_battery_code:1.0.0#TractionBatteryCode"
            })
    })
    private Object payload;

    @JsonProperty("aspectType")
    private String aspectType;

    @JsonProperty("identification")
    private String identification;

    @JsonCreator
    public Submodel(@JsonProperty("aspectType") String aspectType, @JsonProperty("payload") Object payload) {
        this.aspectType = aspectType;
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }

    public String getAspectType() {
        return aspectType;
    }

    public String getIdentification() {
        return identification;
    }
}



