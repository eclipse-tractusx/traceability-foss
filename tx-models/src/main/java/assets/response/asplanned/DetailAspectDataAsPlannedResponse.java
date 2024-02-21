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
package assets.response.asplanned;

import assets.response.base.response.DetailAspectDataResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DetailAspectDataAsPlannedResponse implements DetailAspectDataResponse {

    @Schema(example = "2022-09-26T12:43:51.079Z", maxLength = 255)
    @Size(max = 255)
    private String validityPeriodFrom;

    @Schema(example = "20232-07-13T12:00:00.000Z", maxLength = 255)
    @Size(max = 255)
    private String validityPeriodTo;
}
