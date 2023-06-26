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

package org.eclipse.tractusx.traceability.assets.application.rest.response;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;

public record DescriptionsResponse(
        @ApiModelProperty(example = "urn:uuid:a4a26b9c-9460-4cc5-8645-85916b86adb0")
        @Size(max = 255)
        String id,
        @ApiModelProperty(example = "null")
        @Size(max = 255)
        String idShort) {

    public static DescriptionsResponse from(final Descriptions descriptions) {
        return new DescriptionsResponse(
                descriptions.id(),
                descriptions.idShort()
        );
    }
}
