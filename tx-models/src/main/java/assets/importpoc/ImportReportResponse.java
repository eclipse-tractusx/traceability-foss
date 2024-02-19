/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package assets.importpoc;

import assets.response.base.response.ImportStateResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema
public record ImportReportResponse(
        ImportJob importJob,
        List<ImportedAsset> importedAsset) {

    @Schema
    public record ImportJob(
            @Schema(example = "456a952e-05eb-40dc-a6f2-9c2cb9c1387f")
            String importId,
            @Schema(example = "2099-02-21T21:27:10.734950Z", maxLength = 50)
            String startedOn,
            @Schema(example = "2099-02-21T21:27:10.734950Z", maxLength = 50)
            String completedOn,
            @Schema
            ImportJobStatusResponse importJobStatusResponse) {
    }

    @Schema
    public record ImportedAsset(
            @Schema(example = "urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd}")
            String catenaxId,
            @Schema
            ImportStateResponse importStateResponse,
            @Schema(example = "2099-02-21T21:27:10.734950Z", maxLength = 50)
            String importedOn,
            @Schema(example = "Asset created successfully in transient state.")
            String importMessage) {
    }
}
