/********************************************************************************
 * Copyright (c)  2023 Contributors to the Eclipse Foundation
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.MainAspectAsBuiltRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.MainAspectAsPlannedRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.PartSiteInformationAsPlannedRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.SingleLevelBomAsBuiltRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.SingleLevelBomAsPlannedRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.SingleLevelUsageAsBuiltRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.SingleLevelUsageAsPlannedRequest;


@Slf4j
public class GenericSubmodel {
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            defaultImpl = Void.class,
            property = "aspectType")
    @JsonSubTypes({
            @Type(value = MainAspectAsBuiltRequest.class, names = {
                    "urn:bamm:io.catenax.serial_part:1.0.1#SerialPart",
                    "urn:samm:io.catenax.batch:2.0.0#Batch",
                    "urn:bamm:io.catenax.just_in_sequence_part:1.0.0#JustInSequencePart"
            }),

            @Type(value = MainAspectAsPlannedRequest.class, names = {
                    "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned"
            }),
            @Type(value = PartSiteInformationAsPlannedRequest.class, names = {
                    "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned"
            }),

            @Type(value = DetailAspectDataTractionBatteryCode.class, names = {
                    "urn:bamm:io.catenax.traction_battery_code:1.0.0#TractionBatteryCode"
            }),
            @Type(value = SingleLevelBomAsBuiltRequest.class, names = {
                    "urn:bamm:io.catenax.single_level_bom_as_built:3.0.0#SingleLevelBomAsBuilt"
            }),
            @Type(value = SingleLevelUsageAsBuiltRequest.class, names = {
                    "urn:bamm:io.catenax.single_level_usage_as_built:3.0.0#SingleLevelUsageAsBuilt"
            }),
            @Type(value = SingleLevelUsageAsPlannedRequest.class, names = {
                    "urn:bamm:io.catenax.single_level_usage_as_planned:1.1.0#SingleLevelUsageAsPlanned"
            }),
            @Type(value = SingleLevelBomAsPlannedRequest.class, names = {
                    "urn:bamm:io.catenax.single_level_bom_as_planned:3.0.0#SingleLevelBomAsPlanned"
            })
    })
    private final Object payload;

    private String payloadRaw;

    @JsonProperty("aspectType")
    private String aspectType;

    @JsonCreator
    public GenericSubmodel(@JsonProperty("aspectType") String aspectType, @JsonProperty("payload") Object payload) {
        this.aspectType = aspectType;
        this.payload = payload;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            this.payloadRaw = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            this.payloadRaw = exception.getMessage();
        }
    }

    public Object getPayload() {
        return payload;
    }

    public String getAspectType() {
        return aspectType;
    }

    public String getPayloadRaw() {
        return payloadRaw;
    }

}



