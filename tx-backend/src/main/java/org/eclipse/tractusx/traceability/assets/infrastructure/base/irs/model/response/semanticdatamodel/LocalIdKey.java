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

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public enum LocalIdKey {
    @JsonProperty("manufacturerId") MANUFACTURER_ID("manufacturerId"),
    @JsonProperty("manufacturerPartId") MANUFACTURER_PART_ID("manufacturerPartId"),
    @JsonProperty("partInstanceId") PART_INSTANCE_ID("partInstanceId"),
    @JsonProperty("digitalTwinType") DIGITAL_TWIN_TYPE("digitalTwinType"),
    @JsonProperty("batchId") BATCH_ID("batchId"),
    @JsonEnumDefaultValue UNKNOWN("unknown"),
    @JsonProperty("van") VAN("van"),
    @JsonProperty("jisNumber") JIS_NUMBER("jisNumber");

    private final String value;

    LocalIdKey(String value) {
        this.value = value;
    }

}
