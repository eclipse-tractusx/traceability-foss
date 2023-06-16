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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BomLifecycle {
    @JsonProperty("asBuilt")
    AS_BUILT("asBuilt"),
    @JsonProperty("asPlanned")
    AS_PLANNED("asPlanned");

    private final String realName;

    BomLifecycle(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }
}
