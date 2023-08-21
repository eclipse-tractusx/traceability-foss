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

package assets.response;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Quality types")
public enum QualityTypeResponse {
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

    QualityTypeResponse(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
