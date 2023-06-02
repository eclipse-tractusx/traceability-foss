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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model.response.semanticdatamodel;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum LocalIdKey {
    @JsonProperty("manufacturerId") MANUFACTURER_ID,
    @JsonProperty("manufacturerPartId") MANUFACTURER_PART_ID,
    @JsonProperty("partInstanceId") PART_INSTANCE_ID,
    @JsonProperty("batchId") BATCH_ID,
    @JsonEnumDefaultValue UNKNOWN,
    @JsonProperty("van") VAN,
}
