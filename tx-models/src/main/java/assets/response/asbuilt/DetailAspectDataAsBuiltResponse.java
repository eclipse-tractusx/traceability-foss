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

package assets.response.asbuilt;

import assets.response.base.response.DetailAspectDataResponse;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DetailAspectDataAsBuiltResponse implements DetailAspectDataResponse {
    @ApiModelProperty(example = "95657762-59")
    @Size(max = 255)
    private String partId;
    @ApiModelProperty(example = "01697F7-65")
    @Size(max = 255)
    private String customerPartId;
    @ApiModelProperty(example = "Door front-left")
    @Size(max = 255)
    private String nameAtCustomer;
    @ApiModelProperty(example = "DEU")
    @Size(max = 255)
    private String manufacturingCountry;
    @ApiModelProperty(example = "2022-02-04T13:48:54Z")
    @Size(max = 255)
    private String manufacturingDate;
}
