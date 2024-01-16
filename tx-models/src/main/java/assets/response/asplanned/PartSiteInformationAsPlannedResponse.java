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

import assets.response.base.DetailAspectDataResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartSiteInformationAsPlannedResponse implements DetailAspectDataResponse {
    @ApiModelProperty(example = "2025-02-08T04:30:48.000Z")
    private String functionValidUntil;
    @ApiModelProperty(example = "production")
    private String function;

    @ApiModelProperty(example = "2023-10-13T14:30:45+01:00")
    private String functionValidFrom;
    @ApiModelProperty(example = "urn:uuid:0fed587c-7ab4-4597-9841-1718e9693003")
    private String catenaXSiteId;
}
