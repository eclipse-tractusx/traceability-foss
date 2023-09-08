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
package assets.response.base;

public enum DetailAspectTypeResponse {
    AS_BUILT("AS_BUILT"),
    AS_PLANNED("AS_PLANNED"),
    /* Detail aspect of as built assets */
    TRACTION_BATTERY_CODE("TRACTION_BATTERY_CODE"),
    /* Downward relation of as planned assets */
    SINGLE_LEVEL_BOM_AS_BUILT("SINGLE_LEVEL_BOM_AS_BUILT"),

    /* Upward relation of as planned assets */
    SINGLE_LEVEL_USAGE_AS_BUILT("SINGLE_LEVEL_USAGE_AS_BUILT"),

    /* Downward relation of as planned assets */
    SINGLE_LEVEL_BOM_AS_PLANNED("SINGLE_LEVEL_BOM_AS_PLANNED"),
    /* Detail aspect of as planned assets */
    PART_SITE_INFORMATION_AS_PLANNED("PART_SITE_INFORMATION_AS_PLANNED");

    final String realName;

    DetailAspectTypeResponse(final String realName) {
        this.realName = realName;
    }
}
