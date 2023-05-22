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

package org.eclipse.tractusx.traceability.assets.application.rest.request;


import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;

@ApiModel(description = "Quality types")
public enum QualityTypeRequest {
    @ApiModelProperty("Ok")
    OK("Ok"),
    @ApiModelProperty("Minor")
    MINOR("Minor"),
    @ApiModelProperty("Major")
    MAJOR("Major"),
    @ApiModelProperty("Critical")
    CRITICAL("Critical"),
    @ApiModelProperty("Life-threatening")
    LIFE_THREATENING("LifeThreatening");

    private final String description;

    QualityTypeRequest(String description) {
        this.description = description;
    }

    public static org.eclipse.tractusx.traceability.assets.application.rest.response.QualityTypeResponse from(final QualityType qualityType) {
        return org.eclipse.tractusx.traceability.assets.application.rest.response.QualityTypeResponse.valueOf(qualityType.name());
    }

    public QualityType toDomain() {
        return QualityType.valueOf(this.name());
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
